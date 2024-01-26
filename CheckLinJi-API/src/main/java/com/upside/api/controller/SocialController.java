package com.upside.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.upside.api.dto.ApplePublicKey;
import com.upside.api.dto.MemberDto;
import com.upside.api.dto.MessageDto;
import com.upside.api.dto.NotificationRequestDto;
import com.upside.api.service.AppleOAuthService;
import com.upside.api.service.KaKaoOAuthService;
import com.upside.api.service.MemberService;
import com.upside.api.service.SocialService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/social")
public class SocialController {
    
    private final AppleOAuthService appleSerivce;
    private final KaKaoOAuthService kakaoSerivce;
    private final MemberService memberService;    
    private final SocialService socialService;

    // 카카오 로그인 페이지 테스트
    @GetMapping
    public String socialKakaoLogin(ModelAndView mav) {

        return "Login2";
    }
    
    
    
    @PostMapping("/login")
	public ResponseEntity<Map<String , String>> login (@RequestBody MemberDto memberDto) {
    	    			
    	Map<String, String> result = memberService.validateEmail(memberDto);
    	
   	 	if(result.get("HttpStatus").equals("2.00")) {			 		 
   	 		return new ResponseEntity<>(result, HttpStatus.OK);
   	 	} else {			 
   	 		return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST); 
   	 	}
	}
    
    @PostMapping("/sign")
	public ResponseEntity<Map<String , String>> signUp (@RequestBody MemberDto memberDto) {
    	
    	Map<String, String> result = socialService.signUpSocial(memberDto);
		
		if (result.get("HttpStatus").equals("2.00")) { // 성공						
			return new ResponseEntity<>(result,HttpStatus.OK);			
		} else {			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 		
	}
    

    
    // 인증 완료 후 리다이렉트 페이지로
    @PostMapping("/apple")
   	public ResponseEntity<Map<String , String>> redirectApple (@RequestBody ApplePublicKey applePublicKey )  {							 
    	     	    	    	    		  		    	   		 		   		    	
    	Map<String , String> result = appleSerivce.appleLogin(applePublicKey.getIdentityToken() , applePublicKey.getAuthorizationCode());
    	
    	if (result.get("HttpStatus").equals("2.00")){
    		return new ResponseEntity<>(result,HttpStatus.OK);
    	}else {
    		return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
    	}
   		    	
    }
    
}
