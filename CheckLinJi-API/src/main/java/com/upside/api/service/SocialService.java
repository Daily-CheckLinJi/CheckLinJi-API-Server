package com.upside.api.service;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upside.api.config.JwtTokenProvider;
import com.upside.api.dto.MemberDto;
import com.upside.api.entity.MemberEntity;
import com.upside.api.repository.MemberRepository;
import com.upside.api.util.Constants;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@RequiredArgsConstructor
@Service
public class SocialService {
	
	
	private final MemberRepository memberRepository;		
	private final JwtTokenProvider jwtTokenProvider;		
	private final PasswordEncoder passwordEncoder;
	private final EntityManager entityManager;
	/**
	 * 회원목록 조회
	 * @param memberDto
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<MemberEntity> memberList(Pageable pageable) {																				 		 		 		 		
		return  memberRepository.findAll(pageable);							
	}
	
	
	 /**
     * Unique한 값을 가져야하나, 중복된 값을 가질 경우를 검증
     * @param email
     */
    public boolean validateDuplicated(String nickName) {
    	    	    	
        if (memberRepository.findByNickName(nickName).isPresent()) {
        	log.info("아이디 검증 ------> " + "중복된 닉네임 입니다.");			 
        	return false ;			 			         	
        } else {
        	log.info("아이디 검증 ------> " + "사용할 수 있는 닉네임입니다.");
        	return true ;        	        	
        }        
    }
	
	
	/**
	 * 회원정보 업데이트
	 * @param memberDto
	 * @return
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public  Map<String, String> updateMember(MemberDto memberDto) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		if(memberDto.getEmail() == null ) {
			log.info("회원정보 업데이트 실패 ------> " + Constants.NOT_EXIST_PARAMETER);
			result.put("HttpStatus","1.01");
			result.put("Msg",Constants.NOT_EXIST_PARAMETER);
			return result ; 
		} 
				
		Optional<MemberEntity> user = memberRepository.findById(memberDto.getEmail());		
		
		if(!user.isPresent()) { // 아이디가 없으면
			log.info("회원정보 업데이트 실패 ------> " + Constants.NOT_EXIST_EMAIL);
			result.put("HttpStatus","1.04");
			result.put("Msg",Constants.NOT_EXIST_EMAIL);			
			
		} else if(memberDto.getPassword() != null && !passwordEncoder.matches(memberDto.getPassword(), user.get().getPassword())) { // 패스워드 변경시
			
			 MemberEntity updateUser = user.get();
			 log.info("회원정보 패스워드 업데이트 ------> " + user.get().getEmail());			
			 updateUser.setPassword(passwordEncoder.encode(memberDto.getPassword()));			 
			 result.put("HttpStatus","2.00");
			 result.put("Msg","비밀변호 변경이 완료되었습니다.");		 
			 	
		} else {
			 MemberEntity updateUser = user.get();			 
			 updateUser.setName(memberDto.getName());
			 updateUser.setNickName(memberDto.getNickName());			 		 
			 updateUser.setBirth(memberDto.getBirth());			 
			 updateUser.setSex(memberDto.getSex());
			 
			 result.put("HttpStatus","2.00");
			 result.put("Msg","회원정보 수정이 완료되었습니다.");
			 log.info("회원정보 업데이트 ------> " + updateUser.getEmail()); 			 			
		}				
		return result ; // 요청 성공	 	
	}
	
	/**
	 * 회원정보 삭제
	 * @param memberId
	 * @return
	 */
	public Map<String, String> deleteMember(String email) {
		Map<String, String> result = new HashMap<String, String>();
		
		 if(memberRepository.findById(email).isPresent() == true ) {
			 memberRepository.deleteById(email);
			 log.info("삭제 성공 ------> " + email);
			 result.put("HttpStatus", "2.00");
			 result.put("Msg", Constants.SUCCESS);
			 
		 } else {
			 log.info("삭제 실패 ------> " + email);
			 result.put("HttpStatus", "1.00");
			 result.put("Msg", Constants.FAIL);
			 	
		 }
		 return result ; 
}	
	/**
	 * 회원 로그인
	 * @param memberDto
	 * @return
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> loginMember(MemberDto memberDto) {
		Map<String, String> result = new HashMap<String, String>();
		
		Optional<MemberEntity> memberEntity = memberRepository.findById(memberDto.getEmail());
		
		if(!memberEntity.isPresent()) {
			log.info("회원 로그인 ------> " + Constants.INBALID_EMAIL_PASSWORD); 
			result.put("HttpStatus", "1.03");
	    	result.put("UserEmail", null);
	    	result.put("Msg", Constants.INBALID_EMAIL_PASSWORD);
	    	 return result ;
		}
				
		MemberEntity member = memberEntity.get();
		     
		    if (!passwordEncoder.matches(memberDto.getPassword(), member.getPassword())) {
		    	log.info("회원 로그인 ------> " + Constants.INBALID_EMAIL_PASSWORD);
		    	result.put("HttpStatus", "1.03");
		    	result.put("UserEmail", null);
		    	result.put("Msg", Constants.INBALID_EMAIL_PASSWORD);		    	
		    } else {
		    	log.info("회원 로그인 ------> " + Constants.SUCCESS);
		    	member.setRefreshToken((jwtTokenProvider.createRefreshToken())); // refresh Token DB 저장		    
			    result.put("HttpStatus", "2.00");
			    result.put("Token", jwtTokenProvider.createToken(memberDto.getEmail()));
			    result.put("RefreshToken", member.getRefreshToken());
			    result.put("UserEmail", member.getEmail());
			    result.put("Msg", Constants.SUCCESS);
		    
		    }
		    return result ;	
		    
		    

}
	  /**
     * 토큰 재발행
     * Token은 스프링 시큐리티 필터링 단계에서 해당 로직을 타고,
     * Refresh Token은 DB 접근 후 Refresh Token 객체에 맞는 유효성 검증 
     * @param requestDto
     * @return
     */
    @Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
    public Map<String, String> validateRefreshToken (MemberDto memberDto) {
    	
    	Map<String, String> result = new HashMap<String, String>();
    	 
    	if (memberDto.getAccessToken() == null || memberDto.getRefreshToken() == null ) {
    		log.info("Refresh Token 검증 ------> " + Constants.EXPIRATION_TOKEN);
    		
    		result.put("HttpStatus", "1.05");	    	
	    	result.put("Msg", Constants.EXPIRATION_TOKEN);	        	
	    	return result; 
		}    	
    	
        if (!jwtTokenProvider.validateTokenExceptExpiration(memberDto.getRefreshToken())) { // 만료기간이 지났는지 확인 
        	log.info("Refresh Token 검증 ------> " + Constants.EXPIRATION_TOKEN);
        	
        	result.put("HttpStatus", "1.05");	    	
	    	result.put("Msg", Constants.EXPIRATION_TOKEN);	        	
	    	return result; 
        }
            
        MemberEntity member = findMemberByToken(memberDto); // 파라미터로 입력받은 Access Token에 대한 회원 정보를 찾아온다.
        
        if(member == null ) {
        	log.info("Refresh Token 검증 ------> " + Constants.FAIL);
        	
        	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.FAIL);	    	
	    	return result; 
        }
        
