package com.upside.api.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.upside.api.dto.MemberDto;
import com.upside.api.dto.MessageDto;
import com.upside.api.service.KaKaoOAuthService;
import com.upside.api.service.MemberService;
import com.upside.api.service.SocialService;

import lombok.RequiredArgsConstructor;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/social")
public class SocialController {
    
    private final KaKaoOAuthService oAuthSerivce;
    private final MemberService memberService;    
    private final SocialService socialService;

    // 카카오 로그인 페이지 테스트
    @GetMapping
    public String socialKakaoLogin(ModelAndView mav) {

        return "Login2";
    }
    
    
    
    @PostMapping("/login")
	public ResponseEntity<Map<String , String>> login (@RequestBody MemberDto memberDto) {
    	    			
    	Map<String, String> result = memberService.validateEmail(memberDto.getEmail());
    	
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
    
    
    
    // 인증 완료 후 리다이렉트 페이지
    @GetMapping("/kakao")
   	public ResponseEntity<MessageDto> redirectKakao (@RequestParam String code)  {							 
    	 
    	 MessageDto message = new MessageDto();
    	 
   		 String getKakaoAccessToken = oAuthSerivce.getKakaoAccessToken(code);
   		 
   		Map<String, String> getKakaoUserInfo = oAuthSerivce.getKakaoUserInfo(getKakaoAccessToken);
   		 
   		 if(getKakaoUserInfo.get("Email").equals("N") ) {
   			message.setStatusCode("1.00");
   			message.setMsg("이메일 정보 수집동의에 체크 해주시기 바랍니다");
   			return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
   		 }
   		 
   		
   		Map<String, String> result = memberService.validateEmail(getKakaoUserInfo.get("Email"));
   		
   		if(result.get("HttpStatus").equals("2.01")) { // 신규 회원일 경우
   			message.setStatusCode("2.01");
   			message.setUserEmail(result.get("UserEmail"));   			
   			return new ResponseEntity<>(message,HttpStatus.OK);
   		} else { // 로그인    			
   			message.setStatusCode("2.00");
   			message.setUserEmail(result.get("UserEmail"));
   			message.setToKen(result.get("Token"));
   			message.setRefreshToken(result.get("RefreshToken"));
   			return new ResponseEntity<>(message,HttpStatus.OK);
   		}
   		    		    		   		    		 
   	}
    
    
    // 인증 완료 후 리다이렉트 페이지
    @GetMapping("/google")
   	public ResponseEntity<MessageDto> redirectGoogle (@RequestParam String Email)  {							 
    	 
    	 MessageDto message = new MessageDto();
    	    		  		    	   		 		   		    	
   		Map<String, String> result = memberService.validateEmail(Email);
   		
   		if(result.get("HttpStatus").equals("2.01")) { // 신규 회원일 경우
   			message.setStatusCode("2.01");
   			message.setUserEmail(result.get("UserEmail"));   			
   			return new ResponseEntity<>(message,HttpStatus.OK);
   		} else { // 로그인    			
   			message.setStatusCode("2.00");
   			message.setUserEmail(result.get("UserEmail"));
   			message.setToKen(result.get("Token"));
   			message.setRefreshToken(result.get("RefreshToken"));
   			return new ResponseEntity<>(message,HttpStatus.OK);
   		}  		    		    		   		    		 
   	}
}
