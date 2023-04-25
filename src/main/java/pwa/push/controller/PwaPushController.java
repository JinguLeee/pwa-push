package pwa.push.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jose4j.lang.JoseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

import nl.martijndwars.webpush.cli.commands.GenerateKeyCommand;
import nl.martijndwars.webpush.cli.handlers.GenerateKeyHandler;
import pwa.push.model.UserSubscriInfo;
import pwa.push.repository.UserSuvscriInfoRepository;
import pwa.push.service.PwaPushService;

@Controller
public class PwaPushController {
	
private Logger logger = LoggerFactory.getLogger(PwaPushController.class);
	
	@Resource(name = "pwaPushService")
    private PwaPushService pwaPushService;
	
	@Autowired
	private UserSuvscriInfoRepository userSuvscriInfoRepository;
	/**
	 * 알림허용 등록
	 * @return ModelAndView
	 * @exception Exception
	 */
	@RequestMapping({"/push/subscribe.do"})
	public ModelAndView subscribe(HttpServletRequest request, HttpServletResponse response, @RequestBody String transactionConfig) throws GeneralSecurityException, InterruptedException, JoseException, ExecutionException, IOException {
		  
	   logger.debug("/push/subscribe.do 호출");
				
	   ModelAndView mav = new ModelAndView("jsonView");
	   
	   logger.debug("transactionConfig 문자형식 : " + transactionConfig);
	   
	   ObjectMapper mapper = new ObjectMapper();
	   Map<String, Object> subscribeMap = new HashMap<String, Object>();
	   
	   try { 
		// convert JSON string to Map 
		   subscribeMap = mapper.readValue(transactionConfig, Map.class);
		   logger.debug("subscribeMap : " + subscribeMap);
		   logger.debug("keys : " + subscribeMap.get("keys"));
		   
		   Map<String, Object> mapKeys = (Map<String, Object>) subscribeMap.get("keys");
		   
		   logger.debug("mapKeys===> : " + mapKeys);
			
		   UserSubscriInfo userSubscriInfo = new UserSubscriInfo();
		   

		   userSubscriInfo.setAuthor(mapKeys.get("auth").toString());

		   userSubscriInfo.setP256dh(mapKeys.get("p256dh").toString());	   
		   userSubscriInfo.setEndpoint(subscribeMap.get("endpoint").toString());
		   // userSubscriInfo.setExpirationTime(subscribeMap.get("expirationTime").toString());
		   userSubscriInfo.setUseYn("Y");
		   
		   userSuvscriInfoRepository.save(userSubscriInfo);
		   
		   mav.addObject("ResultCode", "SUCCESS");
		   mav.addObject("ErrorMsg", "");
		   
	   } catch (IOException e) {
		   e.printStackTrace(); 
		   mav.addObject("ResultCode", "ERROR");
		   mav.addObject("ErrorMsg", "에러발생");
	   } catch ( Exception e ) {
		  mav.addObject("ResultCode", "ERROR");
		  mav.addObject("ErrorMsg", "에러발생");
	   }	
		return mav;
	}

	
	/**
	 * 알림해제
	 * @return ModelAndView
	 * @exception Exception
	 */
	@RequestMapping({"/push/unsubscribe.do"})
	public ModelAndView unsubscribe(HttpServletRequest request, HttpServletResponse response, @RequestBody String transactionConfig) throws GeneralSecurityException, InterruptedException, JoseException, ExecutionException, IOException {
		  
	   logger.debug("/push/unsubscribe.do 호출");
				
	   ModelAndView mav = new ModelAndView("jsonView");
	   
	   logger.debug("transactionConfig 문자형식 : " + transactionConfig);
	   
	   ObjectMapper mapper = new ObjectMapper();
	   Map<String, Object> subscribeMap = new HashMap<String, Object>();
	   
	   try { 
		// convert JSON string to Map 
		   subscribeMap = mapper.readValue(transactionConfig, Map.class);
		   logger.debug("subscribeMap : " + subscribeMap);
		   logger.debug("keys : " + subscribeMap.get("keys"));
		   
		   Map<String, Object> mapKeys = (Map<String, Object>) subscribeMap.get("keys");
		   
		   logger.debug("mapKeys===> : " + mapKeys);
		   
		   userSuvscriInfoRepository.deleteByP256dh(mapKeys.get("p256dh").toString());
		   
		   mav.addObject("ResultCode", "SUCCESS");
		   mav.addObject("ErrorMsg", "");
		   
	   } catch (IOException e) {
		   e.printStackTrace(); 
		   mav.addObject("ResultCode", "ERROR");
		   mav.addObject("ErrorMsg", "에러발생");
	   } catch ( Exception e ) {
		  mav.addObject("ResultCode", "ERROR");
		  mav.addObject("ErrorMsg", "에러발생");
	   }	
		return mav;
	}
	
	/**
	 * 알림전송
	 * @return ModelAndView
	 * @exception Exception
	 */
	@RequestMapping({"/push/sendMessage.do"})
	public ModelAndView sendMessage(HttpServletRequest request, HttpServletResponse response, @RequestBody String transactionConfig) throws GeneralSecurityException, InterruptedException, JoseException, ExecutionException, IOException {
		  
	   logger.debug("/push/sendMessage.do 호출");
				
	   ModelAndView mav = new ModelAndView("jsonView");
	   
	   logger.debug("transactionConfig 문자형식 : " + transactionConfig);
	   
	   ObjectMapper mapper = new ObjectMapper();
	   Map<String, Object> messageMap = new HashMap<String, Object>();
	   
	   try { 
		// convert JSON string to Map 
		   messageMap = mapper.readValue(transactionConfig, Map.class);
		   logger.debug("messageMap : " + messageMap);
	
		   List<UserSubscriInfo> userSubscriInfo = userSuvscriInfoRepository.findAll();
		    
		   pwaPushService.sendMessage(messageMap,userSubscriInfo);
		   
		   mav.addObject("ResultCode", "SUCCESS");
		   mav.addObject("ErrorMsg", "");
		   
	   } catch (IOException e) {
		   e.printStackTrace(); 
		   mav.addObject("ResultCode", "ERROR");
		   mav.addObject("ErrorMsg", "에러발생");
	   } catch ( Exception e ) {
		  mav.addObject("ResultCode", "ERROR");
		  mav.addObject("ErrorMsg", "에러발생");
	   }	
		return mav;
	}
	
	
	/**
	 * 암호화 키생성
	 * @return ModelAndView
	 * @exception Exception
	 */
	@RequestMapping({"/push/keyGenerater.do"})
	public void keyGenerater(HttpServletRequest request, HttpServletResponse response) {
		  
	   logger.debug("/push/keyGenerater.do 호출");
	  	   
	   GenerateKeyCommand generateKeyCommand = new GenerateKeyCommand();
	      
		try {
			new GenerateKeyHandler(generateKeyCommand).run();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}

}
