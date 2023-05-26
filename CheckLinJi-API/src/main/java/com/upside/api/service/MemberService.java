package com.upside.api.service;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.upside.api.config.JwtTokenProvider;
import com.upside.api.dto.MemberDto;
import com.upside.api.entity.MemberEntity;
import com.upside.api.mapper.MemberMapper;
import com.upside.api.repository.MemberRepository;
import com.upside.api.util.Constants;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@RequiredArgsConstructor
@Service
public class MemberService {
	
	 @Value("${file.upload-dir}")
	 private String uploadDir;
	
	private final MemberRepository memberRepository;		
	private final JwtTokenProvider jwtTokenProvider;		
	private final PasswordEncoder passwordEncoder;
	private final EntityManager entityManager;
	private final MemberMapper memberMapper ;
	private final RedisTemplate<String, String> redisTemplate;
	private final FileService fileService ;
	
	
	/**
	 * 회원목록 조회
	 * @param memberDto
	 * @return
	 */
	@Transactional(readOnly = true)
	public Page<MemberEntity> memberList(Pageable pageable) {																				 		 		 		 		
		return  memberRepository.findAll(pageable);							
	}
	
	@Transactional(readOnly = true)
	public Map<String, Object> selectMember(String email) {	
		Map<String, Object> result = new HashMap<String, Object>();
		
		// Base64로 인코딩된 이미지 파일 문자열로 가져옴
	     
		Optional<MemberEntity> data = memberRepository.findById(email);
		
		 if(data.isPresent()) {		
			 String file = fileService.myAuthImage(data.get().getProfile());
			 data.get().setPassword(""); // 조회시 패스워드는 공백 처리
			 data.get().setProfile(file); // 프로필은 base64로 인코딩해서 넘겨줌
			 log.info("회원목록 조회 ------> " + Constants.SUCCESS);
			 result.put("HttpStatus","2.00");
			 result.put("Msg",Constants.SUCCESS);
			 result.put("selectMember",data.get());			 			 			 
		 } else {
			 log.info("회원목록 조회 ------> " + Constants.FAIL);
			 result.put("HttpStatus","1.00");
			 result.put("Msg",Constants.FAIL);
		 }
		 
		 return result;
							
	}
	
	/**
	 * 회원가입 
	 * @param memberDto
	 * @return 
	 */
	public Map<String, String> signUp(MemberDto memberDto) {
		Map<String, String> result = new HashMap<String, String>();
		
		if(memberDto.getEmail() == null || memberDto.getName() == null || memberDto.getNickName() == null || memberDto.getPassword() == null ||
		     memberDto.getBirth() == null || memberDto.getSex() == null ) {  
		    									
			log.info("회원가입 실패 ------> " + Constants.NOT_EXIST_PARAMETER);
			result.put("HttpStatus","1.01");
			result.put("Msg",Constants.NOT_EXIST_PARAMETER);
			return result ; // 요청은 잘 만들어졌지만, 문법 오류로 인하여 따를 수 없습니다.
		} 						 
		
		 Optional<MemberEntity> idSame = memberRepository.findById(memberDto.getEmail());
		 
		 if(idSame.isPresent()) {
			 log.info("회원가입 실패 ------> " + "중복된 이메일 입니다.");
			 
			 result.put("HttpStatus","1.02");
			 result.put("Msg",Constants.DUPLICATE_EMAIL);
			 
			 return result ; // 요청은 잘 만들어졌지만, 문법 오류로 인하여 따를 수 없습니다.
		 }
		 
		 Map<String , String> validateDuplicated = validateDuplicatedNickName(memberDto.getNickName());
		
		if(validateDuplicated.get("HttpStatus").equals("1.00")) {
			log.info("회원가입 실패 ------> " + "중복된 닉네임입니다.");
			 
			 result.put("HttpStatus","1.00");
			 result.put("Msg","중복된 닉네임입니다.");
			 
			 return result ;
		}
		
		String profileName = "";
		 
		if(memberDto.getSex().equals("M")) {			
			
			profileName = "M-" + String.valueOf((int)(Math.random() * 5) + 1) + ".png";
		}else if (memberDto.getSex().equals("W")) {
			
			profileName = "W-" + String.valueOf((int)(Math.random() * 5) + 1) + ".png";
		} else {
			
			profileName = "W-" + String.valueOf((int)(Math.random() * 5) + 1) + ".png";
		}
		
		SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd hh:mm");        		
		
		MemberEntity memberEntity = MemberEntity.builder()
				.email(memberDto.getEmail())
				.name(memberDto.getName())
				.nickName(memberDto.getNickName())
				.password(passwordEncoder.encode(memberDto.getPassword())) 								
				.birth(memberDto.getBirth())
				.sex(memberDto.getSex())
				.loginDate(today.format(new Date()))
				.joinDate(today.format(new Date()))
				.authority("user")
				.profile(uploadDir + "/" + "profile" + "/" + profileName) // 문자열에서 백슬래시()는 이스케이프 문자(escape character)로 사용되기 때문에 사용할려면 \\ 두개로 해야 \로 인식
				.grade("책갈피")
				.build();						        
		
		 memberRepository.save(memberEntity);
		 
		 boolean  exsistUser = memberRepository.findById(memberDto.getEmail()).isPresent();
		 
		 if (exsistUser == true) {
			 log.info("회원가입 성공 ------> " + memberDto.getEmail());
			 result.put("HttpStatus","2.00");
			 result.put("UserEmail",memberDto.getEmail());
			 result.put("Msg",Constants.SUCCESS);			  
		 } else {
			 log.info("회원가입 실패 ------> " + Constants.FAIL);
			 result.put("HttpStatus","1.00");
			 result.put("Msg",Constants.FAIL);			  
		 }
		 return result ;
	}
	