        if (!member.getRefreshToken().equals(memberDto.getRefreshToken())) {// 파라미터로 입력받은 Refresh Token과 실제 DB에 저장된 Refresh Token을 비교하여 검증한다.
        	log.info("Refresh Token 검증 ------> " + Constants.INBALID_TOKEN);
        	result.put("HttpStatus", "1.06");	    	
    		result.put("Msg", Constants.INBALID_TOKEN);
    		
    		return result; 
    		
        } else {
        	 
        	 String accessToken = jwtTokenProvider.createToken(member.getEmail());
             String refreshToken = jwtTokenProvider.createRefreshToken();             
             member.setRefreshToken(refreshToken);             
             entityManager.merge(member); // member는 직접 조회한게 아닌 메소드 반환을 통한 객체로 JPA에 관리된 상태가 아닌 순수한 객체이기 떄문에 merge를 통해 병합해준다.      
             log.info("Refresh Token 검증 ------> " + Constants.SUCCESS);
             result.put("HttpStatus", "2.00");	    	
     		 result.put("Msg", Constants.SUCCESS);
     		 result.put("Token", accessToken);
     		 result.put("RefreshToken", refreshToken);
     		 
             return result ;        	
        }               
    }

    /**
     * 유효한 토큰이라면 AccessToken으로부터 Id 정보를 받아와 DB에 저장된 회원을 찾고 ,
     * 해당 회원의 실제 Refresh Token을 받아온다.
     * @param requestDto
     * @return
     */
    public MemberEntity findMemberByToken(MemberDto requestDto) {
        Authentication auth = jwtTokenProvider.getAuthentication(requestDto.getAccessToken());
        UserDetails userDetails = (UserDetails) auth.getPrincipal();
        String username = userDetails.getUsername();
        
        if(memberRepository.findById(username).isPresent() == true ) {
                	        	
        	return memberRepository.findById(username).get();
        } else {
        
        	return null ;
        }        
    }
    
    /**
     * 유효한 토큰이라면 AccessToken으로부터 Id 정보를 받아와 DB에 저장된 회원을 찾고 ,
     * 해당 회원의 실제 Refresh Token을 받아온다.
     * @param requestDto
     * @return
     */ 
    @Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
    public Map<String, String> validateEmail(String email) {
    	
    	Map<String, String> result = new HashMap<String, String>();
    	log.info("소셜 로그인 ------> Start ");
    	
    	Optional<MemberEntity> user = memberRepository.findById(email);
       
    	if(!user.isPresent()) { 
    		log.info("소셜 로그인 ------> 이메일이 DB에 없는경우 (신규 회원)");
    		result.put("HttpStatus", "2.01");
    		result.put("Msg", Constants.SUCCESS);
	    	result.put("UserEmail", email);	    		    	
    	}else {
    		MemberEntity member = user.get();
    		log.info("소셜 로그인 ------>  이메일이 DB에 있는경우 (로그인)");	
    		member.setRefreshToken((jwtTokenProvider.createRefreshToken())); // refresh Token DB 저장		    		    
			result.put("Token", jwtTokenProvider.createToken(member.getEmail()));
			result.put("RefreshToken", member.getRefreshToken());
	    	result.put("HttpStatus", "2.00");
			result.put("Msg", Constants.SUCCESS);
	    	result.put("UserEmail", member.getEmail());

    	}
    	return result;
   }
}