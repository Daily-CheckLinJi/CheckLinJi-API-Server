package com.upside.api.service;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.upside.api.entity.MemberEntity;
import com.upside.api.repository.MemberRepository;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class MailService {
 
	private final JavaMailSender javaMailSender;
	private final RedisTemplate<String, String> redisTemplate;
	private final MemberRepository memberRepository;
	
	
	/**
	 * 회원가입 인증코드와 함께 메일 발송 
	 * @param email
	 * @return
	 */
	public Map<String,String> sendMail(String email){
		
		log.info("이메일 전송 ------> " + "Start");
		
	    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
	    
	    Map<String, String> result = new HashMap<String, String>();
	    
	    String authCode = "" ; 
	    try{
	    
	        // 1. 메일 수신자 설정	    	
	    	String[] receiveList = {email};
	    	
//	        String[] receiveList = {"kyky7852@naver.com", "thswhdrnr12@naver.com"};
	        	      	        
	        simpleMailMessage.setTo(receiveList);
	       
	        // 2. 메일 제목 설정
	        simpleMailMessage.setSubject("데일리 첵린지 회원가입");

	        // 3. 메일 내용 설정
	        authCode = AuthCode(); // 인증코드 발급       
	        simpleMailMessage.setText("인증 번호 6자리를 입력해주세요. " + authCode);       
	       	        
	        // 4. 메일 전송
	        javaMailSender.send(simpleMailMessage);
	        	 		
	 		ValueOperations<String, String> redis = redisTemplate.opsForValue(); // Redis Map 객체 생성		    			    	
	    	redis.set("authCode_"+email, authCode); // authCode Redis 저장
	    	redisTemplate.expire("authCode_"+ email ,30, TimeUnit.MINUTES); // redis refreshToken expire 30분 지정
	    	
	    	log.info("이메일 전송 ------> " + Constants.SUCCESS);
	        result.put("HttpStatus","2.00");		
	 		result.put("Msg",Constants.SUCCESS);
	 		
	    } catch(Exception e){
	    	 log.error("이메일 전송 ------> " + Constants.FAIL , e);
	         result.put("HttpStatus","1.00");		
	  		 result.put("Msg", Constants.FAIL);	  			  		 
	    }
 		
			return result;
			
			
	}
	
	/**
	 * 패스워드 찾기 인증코드 와 함께 메일 발송 
	 * @param email
	 * @return
	 */
	public Map<String,String> sendPasswordCode(String email){
		
		log.info("비밀번호 찾기 이메일 전송 ------> " + "Start");
		Map<String, String> result = new HashMap<String, String>();
		
		// 유저 정보가 있는지 확인
		Optional<MemberEntity> memberEntity = memberRepository.findById(email);
		
		// 이메일이 없을경우 
		if(!memberEntity.isPresent()) {				
			result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.INBALID_EMAIL);
	    	log.info("비밀번호 찾기 이메일 전송 ------> " + Constants.INBALID_EMAIL);
	    	return result ;
		}
		
		// 소셜 계정일 경우 
		if(memberEntity.get().getPassword().equals("X")) {
			result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.INBALID_EMAIL);
	    	log.info("비밀번호 찾기 이메일 전송 ------> " + "소셜 계정은 책린지 내에서는 비밀번호 변경이 불가능합니다.");
	    	return result ;
		}
		
		
	    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();	    	  
	    
	    String passwordCode = "" ; 
	    
	    try{
	    
	        // 1. 메일 수신자 설정	    	
	    	String[] receiveList = {email};
	    		        	        	      	        
	        simpleMailMessage.setTo(receiveList);
	       
	        // 2. 메일 제목 설정
	        simpleMailMessage.setSubject("데일리 첵린지 비밀번호 찾기");

	        // 3. 메일 내용 설정
	        passwordCode = AuthCode(); // 인증코드 발급       
	        simpleMailMessage.setText("인증 번호 6자리를 입력해주세요. " + passwordCode);       
	       	        
	        // 4. 메일 전송
	        javaMailSender.send(simpleMailMessage);
	        
	        // 5. redis 저장 
	        ValueOperations<String, String> redis = redisTemplate.opsForValue(); // Redis Map 객체 생성		    			    	
	    	redis.set("passwordCode_"+email, passwordCode); // passwordCode Redis 저장
	    	redisTemplate.expire("passwordCode_"+ email ,30, TimeUnit.MINUTES); // redis refreshToken expire 30분 지정
	        
	    	log.info("비밀번호 찾기 이메일 전송 ------> " + Constants.SUCCESS);
	        result.put("HttpStatus","2.00");		
			result.put("Msg",Constants.SUCCESS);	   
			
	    } catch(Exception e){
	    	log.error("비밀번호 찾기 이메일 전송 ------> " + Constants.FAIL , e);
	        result.put("HttpStatus","1.00");		
	  		result.put("Msg", Constants.SYSTEM_ERROR);
	  			  		 
	    }	 			    		 		
			return result;					
	}
	
	/**
	 * 회원가입 authCode 발급
	 * @return
	 */
	public String AuthCode () {
		
		String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		int LENGTH = 6;
		
		  Random random = new Random();
	        StringBuilder sb = new StringBuilder();
	        for (int i = 0; i < LENGTH; i++) {
	            int randomIndex = random.nextInt(CHARACTERS.length());
	            sb.append(CHARACTERS.charAt(randomIndex));
	        }
	       
	       return sb.toString();
		
	}
	
	/**
	 * 회원가입 authCode 인증
	 * @return
	 */
	public Map<String, String> ConfirmAuthCode (String email , String authCode) {
		Map<String, String> result = new HashMap<String, String>();
		
		ValueOperations<String, String> redis = redisTemplate.opsForValue(); // Redis Map 객체 생성
		
		log.info("회원가입 인증 ------> " + "Start");
		
		try {
			
		if(redis.get("authCode_"+email).equals(authCode)) {		
			
			log.info("회원가입 인증 ------> " + Constants.SUCCESS);
	        result.put("HttpStatus","2.00");		
	 		result.put("Msg",Constants.SUCCESS);
	 		redisTemplate.delete("authCode_"+email); // "myKey"라는 Key를 삭제	
	 		return result;
		} 		
			log.info("회원가입 인증 ------> " + Constants.FAIL);
	        result.put("HttpStatus","1.00");		
	 		result.put("Msg", Constants.FAIL);
					 		
	 		return result;
	 		
		} catch (Exception e) {
			log.error("회원가입 인증 ------> " + Constants.SYSTEM_ERROR , e);
	        result.put("HttpStatus","1.00");		
	 		result.put("Msg", Constants.SYSTEM_ERROR);
	 		
	 		return result;
		}
	}
	
	
	/**
	 * 비밀번호 찾기 passwordCode 인증
	 * @return
	 */
	public Map<String, String> ConfirmPasswordCode (String email , String passwordCode) {
		Map<String, String> result = new HashMap<String, String>();
		
		ValueOperations<String, String> redis = redisTemplate.opsForValue(); // Redis Map 객체 생성
		
		log.info("비밀번호 찾기 인증 ------> " + "Start");
		
		try {
			
		if(redis.get("passwordCode_"+email).equals(passwordCode)) {		
			
			log.info("비밀번호 찾기 인증 ------> " + Constants.SUCCESS);
	        result.put("HttpStatus","2.00");		
	 		result.put("Msg",Constants.SUCCESS);
	 		redisTemplate.delete("passwordCode_"+email); // "myKey"라는 Key를 삭제	
	 		return result;
		} 		
			log.info("비밀번호 찾기 인증 ------> " + Constants.FAIL);
	        result.put("HttpStatus","1.00");		
	 		result.put("Msg", Constants.FAIL);
					 		
	 		return result;
	 		
		} catch (Exception e) {
			log.error("비밀번호 찾기 인증 ------> " + Constants.SYSTEM_ERROR , e);
	        result.put("HttpStatus","1.00");		
	 		result.put("Msg", Constants.SYSTEM_ERROR);
	 		
	 		return result;
		}
	}
		
}
