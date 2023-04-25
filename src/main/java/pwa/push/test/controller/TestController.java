package pwa.push.test.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import pwa.errorhandling.exception.ForbiddenException;

@Controller
public class TestController {
	
	private static final Logger log = LoggerFactory.getLogger(TestController.class);

	@GetMapping("/internalerror")
    public void internalerror() {
		System.out.println("internalerror");
		
        throw new RuntimeException("500 Internal Error !!");
    }

    @GetMapping("/forbidden")
    public void forbidden() {
        throw new ForbiddenException("403 Forbidden !!");
    }
    
}