	 /**
     * Unique한 값을 가져야하나, 중복된 값을 가질 경우를 검증
     * @param nickName
     */
    public Map<String , String> validateDuplicatedNickName(String nickName) {
    	   
    	Map<String , String> result = new HashMap<String, String>();
    	
        if (memberRepository.findByNickName(nickName).isPresent()) {
        	log.info("아이디 검증 ------> " + "중복된 닉네임입니다.");
        	result.put("HttpStatus","1.00");			
			result.put("Msg","중복된 닉네임입니다.");        	         	
        } else {
        	log.info("아이디 검증 ------> " + "사용할 수 있는 닉네임입니다.");
        	result.put("HttpStatus","2.00");			
			result.put("Msg","사용할 수있는 닉네임입니다.");        		        	
        }
        return result ;
    }
	
    public Map<String , String> validateDuplicatedEmail(String email) {
    	
    	Map<String , String> result = new HashMap<String, String>();
    	
        if (memberRepository.findById(email).isPresent()) {
        	log.info("아이디 검증 ------> " + "중복된 이메일 입니다.");
        	result.put("HttpStatus","1.00");			
			result.put("Msg","중복된 이메일입니다.");        		 			         	
        } else {
        	log.info("아이디 검증 ------> " + "사용할 수있는 이메일입니다.");
        	result.put("HttpStatus","2.00");			
			result.put("Msg","사용할 수있는 이메일입니다.");		        	        	        	
        }   
        return result ;
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
			
		try {						
			 if(memberRepository.findById(email).isPresent() == true ) {			 
				 
				 HashMap<String, String> param = new HashMap<String, String>();			 			 			 
				 
				 param.put("email", email);
				 param.put("COMPLETED", "");
				 
				 memberMapper.memberDelete(param);
				 			 			 			 
				 if (param.get("COMPLETED").equals("Y")) {
					 log.info("삭제 성공 ------> " + email);
					 result.put("HttpStatus", "2.00");
					 result.put("Msg", Constants.SUCCESS);
				 }else {
					 log.info("삭제 실패 ------> " + email);
					 result.put("HttpStatus", "1.00");
					 result.put("Msg", Constants.FAIL);
				 }			 			 			 
			 } else {
				 log.info("삭제 실패 ------> " + email);
				 result.put("HttpStatus", "1.00");
				 result.put("Msg", "존재하지 않는 이메일입니다.");
				 	
			 }
		} catch (Exception e) {
			 log.info("DB 에러 ------> " );
			 e.printStackTrace();
			 result.put("HttpStatus", "1.00");
			 result.put("Msg", "DB 오류로 인한 실패");
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
		    	ValueOperations<String, String> redis = redisTemplate.opsForValue(); // Redis Map 객체 생성		    			    	
		    	redis.set("refreshToken_"+member.getEmail(), jwtTokenProvider.createRefreshToken()); // refresh Token Redis 저장
		    	redisTemplate.expire("refreshToken_"+member.getEmail(), 31, TimeUnit.DAYS); // redis refreshToken expire 31일 지정
		    	
			    result.put("HttpStatus", "2.00");
			    result.put("Token", jwtTokenProvider.createToken(memberDto.getEmail()));
			    result.put("RefreshToken", redis.get("refreshToken_"+member.getEmail()));
			    result.put("UserEmail", member.getEmail());
			    result.put("Msg", Constants.SUCCESS);
			    
			    log.info("회원 로그인 ------> " + Constants.SUCCESS);
		    }
		    return result ;	
		    
		    

}
	  /**
     * 토큰 재발행
     * Token은 스프링 시큐리티 필터링 단계에서 해당 로직을 타고,
     * Refresh Token은 Redis 접근 후 Refresh Token 객체에 맞는 유효성 검증 
     * @param requestDto
     * @return
     */
    @Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
    public Map<String, String> validateRefreshToken (MemberDto memberDto) {
    	
    	Map<String, String> result = new HashMap<String, String>();
    	 
    	if (memberDto.getAccessToken() == null || memberDto.getRefreshToken() == null ) {
    		log.info("Refresh Token 검증 ------> " + Constants.EXPIRATION_TOKEN);
    		
    		result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.EXPIRATION_TOKEN);	        	
	    	return result; 
		}    	
    	
