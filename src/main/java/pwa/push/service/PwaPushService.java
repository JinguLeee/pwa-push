package pwa.push.service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.jose4j.lang.JoseException;

import pwa.push.model.UserSubscriInfo;

public interface PwaPushService {
	void sendMessage(Map<String, Object> messageMap, List<UserSubscriInfo> userSubscriInfo) throws GeneralSecurityException, InterruptedException, JoseException, ExecutionException, IOException;
}
