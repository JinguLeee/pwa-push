# Java Pwa push Example

## web-push를 이용한 notification

### 내장톰캣서버를 기동한다.
- publicVapidKey 와 privateVapidKey 를 생성한다.

```
브라우저에서  http://127.0.0.1:8081/push/keyGenerater.do 를 호출하면 console창에 system.out.println을 통해 키값이 찍힌다.
해당 키값을 pwa.properties 파일에 저장한다.
호출시 java.security.NoSuchProviderException: no such provider: BC 에러가 발생할 경우 보안 관련 문제이기 때문에 설치한 자바 파일
경로에 가서 설정을 추가한다.
c:\Program Files\Java\jdk1.8.0_162\jre\lib\security\java.security
설치된 자바 경로에 가서 관리자 권한으로 java.security 파일을 열어서 security 관련 설정을 추가한다.
security.provider.11=org.bouncycastle.jce.provider.BouncyCastleProvider <= 이부분 추가.
```
  
### 브라우저 알림전송 확인
- [로컬호스트 web-push](http://127.0.0.1:8081/pwa/index.html)

### gradle build(컴파일)
- 아래 명령어로 컴파일 하면 build/libs 폴더에 jar 파일이 생성된다.

```
gradlew bootjar
```

- jar 파일 실행

```
java -Dfile.encoding=UTF-8 -jar pwa-push-0.0.1-SNAPSHOT.jar
```
