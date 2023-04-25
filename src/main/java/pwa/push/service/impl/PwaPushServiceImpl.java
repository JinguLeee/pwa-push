package pwa.push.service.impl;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jose4j.base64url.Base64;
import org.jose4j.json.internal.json_simple.JSONObject;
import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.stereotype.Service;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import pwa.push.model.UserSubscriInfo;
import pwa.push.service.PwaPushService;



@Service("pwaPushService")
public class PwaPushServiceImpl implements PwaPushService {
	private Logger logger = LoggerFactory.getLogger(PwaPushServiceImpl.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void sendMessage(Map<String, Object> messageMap, List<UserSubscriInfo> userSubscriInfo) throws GeneralSecurityException, InterruptedException, JoseException, ExecutionException, IOException {

		// 컨테이너 생성
		GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
	   // 환경변수 관리 객체 생성
		ConfigurableEnvironment env = ctx.getEnvironment();
		// 프로퍼티 관리 객체 생성
		MutablePropertySources prop = env.getPropertySources();
		// 프로퍼티 관리 객체에 프로퍼티 파일 추가
		prop.addLast(new ResourcePropertySource("classpath:pwa.properties"));
		// 프로퍼티 정보 얻기
		String publicKey = env.getProperty("publicKey");
		String privateKey = env.getProperty("privateKey");
				
				
		logger.debug("publicKey=======>"+ publicKey);
		logger.debug("privateKey=======>"+ privateKey);
		
		logger.debug("sendMessage=======>"+ messageMap);
		logger.debug("userSubscriInfo=======>"+ userSubscriInfo);
		
		//String publicKey = "BNzzfdcBcThU27FcGve6F3GF6He2Fro82ZMuOLga9fukatLMlaKB6GdO-82loi6W4iGdPQZAp_4HLgST8z5of_E";
		//String privateKey = "yzZ8xvvhiM50HlTsDLCwiofkCyOypb-ZTkqdvpwyz7c";
		
		
		/* 화면에 출력할 내용 셋팅 인자로 넘어온 값을 셋팅해준다. */
		JSONObject jsonObject = new JSONObject();

        jsonObject.put("title", Base64.encode(URLEncoder.encode(messageMap.get("title").toString(),"UTF-8").getBytes()) );
        jsonObject.put("body", Base64.encode(URLEncoder.encode(messageMap.get("body").toString(),"UTF-8").getBytes()));
        jsonObject.put("icon", messageMap.get("icon"));  //나타날이미지
        jsonObject.put("badge", messageMap.get("badge"));
        jsonObject.put("vibrate", messageMap.get("vibrate"));
        jsonObject.put("params", messageMap.get("params"));
        jsonObject.put("image", messageMap.get("image"));
        jsonObject.put("requireInteraction", messageMap.get("requireInteraction"));
        jsonObject.put("actions", messageMap.get("actions"));
        
		String payload = jsonObject.toString();
		
		logger.debug("payload=======>"+ payload);
		
		PushSubscription pushSubscription = new PushSubscription();
		Notification notification;
        PushService pushService;
		
		/*
		pushSubscription.setEndpoint("https://fcm.googleapis.com/fcm/send/fwfJHvbho64:APA91bFPFvkCLupOqjj-9O1i4k8T4t1GPhR53c5dsFvtYhOa-lwI8ahek68Dhewwsk_pEIOxoNc4jFNteEUoCf6SPm6dMl8M_FEGik1bfu-SZ-hDMHlHzUpzm7kLNk07AqiNz4XzztVb");
		pushSubscription.setKey("BHQiaOWy55pFm8f0II6PiIF9ANp0mUlczKwpXZUxdIkUP5FX1OZ4TmQLKaKUtXVFPlxNkIXa0KkK9uuh3H_MAPo");
		pushSubscription.setAuth("_x-yZaAKZkeGuGV10Gss2Q");
		*/
		//db에 등록되어 있는 구독자를 가지고 와서 메세지를 전송한다.
		for (UserSubscriInfo userInfo : userSubscriInfo) {
			pushSubscription.setEndpoint(userInfo.getEndpoint());
			pushSubscription.setKey(userInfo.getP256dh());
			pushSubscription.setAuth(userInfo.getAuthor());
			
			notification = new Notification(
	        		pushSubscription.getEndpoint(),
	        		pushSubscription.getUserPublicKey(),
	        		pushSubscription.getAuthAsBytes(),
	        		payload.getBytes()
	        );
			
			pushService = new PushService();
	        pushService.setPublicKey(publicKey);
	        pushService.setPrivateKey(privateKey);
	        pushService.setSubject("mailto:admin@domain.com");
	        // Send the notification
	        pushService.send(notification);
		}
				
	}

}