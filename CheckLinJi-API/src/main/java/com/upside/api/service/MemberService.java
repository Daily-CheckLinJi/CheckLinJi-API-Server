package com.upside.api.service;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upside.api.config.JwtTokenProvider;
import com.upside.api.dto.MemberDto;
import com.upside.api.dto.UserChallengeDto;
import com.upside.api.entity.ChallengeEntity;
import com.upside.api.entity.MemberEntity;
import com.upside.api.entity.UserChallengeEntity;
import com.upside.api.mapper.ChallengeMapper;
import com.upside.api.mapper.MemberMapper;
import com.upside.api.repository.ChallengeRepository;
import com.upside.api.repository.MemberRepository;
import com.upside.api.repository.UserChallengeRepository;
import com.upside.api.util.Constants;

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
	private final MemberMapper memberMapper ;
	private final RedisTemplate<String, String> redisTemplate;
	private final FileService fileService ;
	private final ChallengeRepository challengeRepository;
	private final UserChallengeRepository userChallengeRepository;

	
	
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
					    
		try {
					
			Optional<MemberEntity> userInfo = memberRepository.findById(email);
			
			 if(userInfo.isPresent()) {
				 
				 MemberDto memberDto = new MemberDto();
				 memberDto.setEmail(email);
				 
		         Map<String, String> ownData = new HashMap<String, String>();              
		       
		         ownData.put("email", email);
				 
		         // 본인 미션 랭킹 가져오기
				 Map<String, String> missionRankingOwn = memberMapper.OwnRanking(ownData);
			
				 // 미션 정보가 없으면 랭크 0 처리
				 if(missionRankingOwn == null) {
					 missionRankingOwn = new HashMap<String, String>();
					 missionRankingOwn.put("rank", "0");
				 }
				 
				 // 저장된 파일을 Base64로 인코딩
				 String file = fileService.myAuthImage(userInfo.get().getProfile());
				 
				 userInfo.get().setPassword(""); // 조회시 패스워드는 공백 처리
				 userInfo.get().setProfile(file); // 프로필은 base64로 인코딩해서 넘겨줌
				 
				 result.put("HttpStatus","2.00");
				 result.put("Msg",Constants.SUCCESS);
				 result.put("selectMember",userInfo.get());
				 result.put("ownRanking",missionRankingOwn.get("rank"));
				 
				 // 오늘 첼린지 제출 완료 시 Y 아닐 시 N
				 if(memberMapper.missionYn(email) != 0) {
					 result.put("missionSuccess","Y");					 
				 }else {
					 result.put("missionSuccess","N");
				 }
				 
				 log.info("본인 정보 조회 ------> " + Constants.SUCCESS);
				 
			 } else {				 
				 result.put("HttpStatus","1.00");
				 result.put("Msg",Constants.FAIL);
				 log.info("본인 정보 조회 ------> " + Constants.FAIL);
			 }
		 
		} catch (Exception e) {
			 log.error("본인 정보 조회 ------> " + Constants.SYSTEM_ERROR , e);
			 result.put("HttpStatus","1.00");
			 result.put("Msg",Constants.SYSTEM_ERROR);
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
		
		try {
			
			// 값이 존재 하지않는 파라미터가 있는지 확인
			if(memberDto.getEmail() == null || memberDto.getName() == null || memberDto.getNickName() == null || memberDto.getPassword() == null ||
			   memberDto.getBirth() == null || memberDto.getSex() == null ) {  
			    													
				result.put("HttpStatus","1.00");
				result.put("Msg",Constants.NOT_EXIST_PARAMETER);
				log.info("회원가입 실패 ------> " + Constants.NOT_EXIST_PARAMETER);
				return result ; 
			} 						 
			
			 Optional<MemberEntity> isDuplicateId = memberRepository.findById(memberDto.getEmail());
			 
			 if(isDuplicateId.isPresent()) {				 				 
				 result.put("HttpStatus","1.00");
				 result.put("Msg",Constants.DUPLICATE_EMAIL);
				 log.info("회원가입 실패 ------> " + "중복된 이메일 입니다.");
				 return result ; 
			 }
			 
			 // 닉네임 검증 중복 시 1.00 
			 Map<String , String> validateDuplicated = validateDuplicatedNickName(memberDto.getNickName());
			
			 if(validateDuplicated.get("HttpStatus").equals("1.00")) {		 				 
				 result.put("HttpStatus","1.00");
				 result.put("Msg","중복된 닉네임입니다.");
				 log.info("회원가입 실패 ------> " + "중복된 닉네임입니다.");				 
				 return result ;
			 }
			
			String profileName = "";
			 
			// 회원가입 시 기본 프로필 성별 따라 랜덤 지정
			if(memberDto.getSex().equals("M")) {							
				profileName = "M-" + String.valueOf((int)(Math.random() * 5) + 1) + ".png";
			}else if (memberDto.getSex().equals("W")) {				
				profileName = "W-" + String.valueOf((int)(Math.random() * 5) + 1) + ".png";
			}else{				
				profileName = "W-" + String.valueOf((int)(Math.random() * 5) + 1) + ".png";
			}
			
			SimpleDateFormat today = new SimpleDateFormat("yyyy-MM-dd hh:mm");        		
			
			// 회원가입 유저 정보 DB 저장
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
			 	
			 // 유저 정보 DB 저장이 잘 됬는지 확인
			 boolean  exsistUser = memberRepository.findById(memberDto.getEmail()).isPresent();
			 
			 if (exsistUser) {
				
				 // 유저 첼린지 가입 (원래는 사용자가 직접 가입 해야하지만 룰이 좀 바뀌어서 임시로)				 
				 ChallengeEntity challenge =  challengeRepository.findById("첵린지").get();
				 
				 MemberEntity member =  memberRepository.findById(memberDto.getEmail()).get();
				 
				 UserChallengeEntity userChallenge =  UserChallengeEntity.builder()
						   .memberEntity(member)
						   .challengeEntity(challenge)
						   .registrationTime(LocalDateTime.now())
						   .completed(false)
						   .build();
	
			 	 userChallengeRepository.save(userChallenge);
				 
			 	 // 첼린지 가입이 제대로 됬는지 확인
			 	 Boolean exsistUserChallenge = userChallengeRepository.findByMemberEntityAndChallengeEntity(member,challenge).isPresent();
			 	 
				 result.put("HttpStatus","2.00");
				 result.put("UserEmail",memberDto.getEmail());	
				 
				 if(exsistUserChallenge) {
					 result.put("Msg",Constants.SUCCESS); 
				 }else {
					 result.put("Msg","회원가입은 완료했으나 첼린지 참여에 실패하였습니다."); 
				 }
				 
				 log.info("회원가입 성공 ------> " + memberDto.getEmail());
				 
			 }else{				 
				 result.put("HttpStatus","1.00");
				 result.put("Msg",Constants.FAIL);
				 log.info("회원가입 실패 ------> " + Constants.FAIL);
			 }
		 
		} catch (Exception e) {			 
			 result.put("HttpStatus","1.00");
			 result.put("Msg",Constants.SYSTEM_ERROR);
			 log.error("회원가입 실패 ------> " + Constants.SYSTEM_ERROR , e);
		}				 
		 return result ;
	}
	
	/**
	 * 닉네임 검증
	 * @param nickName
	 * @return
	 */
    public Map<String , String> validateDuplicatedNickName(String nickName) {
    	   
    	Map<String , String> result = new HashMap<String, String>();
    	
    	try {
			  
    		// 중복된 닉네임이 있는지 확인
    		if(memberRepository.findByNickName(nickName).isPresent()){	        	
	        	result.put("HttpStatus","1.00");			
				result.put("Msg","중복된 닉네임입니다.");        	         	
				log.info("닉네임 검증 ------> " + "중복된 닉네임입니다.");
    		}else {	        	
	        	result.put("HttpStatus","2.00");			
				result.put("Msg","사용할 수 있는 닉네임입니다.");
				log.info("닉네임 검증 ------> " + "사용할 수 있는 닉네임입니다.");
    		}
        
		} catch (Exception e) {			
        	result.put("HttpStatus","1.00");			
			result.put("Msg",Constants.SYSTEM_ERROR);
			log.error("닉네임 검증 ------> " + Constants.SYSTEM_ERROR , e);
		}
        
        return result ;
    }
	
    /**
     * 이메일 검증
     * @param email
     * @return
     */
    public Map<String , String> validateDuplicatedEmail(String email) {
    	
    	Map<String , String> result = new HashMap<String, String>();
    	
    	try {
			    
    		// 중복된 이메일이 있는지 확인
	        if (memberRepository.findById(email).isPresent()) {	        	
	        	result.put("HttpStatus","1.00");			
				result.put("Msg","중복된 이메일입니다.");        		
				log.info("이메일 검증 ------> " + "중복된 이메일 입니다.");
	        } else {	        	
	        	result.put("HttpStatus","2.00");			
				result.put("Msg","사용할 수있는 이메일입니다.");
				log.info("이메일 검증 ------> " + "사용할 수있는 이메일입니다.");
	        }   
        
		} catch (Exception e) {        	
        	result.put("HttpStatus","1.00");			
			result.put("Msg",Constants.SYSTEM_ERROR);
			log.error("이메일 검증 ------> " + Constants.SYSTEM_ERROR , e);
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
		
		try {
			
			// 값이 존재 하지않는 파라미터가 있는지 확인
			if(memberDto.getEmail() == null) {				
				result.put("HttpStatus","1.00");
				result.put("Msg",Constants.NOT_EXIST_PARAMETER);
				log.info("회원정보 업데이트 실패 ------> " + Constants.NOT_EXIST_PARAMETER);
				return result ; 
			} 
					
			// 가입된 유저인지 확인
			Optional<MemberEntity> user = memberRepository.findById(memberDto.getEmail());		
			
			// 유저 정보가 DB에 없을 경우
			if(!user.isPresent()) { 				
				result.put("HttpStatus","1.00");
				result.put("Msg",Constants.NOT_EXIST_EMAIL);
				log.info("회원정보 업데이트 실패 ------> " + Constants.NOT_EXIST_EMAIL);				
			}else if(memberDto.getPassword() != null && !passwordEncoder.matches(memberDto.getPassword(), user.get().getPassword())) { // 패스워드 변경시				
				 MemberEntity updateUser = user.get();
				 log.info("회원정보 패스워드 업데이트 ------> " + user.get().getEmail());			
				 updateUser.setPassword(passwordEncoder.encode(memberDto.getPassword()));			 
				 result.put("HttpStatus","2.00");
				 result.put("Msg","비밀변호 변경이 완료되었습니다.");		 				 	
			}else {
				 MemberEntity updateUser = user.get();			 
				 updateUser.setName(memberDto.getName());
				 updateUser.setNickName(memberDto.getNickName());			 		 
				 updateUser.setBirth(memberDto.getBirth());			 
				 updateUser.setSex(memberDto.getSex());
				 
				 result.put("HttpStatus","2.00");
				 result.put("Msg","회원정보 수정이 완료되었습니다.");
				 log.info("회원정보 업데이트 ------> " + updateUser.getEmail()); 			 			
			}
		
		} catch (Exception e) {
			log.error("회원정보 업데이트 실패 ------> " + Constants.SYSTEM_ERROR , e);
			result.put("HttpStatus","1.00");
			result.put("Msg",Constants.SYSTEM_ERROR);
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
					 log.info("회원정보 삭제 성공 ------> " + email);
					 result.put("HttpStatus", "2.00");
					 result.put("Msg", Constants.SUCCESS);
				 }else {
					 log.info("회원정보 삭제 실패 ------> " + email);
					 result.put("HttpStatus", "1.00");
					 result.put("Msg", Constants.FAIL);
				 }			 			 			 
			 } else {
				 log.info("회원정보 삭제 실패 ------> " + email);
				 result.put("HttpStatus", "1.00");
				 result.put("Msg", "존재하지 않는 이메일입니다.");
				 	
			 }
		} catch (Exception e) {
			 log.info("회원정보 삭제 실패 ------> " + Constants.SYSTEM_ERROR , e);			 
			 result.put("HttpStatus", "1.00");
			 result.put("Msg", Constants.SYSTEM_ERROR);
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
		
		try {
					
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
		    	
		    	redis.set("accessToken_"+member.getEmail(), jwtTokenProvider.createToken(memberDto.getEmail())); // refresh Token Redis 저장 , 키 값이 같으면 덮어 씌워짐
		    	redis.set("refreshToken_"+member.getEmail(), jwtTokenProvider.createRefreshToken()); // refresh Token Redis 저장 , 키 값이 같으면 덮어 씌워짐		    	
		    	redisTemplate.expire("accessToken_" + member.getEmail(), 1, TimeUnit.HOURS); // redis accessToken expire 1시간 지정
		    	redisTemplate.expire("refreshToken_"+member.getEmail(), 31, TimeUnit.DAYS); // redis refreshToken expire 31일 지정
		    	
		    	
			    result.put("HttpStatus", "2.00");
			    result.put("Token", redis.get("accessToken_"+member.getEmail()));
			    result.put("RefreshToken", redis.get("refreshToken_"+member.getEmail()));
			    result.put("UserEmail", member.getEmail());
			    result.put("Msg", Constants.SUCCESS);
			    
			    log.info("회원 로그인 ------> " + Constants.SUCCESS);
		    }
		    
		} catch (Exception e) {
			log.error("회원 로그인 ------> " + Constants.SYSTEM_ERROR , e); 
			result.put("HttpStatus", "1.00");
	    	result.put("UserEmail", null);
	    	result.put("Msg", Constants.SYSTEM_ERROR);
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
    	 
    	try {
		
    	ValueOperations<String, String> redis = redisTemplate.opsForValue(); // Redis Map 객체 생성
    		
    	if (memberDto.getAccessToken() == null || memberDto.getRefreshToken() == null ) {
    		log.info("Refresh Token 검증 ------> " + Constants.EXPIRATION_TOKEN);
    		
    		result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.EXPIRATION_TOKEN);	        	
	    	return result; 
		}    	
    	
        if (!jwtTokenProvider.validateRefreshTokenExceptExpiration(memberDto.getRefreshToken())) { // 리프레쉬 토큰 만료기간이 지났는지 확인 
        	log.info("Refresh Token 검증 ------> " + Constants.EXPIRATION_TOKEN);
        	
        	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.EXPIRATION_TOKEN);	        	
	    	return result; 
        }
                        
        String email = jwtTokenProvider.getEmail(memberDto.getAccessToken()); // 유효한 토큰이라면 AccessToken으로부터 email을 얻기위해 헤더에서 토큰을 디코딩하는 부분.                
                
        if(email == null) {
        	log.error("Refresh Token 검증 ------> " + "accessToken에 email 값이 없음");
        	
        	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.FAIL);	    	
	    	return result; 
        }
        
        if (redis.get("refreshToken_"+email) == null || !redis.get("refreshToken_"+email).equals(memberDto.getRefreshToken())) {// 파라미터로 입력받은 Refresh Token과 실제 Redis 에 저장된 Refresh Token을 비교하여 검증한다.
        	log.info("Refresh Token 검증 ------> " + Constants.INBALID_TOKEN);
        	result.put("HttpStatus", "1.00");	    	
    		result.put("Msg", Constants.EXPIRATION_TOKEN);
    		
    		return result;   
    		
        } else {
        	 
        	 String accessToken = jwtTokenProvider.createToken(email);
             String refreshToken = jwtTokenProvider.createRefreshToken();
             
             redis.set("accessToken_"+email, accessToken);
             redis.set("refreshToken_"+email, refreshToken);
             
             redisTemplate.expire("accessToken_" +email, 1, TimeUnit.HOURS); // redis accessToken expire 1시간 지정
             redisTemplate.expire("refreshToken_"+email, 31, TimeUnit.DAYS); // redis refreshToken expire 31일 지정
             
                          
                  
             log.info("Refresh Token 검증 ------> " + Constants.SUCCESS);
             result.put("HttpStatus", "2.00");	    	
     		 result.put("Msg", Constants.SUCCESS);
     		 result.put("Token", redis.get("accessToken_"+email));
     		 result.put("RefreshToken", redis.get("refreshToken_"+email));     		      		      	                    	
        	}        
		} catch (Exception e) {
			log.error("Refresh Token 검증 ------> " + Constants.SYSTEM_ERROR , e);        	
        	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.SYSTEM_ERROR);	    	
	    	
		}
    	return result; 
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
    	
    	try {
			    	
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
    	
		} catch (Exception e) {
    		log.error("소셜 로그인 ------> "+ Constants.SYSTEM_ERROR , e);
    		result.put("HttpStatus", "1.00");
    		result.put("Msg", Constants.SYSTEM_ERROR);
	    	result.put("UserEmail", email);	 
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
				log.error("프로필 사진 업데이트 실패 ------> " + Constants.SYSTEM_ERROR , e);
	    		result.put("HttpStatus", "1.00");
	    		result.put("Msg", Constants.SYSTEM_ERROR);
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
		} catch (Exception e) {
			log.error("본인 누적미션 횟수 ------> " + Constants.SYSTEM_ERROR , e);    	   
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
		} catch (Exception e) {
			log.error("본인 누적미션 횟수 ------> " + Constants.SYSTEM_ERROR , e);    	   
   		 return 0 ;   		 
		}
        
	  return updateGrade ;				 	    			    		   
	}
 
	
	/**
	 * 서비스 이용 날짜
	 * @param userEmail
	 * @return
	 */
	public Map<String, Object> joinDate (String userEmail) {
		Map<String, Object> result = new HashMap<String, Object>();
		
		
	    log.info("회원 가입 날짜 ------> " + Constants.SUCCESS);
		                
         try {
					
  
    	 result = memberMapper.joinDate(userEmail);	        
       
		 if(result.get("joinDate") == null ) {
			 result.put("HttpStatus","1.00");		
			 result.put("Msg",Constants.FAIL); 
			 return result ;	
		 }
    	 
    	Date joinDate  =  (Date) result.get("joinDate");
    	Date currentDate = new Date();
    	
    	
    	long differenceInMillis = currentDate.getTime() - joinDate.getTime();
    	long differenceInDays = differenceInMillis / (24 * 60 * 60 * 1000);
    	    	
    	System.out.println(differenceInDays);
    	

    	
    	result.put("joinDate", differenceInDays);
    	
    	
        result.put("HttpStatus","2.00");		
		result.put("Msg",Constants.SUCCESS);
		
		
		log.info("회원 가입 날짜 ------> " + Constants.SUCCESS);
		
		} catch (Exception e) {
			 log.error("회원 가입 날짜 ------> " + Constants.SYSTEM_ERROR , e);
			 result.put("HttpStatus","1.00");		
			 result.put("Msg","Data 접근 실패");
			 
		}
       
	    return result ;			    		   
	}
	
	
	/**
	 * 패스워드 변경 ( 로그인 한 상태 ) 
	 * @param memberDto
	 * @return
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> changePassword (MemberDto memberDto) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		try {
							
		Optional<MemberEntity> memberEntity = memberRepository.findById(memberDto.getEmail());
		
		// 이메일이 없을경우 
		if(!memberEntity.isPresent()) {
			log.info("패스워드 변경 ( 로그인 한 상태 ) ------> " + Constants.INBALID_EMAIL); 
			result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.INBALID_EMAIL);
	    	 return result ;
		}
								     
	    if (!passwordEncoder.matches(memberDto.getPassword(), memberEntity.get().getPassword())) {
	    	log.info("패스워드 변경 ( 로그인 한 상태 ) ------> " + Constants.INBALID_PASSWORD);
	    	result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.INBALID_PASSWORD);		    	
	    } else {
	    	MemberEntity member = memberEntity.get();			 			
	    	member.setPassword(passwordEncoder.encode(memberDto.getAfterPassword()));	
		    result.put("HttpStatus", "2.00");
		    result.put("Msg", Constants.SUCCESS);		
		    jwtTokenProvider.deleteToken(memberDto.getAccessToken()); // redis 에 저장되어있던 Token 삭제 		    
		    log.info("패스워드 변경 ( 로그인 한 상태 ) ------> " + Constants.SUCCESS);
	    }
	    
		} catch (Exception e) {
		    result.put("HttpStatus", "1.00");
		    result.put("Msg", Constants.SYSTEM_ERROR);	
			log.error("패스워드 변경 ( 로그인 한 상태 ) ------> " + Constants.SYSTEM_ERROR , e);			
		}
	    return result ;			    	
	}
	
	
	/**
	 * 패스워드 변경 ( 비밀번호 찾기 ) 
	 * @param memberDto
	 * @return
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> changePasswordMail (MemberDto memberDto) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		try {
							
		Optional<MemberEntity> memberEntity = memberRepository.findById(memberDto.getEmail());
		
		// 이메일이 없을경우 
		if(!memberEntity.isPresent()) {
			log.info("패스워드 변경 ( 비밀번호 찾기 ) ------> " + Constants.INBALID_EMAIL); 
			result.put("HttpStatus", "1.00");	    	
	    	result.put("Msg", Constants.INBALID_EMAIL);
	    	 return result ;
		}
								     
    	MemberEntity member = memberEntity.get();			 			
    	member.setPassword(passwordEncoder.encode(memberDto.getPassword()));	
	    result.put("HttpStatus", "2.00");
	    result.put("Msg", Constants.SUCCESS);		    
	    log.info("패스워드 변경 ( 비밀번호 찾기 ) ------> " + Constants.SUCCESS);
	    
		} catch (Exception e) {
		    result.put("HttpStatus", "1.00");
		    result.put("Msg", Constants.SYSTEM_ERROR);	
		    log.error("패스워드 변경 ( 비밀번호 찾기 ) ------> " + Constants.SYSTEM_ERROR , e);			
		}
	    return result ;			    	
	}
	
	/**
	 * 총 사용자 수 
	 * @param userEmail
	 * @return
	 */
	public Map<String, String> findMemCnt () {
		
		Map<String, String> result = new HashMap<String, String>();
				
		log.info("총 사용자 수 -----------------> Start " );
	
       
       try {
					  	    	    	    
	    result.put("memberCnt",String.valueOf(memberRepository.count()));
        result.put("HttpStatus","2.00");		
		result.put("Msg",Constants.SUCCESS);
		
		
		log.info("총 사용자 수 -----------------> " + Constants.SUCCESS);
		
		} catch (Exception e) {
			 result.put("HttpStatus","1.00");		
			 result.put("Msg",Constants.SYSTEM_ERROR);			 
			 log.error("총 사용자 수 -----------------> " + Constants.SYSTEM_ERROR , e);
		}
       
	    return result ;			    		   
	}
	
}