        if (!jwtTokenProvider.validateTokenExceptExpiration(memberDto.getRefreshToken())) { // 리프레쉬 토큰 만료기간이 지났는지 확인 
        	log.info("Refresh Token 검증 ------> " + Constants.EXPIRATION_TOKEN);
        	
        	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.EXPIRATION_TOKEN);	        	
	    	return result; 
        }
            
//        MemberEntity member = findMemberByToken(memberDto); // 유효한 토큰이라면 AccessToken으로부터 Id 정보를 받아와 DB에 저장된 회원을 찾고 , 해당 회원의 실제 Refresh Token을 받아온다.
        
        String token = jwtTokenProvider.getEmail(memberDto.getAccessToken()); // 유효한 토큰이라면 AccessToken으로부터 email을 얻기위해 헤더에서 토큰을 디코딩하는 부분.
        
        ValueOperations<String, String> redis = redisTemplate.opsForValue(); // Redis Map 객체 생성
                
        if(token == null ) {
        	log.info("Refresh Token 검증 ------> " + "Refresh Token NULL");
        	
        	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.FAIL);	    	
	    	return result; 
        }
        
        if (!redis.get("refreshToken_"+token).equals(memberDto.getRefreshToken())) {// 파라미터로 입력받은 Refresh Token과 실제 Redis 에 저장된 Refresh Token을 비교하여 검증한다.
        	log.info("Refresh Token 검증 ------> " + Constants.INBALID_TOKEN);
        	result.put("HttpStatus", "1.00");	    	
    		result.put("Msg", Constants.INBALID_TOKEN);
    		
    		return result;   
    		
        } else {
        	 
        	 String accessToken = jwtTokenProvider.createToken(token);
             String refreshToken = jwtTokenProvider.createRefreshToken();
             
             redis.set("refreshToken_"+token, refreshToken);
             
             redisTemplate.expire("refreshToken_"+token, 31, TimeUnit.DAYS); // redis refreshToken expire 31일 지정
             
             log.info("redis RT : {}", redis.get("refreshToken_"+token));
             
                  
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
    		result.put("HttpStatus", "1.00");
    		result.put("Msg", "가입 되지않은 사용자입니다. 회원가입을 진행해주세요.");
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
    
    
   /**
    * 프로필 업데이트
    * @param email
    * @return
    */
    @Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
    public Map<String, String> updateProfile (String profile , String profileName , String email) {
    	
    	Map<String, String> result = new HashMap<String, String>();
    	log.info("프로필 사진 업데이트 ------> Start ");
    	
    	try {					    	
    	Optional<MemberEntity> user = memberRepository.findById(email);       
    	if(!user.isPresent()) { 
    		log.info("프로필 사진 업데이트 실패 ------> 존재하지 않는 이메일입니다.");
    		result.put("HttpStatus", "1.00");
    		result.put("Msg", Constants.FAIL);
    		return result;
    	}else {
    		
    		// 기본 프로필 ( M- , or W- 일 경우 삭제 X )
    		if(!user.get().getProfile().contains("M-") && !user.get().getProfile().contains("W-")) {
    		
    			// 파일 삭제
        		boolean deleteYn = fileService.deleteFile(user.get().getProfile());
        		
        		// 삭제 실패 시
        		if(!deleteYn) {
        			log.info("프로필 사진 삭제 실패 ------> " + user.get().getProfile());
    	    		result.put("HttpStatus", "1.00");
    	    		result.put("Msg", "프로필 사진 삭제에 실패하였습니다.");
    	    		return result;
        		}
    			
    		}    		    		    		
    		
    		// 파일 업로드 
    	 	String submissionImageRoute = fileService.uploadProfile(profile, profileName ,email);
    		
    	 	if(submissionImageRoute.equals("N")) {
    	 		log.info("프로필 사진 업데이트 실패 ------> 파일 에러");
	    		result.put("HttpStatus", "1.00");
	    		result.put("Msg", "프로필 사진 업데이트에 실패하였습니다.");
	    		return result;
    	 	}
    	 	    	 	    	        	 	
    		MemberEntity member = user.get();
    		
    		member.setProfile(submissionImageRoute);    		    
			
	    	result.put("HttpStatus", "2.00");
			result.put("Msg", Constants.SUCCESS);
			log.info("프로필 사진 업데이트 성공 ------>" + email);	

		}
			} catch (Exception e) {
				log.info("프로필 사진 업데이트 실패 ------> Exception");
	    		result.put("HttpStatus", "1.00");
	    		result.put("Msg", Constants.FAIL);
	    		return result;
			}
    	return result;
   }
    
    
    /**
	 * 본인 누적미션 횟수
	 * 첼린지 미션 제출 후 조회하는것이기 때문에 무조건 0 이상이므로 0은 에러로 
	 * @param fileUploadDto
	 * @return
	 */
	public int missionCompletedSum(MemberDto memberDto) {		
		log.info("본인 누적미션 횟수 ------> " + "Start");						                                                 
		int missionCompletedSum = 0 ;
		
        try {        	
        	missionCompletedSum = memberMapper.missionCompletedSum(memberDto);        	        	        	        	        	
		} catch (DataAccessException e) {
			log.info("본인 누적미션 횟수 ------> " + "Data 접근 실패");    	   
   		 return 0 ;   		 
		}
        
	  return missionCompletedSum ;				 	    			    		   
	}
    
	/**
	 * 누적미션 횟수에 따라 등급 업데이트	 
	 * @return
	 */
	public int updateGrade(MemberDto memberDto) {		
		log.info("등급 업데이트 ------> " + "Start");
		
		int updateGrade = 0 ;
		
        try {
        	updateGrade = memberMapper.updateGrade(memberDto);        	        	        	        	        	
		} catch (DataAccessException e) {
			log.info("본인 누적미션 횟수 ------> " + "Data 접근 실패");    	   
   		 return 0 ;   		 
		}
        
	  return updateGrade ;				 	    			    		   
	}
    
}