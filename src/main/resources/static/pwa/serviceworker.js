const sCacheName = 'pwa-push'; // 캐시제목 선언
const aFilesToCache = [ // 캐시할 파일 선언
  '/pwa/manifest.json',
  '/pwa/styles/index.css',
  '/pwa/index.html',
  '/pwa/images/hello-pwa.png'
];


//1서비스워커를 설치하고 캐시를 저장함
self.addEventListener('install', pEvent => {
  console.log('서비스워커 설치함!');
  pEvent.waitUntil(
    caches.open(sCacheName)
    .then(pCache => {
      console.log('파일을 캐시에 저장함!');
      return pCache.addAll(aFilesToCache);
    })
  );
});


// 2. 고유번호 할당받은 서비스 워커 동작 시작
self.addEventListener('activate', pEvent => {
  console.log('서비스워커 동작 시작됨!');
});

// 3.데이터 요청시 네트워크 또는 캐시에서 찾아 반환 
self.addEventListener('fetch', pEvent => {
  pEvent.respondWith(
    caches.match(pEvent.request)
    .then(response => {
      if (!response) {
        console.log("네트워크에서 데이터 요청!", pEvent.request)
        return fetch(pEvent.request);
      }
      console.log("캐시에서 데이터 요청!", pEvent.request)
      return response;
    }).catch(err => console.log(err))
  );
});


self.addEventListener("push", event => {
  console.log('[Service Worker] Push Received.');
  console.log(`[Service Worker] Push had this data==>: "${event.data}"`);
  //console.log(`[Service Worker] Push had this data: "${event.data.text()}"`);
  /* 자바에 URLEncoder 한것을 자바스크립트도 같은 타입으로 변환처리 */
  function urldecode(str) {
    return decodeURIComponent((str + '')
        .replace(/%(?![\da-f]{2})/gi, function() {
            return '%25';
        })
        .replace(/\+/g, '%20'));
   }

  const payload = event.data.json();
  const title =  urldecode(atob(payload.title));         //제목 베이스64 복호화
  
  const options = {  
     actions: payload.actions,
	 body: urldecode(atob(payload.body)),              //내용
     requireInteraction: payload.requireInteraction,      //닫기버튼 눌러줄때까지 창이 안닫힌다.
	 icon: payload.icon,
    //tag: 'message-group-1',          //태그는 새로운 알림이 뜰때 이전 알림의 태그 값을 참고하여 이전 알림은 지우고,새로운 알림을 띄우게 하는 역할을 합니다.
     // icon: "http://image.ibb.co/frYOFd/tmlogo.png",
	 badge: payload.badge,       //모바일기기에서 상단 status바에 뜰 소형 아이콘
	 vibrate: payload.vibrate,   //모바일기기에서 진동 
    //sound: '/demos/notification-examples/audio/notification-sound.mp3',  //모바일기기에사만 소리
     image: payload.image,  
	 data : payload.params                           //notification 다음 행동때의 파라미터 
	};

  event.waitUntil( self.registration.showNotification(title, options) );  //위에서 설정한 값을 가지고 실제로 notification을 나타나게 해주는 코드

});


//notification을 사용자가 클릭시의 리스너
self.addEventListener('notificationclick', function(event) {
  //푸시 노티피케이션 에서 클릭 리스너
  console.log('[Service Worker] Notification click Received.',event.notification)

  var data = event.notification.data    
  event.notification.close()                          //현재 notification을 닫는다.

  console.log('event.action',event.action);
  
  if(event.action === 'close') return
  
  let urlToOpen = "";
  if(event.action === 'naver') {
	 urlToOpen = new URL("https://www.naver.com", self.location.origin).href;
  } else if(data.url !== undefined) {
	urlToOpen = new URL(data.url, self.location.origin).href;
  } else {
	return
  } 
  

  // 알람클릭시 이미 열려있는 창에 집중시키기
  //제목 그대로, 클릭 했을 때 만약 이미 해당 사이트가 열려 있으면 그쪽으로 사용자 시선을 집중시키는 속성입니다. 위 방법보다는 조금 더 완성도 있게 알람을 구현할 수 있죠  
  // new URL() : url이 products/10 이런식이면 http://products/10 와 같이 바꿔줍니다.
	
 var promiseChain = clients.matchAll({ // matchAll() 은 탭만 반환하고, 웹 워커는 제외합니다.
   type: 'window',
   includeUncontrolled: true // 현재 서비스워커 이외의 다른 서비스워커가 제어하는 탭들도 포함합니다. 그냥 default로 항상 넣어주세요.
 })
 .then((windowClients) => {
  // windowClients 는 현재 열린 탭들의 값입니다.
  var matchingClient = null;

  for (var i = 0; i < windowClients.length; i++) {
    var windowClient = windowClients[i];
    if (windowClient.url === urlToOpen) {
      matchingClient = windowClient;
      break;
    }
  }

  if (matchingClient) {
    return matchingClient.focus();
  } else {
    return clients.openWindow(urlToOpen);
  }
});

// promiseChain은 위 matchingClient.focus()의 실행이 끝난 후 waitUntil()을 수행하기 위한 프로미스 체인입니다.
event.waitUntil(promiseChain);
  
  //event.waitUntil( clients.openWindow( data.url ) )   //클라이언트(브라우저)새창은 연다
  //clients.openWindow('https://developers.google.com/web/')
});