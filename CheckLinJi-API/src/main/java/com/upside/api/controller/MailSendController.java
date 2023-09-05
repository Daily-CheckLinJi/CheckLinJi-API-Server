package com.upside.api.controller;




import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upside.api.dto.MemberDto;
import com.upside.api.dto.MessageDto;
import com.upside.api.service.MailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/send")
public class MailSendController {
    
    private final MailService mailService;
        


    /**
     * 회원 가입 이메일 인증
     * @param memberDto
     * @return
     * @throws Exception
     */
    @PostMapping("/mail")     
    public ResponseEntity<MessageDto> SendMail(@RequestBody MemberDto memberDto ) throws Exception {    	
    	
    	MessageDto messageDto = new MessageDto();
    	
    	Map<String, String> result = mailService.sendMail(memberDto.getEmail());
    	
    	messageDto.setStatusCode(result.get("HttpStatus"));
		messageDto.setMsg(result.get("Msg"));
    	
		if(result.get("HttpStatus").equals("1.00")){
				
		return new ResponseEntity<>(messageDto,HttpStatus.BAD_REQUEST);
		
		} 
		
		return new ResponseEntity<>(messageDto,HttpStatus.OK);
		
    	} 
    
    
    /**
     * 비밀번호 찾기 이메일 인증
     * @param memberDto
     * @return
     * @throws Exception
     */
    @PostMapping("/passwordCode")     
    public ResponseEntity<Map<String, String>> passwordCode(@RequestBody MemberDto memberDto ) throws Exception {    	
    	    	    	
    	Map<String, String> result = mailService.sendPasswordCode(memberDto.getEmail());
    	    	
		if(result.get("HttpStatus").equals("2.00")){				
			return new ResponseEntity<>(result,HttpStatus.OK);		
		}else {
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);	
		}					
	} 
    
    /**
     * 회원가입 인증코드 확인
     * @param data
     * @return
     * @throws Exception
     */
    @PostMapping("/confirm")     
    public ResponseEntity<MessageDto> ConfirmAuthCode (@RequestBody Map<String,String> data ) throws Exception {    	
    	
    	MessageDto messageDto = new MessageDto();
    	    	
    	Map<String, String> result = mailService.ConfirmAuthCode(data.get("email"),data.get("authCode"));
    	
    	messageDto.setStatusCode(result.get("HttpStatus"));
		messageDto.setMsg(result.get("Msg"));
    	
		if(result.get("HttpStatus").equals("1.00")){
				
		return new ResponseEntity<>(messageDto,HttpStatus.BAD_REQUEST);
		
		} 
		
		return new ResponseEntity<>(messageDto,HttpStatus.OK);
		
    	} 
    
    /**
     * 비밀번호 찾기 인증코드 확인
     * @param data
     * @return
     * @throws Exception
     */
    @PostMapping("/confirmPasswordCode")     
    public ResponseEntity<Map<String, String>> ConfirmPasswordCode (@RequestBody Map<String,String> data ) throws Exception {    	
    	    	    	    
    	Map<String, String> result = mailService.ConfirmPasswordCode(data.get("email"),data.get("passwordCode"));
    	        	
		if(result.get("HttpStatus").equals("2.00")){		
			return new ResponseEntity<>(result,HttpStatus.OK);				
		} else {
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		}						
	} 
  
}
