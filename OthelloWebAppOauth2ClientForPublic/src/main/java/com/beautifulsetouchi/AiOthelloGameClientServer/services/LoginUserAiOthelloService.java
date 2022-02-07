
package com.beautifulsetouchi.AiOthelloGameClientServer.services;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import org.springframework.web.client.RestTemplate;

import com.beautifulsetouchi.AiOthelloGameClientServer.models.AiOthelloRequestResource;
import com.beautifulsetouchi.AiOthelloGameClientServer.models.LoginUserAiOthelloResponseResource;

@Service 
public class LoginUserAiOthelloService {

	@Autowired
	private RestTemplate restTemplate;
	
	public static final String URL = "xxxx";
	
	public LoginUserAiOthelloResponseResource getAiOthelloResponse(AiOthelloRequestResource aiOthelloRequestResource) {	

		LoginUserAiOthelloResponseResource loginUserAiOthelloResponseResource = restTemplate.postForObject(URL, aiOthelloRequestResource, LoginUserAiOthelloResponseResource.class); 
		
		return loginUserAiOthelloResponseResource; 
	}

 
}
