package com.upside.api.service;



import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.upside.api.dto.ChallengeDto;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.dto.MemberDto;
import com.upside.api.dto.PageDto;
import com.upside.api.dto.UserChallengeDto;
import com.upside.api.entity.ChallengeEntity;
import com.upside.api.entity.ChallengeSubmissionEntity;
import com.upside.api.entity.HashTagEntity;
import com.upside.api.entity.MemberEntity;
import com.upside.api.entity.SubmissionHashTagEntity;
import com.upside.api.entity.UserChallengeEntity;
import com.upside.api.mapper.ChallengeMapper;
import com.upside.api.repository.ChallengeRepository;
import com.upside.api.repository.ChallengeSubmissionRepository;
import com.upside.api.repository.HashTagRepository;
import com.upside.api.repository.MemberRepository;
import com.upside.api.repository.SubmissionHashTagRepository;
import com.upside.api.repository.UserChallengeRepository;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RequiredArgsConstructor
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@Service
public class ChallengeService {
		
	 private final ChallengeRepository challengeRepository;
	 private final UserChallengeRepository userChallengeRepository;
	 private final MemberRepository memberRepository;
	 private final ChallengeSubmissionRepository challengeSubmissionRepository;
	 private final SubmissionHashTagRepository submissionHashTagRepository;
	 private final HashTagRepository hashTagRepository;
	 private final MemberService memberService;
	 private final FileService fileService;
	 private final ChallengeMapper challengeMapper;
	 	 
		 
	 /**
		 * 첼린지 인증글 리스트
		 * @param memberDto
		 * @param challengeDto
		 * @return
		 */
		@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
		public Map<String, Object> viewChallengeList (PageDto pageDto) {
			Map<String, Object> result = new HashMap<String, Object>();
			
			log.info("첼린지 인증글 리스트 ------> " + "Start");
									 						 					
			  try {
		        	ArrayList<Map<String, Object>> viewChallengeList = challengeMapper.viewChallengeList(pageDto);
		        	        	        	
		        	if (viewChallengeList.size() != 0 ) { 
		        		for(int i = 0; i < viewChallengeList.size(); i++) { // 리스트 사이즈만큼 돌면서 DB에 저장된 이미지 경로로 이미지를 base64로 인코딩해서 값 덮어씌우기
		        			String image = fileService.myAuthImage((String) viewChallengeList.get(i).get("SUBMISSION_IMAGE"));
		        				if(!image.equals("N")) {
		        					viewChallengeList.get(i).put("SUBMISSION_IMAGE", image);
		        				}
		        		}
		        	 	log.info("첼린지 인증글 리스트 ------> " + Constants.SUCCESS);
		        	   	result.put("HttpStatus","2.00");		
		      			result.put("Msg",Constants.SUCCESS);
		      			result.put("viewChallengeList",viewChallengeList);		        		
		       		 
		           } else {
		        	   log.info("첼린지 인증글 리스트 ------> " + "게시글이 없습니다.");
		        	    result.put("HttpStatus","1.00");		
		       			result.put("Msg","게시글이 없습니다.");
		       			return result ;
		           }
		        	
				} catch (DataAccessException e) {
					log.info("첼린지 인증글 리스트 ------> " + "Data 접근 실패");					
		    	    result.put("HttpStatus","1.00");		
		   			result.put("Msg","Data 접근 실패");
		   			e.printStackTrace();
		   		 return result ;			
				}               		 
			  return result ;					 	    		   
	}
	 
		
		
		 /**
		 * 첼린지 인증글 상세페이지
		 * @param memberDto
		 * @param challengeDto
		 * @return
		 */
		@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
		public Map<String, Object> detail (ChallengeSubmissionDto submissionDto) {
			Map<String, Object> result = new HashMap<String, Object>();
			
			log.info("첼린지 인증글 상세페이지 ------> " + "Start");
									 						 					
			  try {
				  HashMap<String,String> submissionDetail = challengeMapper.detail(submissionDto);
			
		        	if (submissionDetail.size() != 0 ) { 		        	
		        		String image = fileService.myAuthImage(submissionDetail.get("SUBMISSION_IMAGE_ROUTE"));
		        		if(image.equals("N")) {
		        			submissionDetail.put("SUBMISSION_IMAGE_ROUTE", "파일을 표시할 수 없습니다...");
		        		}else {
		        			submissionDetail.put("SUBMISSION_IMAGE_ROUTE", image);
		        		}
		        		
		        		int likesCount = challengeMapper.likesCount(submissionDto);
		        		ArrayList<Map<String, Object>> commentList = challengeMapper.commentList(submissionDto);
		        		
		        	 	log.info("첼린지 인증글 상세페이지 ------> " + Constants.SUCCESS);
		        	   	result.put("HttpStatus","2.00");		
		      			result.put("Msg",Constants.SUCCESS);
		      			result.put("submissionDetail",submissionDetail);
		      			result.put("likesCount",likesCount);
		      			result.put("commentList",commentList);
		       		 
		           } else {
		        	   log.info("첼린지 인증글 상세페이지 ------> " + "게시글이 없습니다.");
		        	    result.put("HttpStatus","1.00");		
		       			result.put("Msg","게시글이 없습니다.");
		       			return result ;
		           }
		        	
				} catch (DataAccessException e) {
					log.info("첼린지 인증글 상세페이지 ------> " + "Data 접근 실패");
		    	    result.put("HttpStatus","1.00");		
		   			result.put("Msg","Data 접근 실패");
		   		 return result ;			
				}               		 
			  return result ;					 	    		   
	}
	 
	 /**
		 * 본인 첼린지 참가 내역
		 * @param memberDto
		 * @param challengeDto
		 * @return
		 */
		@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
		public Map<String, Object> viewChallenge (String email) {
			Map<String, Object> result = new HashMap<String, Object>();
			
			log.info("첼린지 참가 내역 ------> " + "Start");
			
						 			
			 Optional<MemberEntity> existsMember = memberRepository.findById(email);
					
			
			if(!existsMember.isPresent()) {
				 log.info("첼린지 참가 내역 ------> " + "이메일이 존재하지 않습니다.");
	             result.put("HttpStatus","1.00");		
	     		 result.put("Msg","이메일이 존재하지 않습니다.");
	     	   return result ;
			}				
						 
							 
			MemberEntity member =  existsMember.get();
			 
			 List<UserChallengeDto> challenge_list = new ArrayList<UserChallengeDto>() ;
			 
			 List<UserChallengeEntity> exsistUserChallenge = userChallengeRepository.findByMemberEntity(member);
			 
			 for(UserChallengeEntity as : exsistUserChallenge) {
				 UserChallengeDto i = new UserChallengeDto();
				 i.setChallengeName(as.getChallengeEntity().getChallengeName());
				 challenge_list.add(i);
			 }
			 
			 if(exsistUserChallenge == null) {
				 log.info("첼린지 참가 내역 ------> " + "참가중인 첼린지가 없습니다.");
	             result.put("HttpStatus","1.00");		
	     		 result.put("Msg","참가중인 첼린지가 없습니다.");
	     	   return result ;
			 }
			 			
			 		 
		         log.info("첼린지 참가 내역 ------> " + Constants.SUCCESS);
		         result.put("HttpStatus","2.00");		
				 result.put("Msg",Constants.SUCCESS);
				 result.put("Challenge",challenge_list);
				 log.info("첼린지 참가 내역 ------> " + "End");
			  return result ;				 	    		   
	}
	 
	
		/**
		  * 본인 미션 성공 총 횟수 (월)
		  * @param memberDto
		  * @return
		  */
		public Map<String, String> missionCompletedCnt (String userEmail) {
			Map<String, String> result = new HashMap<String, String>();
							
			 // 현재 날짜와 시간을 LocalDateTime 객체로 가져옵니다.
	        LocalDateTime now = LocalDateTime.now();
	        
	        try {
						
	        // 현재 년도와 월을 가져옵니다.
	        int year = now.getYear();
	        int month = now.getMonthValue();
	                                                 		
	        Map<String, String> data = new HashMap<String, String>();
	        
	        data.put("year", String.valueOf(year));
	        data.put("month", String.valueOf(month));
	        data.put("email", userEmail);
	        
	        int completedCnt = challengeMapper.missionCompletedCnt(data);	        
	        
	        result.put("HttpStatus","2.00");		
			result.put("Msg",Constants.SUCCESS);
			result.put("completedCnt",String.valueOf(completedCnt));
			
			log.info("미션 성공 총 횟수 (월) ------> " + Constants.SUCCESS);
			
			} catch (Exception e) {
				 result.put("HttpStatus","1.00");		
				 result.put("Msg","Data 접근 실패");			
			}
	        
		    return result ;			    		   
		}
		
		
		/**
		  * 본인 미션 성공 총 횟수
		  * @param memberDto
		  * @return
		  */
		public Map<String, String> missionCompletedCntAll (String userEmail) {
			Map<String, String> result = new HashMap<String, String>();
							
			 // 현재 날짜와 시간을 LocalDateTime 객체로 가져옵니다.
	        LocalDateTime now = LocalDateTime.now();
	        
	        try {
						
	        // 현재 년도와 월을 가져옵니다.
	        int year = now.getYear();
	        int month = now.getMonthValue();
	                                                 		
	        Map<String, String> data = new HashMap<String, String>();
	        
	        data.put("year", String.valueOf(year));
	        data.put("month", String.valueOf(month));
	        data.put("email", userEmail);
	        
	        int completedCnt = challengeMapper.missionCompletedCntAll(data);	        
	        
	        result.put("HttpStatus","2.00");		
			result.put("Msg",Constants.SUCCESS);
			result.put("completedCnt",String.valueOf(completedCnt));
			
			log.info("미션 성공 총 횟수 (월) ------> " + Constants.SUCCESS);
			
			} catch (Exception e) {
				 result.put("HttpStatus","1.00");		
				 result.put("Msg","Data 접근 실패");			
			}
	        
		    return result ;			    		   
		}
		
	 
	/**
	 * 첼린지 생성
	 * @param challengeDTO
	 * @return
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> createChallenge (ChallengeDto challengeDto) {
		Map<String, String> result = new HashMap<String, String>();
		
		log.info("첼린지 생성 ------> " + "Start");
		
		 // 현재 날짜와 시간을 LocalDateTime 객체로 가져옵니다.
        LocalDateTime now = LocalDateTime.now();
        
        // 현재 년도와 월을 가져옵니다.
        int year = now.getYear();
//        int month = now.getMonthValue();
        int month = Integer.parseInt(challengeDto.getStartTime());
        
        // YearMonth 객체를 생성하여 해당 월의 날짜 범위를 가져옵니다.
        YearMonth yearMonth = YearMonth.of(year, month);
        int daysInMonth = yearMonth.lengthOfMonth();
        
        // 월초와 월말의 날짜를 LocalDateTime 객체로 생성합니다.
        LocalDateTime startOfMonth = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endOfMonth = LocalDateTime.of(year, month, daysInMonth, 23, 59, 59);
        
        // 월초부터 월말까지의 날짜 범위를 생성합니다.
        LocalDate startDate = startOfMonth.toLocalDate();
        LocalDate endDate = endOfMonth.toLocalDate();
		
		
        boolean existsChallenge = challengeRepository.findById(challengeDto.getChallengeName()).isPresent();
        
        if(existsChallenge == true) {
        	 log.info("첼린지 생성 ------> " + "존재하는 첼린지입니다.");
             result.put("HttpStatus","1.00");		
      		 result.put("Msg","존재하는 첼린지입니다.");
      		 return result;
        }
		
		
		ChallengeEntity challenge =  ChallengeEntity.builder()
									 .challengeName(challengeDto.getChallengeName())
									 .description(challengeDto.getDescription())
									 .startTime(startDate)
									 .endTime(endDate)
									 .build();
		
		
		
        challengeRepository.save(challenge);
        
        log.info("첼린지 생성 ------> " + Constants.SUCCESS);
        result.put("HttpStatus","2.00");		
		result.put("Msg",Constants.SUCCESS);
	
	    return result ;			    		   
	}
	
	/**
	 * 첼린지 참가
	 * @param memberDto
	 * @param challengeDto
	 * @return
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> joinChallenge (String challengeName , String email) {
		Map<String, String> result = new HashMap<String, String>();
		
		log.info("첼린지 참가 ------> " + "Start");
		
		
		 Optional<ChallengeEntity> existsChallenge = challengeRepository.findById(challengeName);
		
		 Optional<MemberEntity> existsMember = memberRepository.findById(email);
				
		
		if(!existsChallenge.isPresent() || !existsMember.isPresent()) {
			 log.info("첼린지 참가 ------> " + "첼린지 혹은 이메일이 존재하지 않습니다.");
             result.put("HttpStatus","1.00");		
     		 result.put("Msg","첼린지 혹은 이메일이 존재하지 않습니다.");
     	   return result ;
		}				
		
		 ChallengeEntity challenge =  existsChallenge.get();
						 
		 MemberEntity member =  existsMember.get();
		 
		 Optional<UserChallengeEntity> exsistUserChallenge = userChallengeRepository.findByMemberEntityAndChallengeEntity(member,challenge);
		 		 
		 		 		 
		 if(exsistUserChallenge.isPresent()) {
			 log.info("첼린지 참가 ------> " + "이미 참여하였습니다.");
             result.put("HttpStatus","1.00");		
     		 result.put("Msg","이미 참여하였습니다.");
     	   return result ;
		 }
		 
		 UserChallengeEntity userChallenge =  UserChallengeEntity.builder()
				 							   .memberEntity(member)
				 							   .challengeEntity(challenge)
				 							   .registrationTime(LocalDateTime.now())
				 							   .completed(false)
				 							   .build();
		 
		 	userChallengeRepository.save(userChallenge);
		 		 
	         log.info("첼린지 참가 ------> " + Constants.SUCCESS);
	         result.put("HttpStatus","2.00");		
			 result.put("Msg",Constants.SUCCESS);	       
			 log.info("첼린지 참가 ------> " + "End");
		  return result ;				 	    		   
}
	
	
	/**
	 * 첼린지 제출
	 * @param challengeDTO
	 * @return
	 * @throws IOException 
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> submitChallenge (ChallengeSubmissionDto submissonDto , String userEmail) throws IOException {
		Map<String, String> result = new HashMap<String, String>();
		
	    log.info("첼린지 제출 ------> " + "Start");	    
	    
		Optional<ChallengeEntity> existsChallenge = challengeRepository.findById(submissonDto.getChallengeName());
		
		Optional<MemberEntity> existsMember = memberRepository.findById(userEmail);
						
		if(!existsChallenge.isPresent() || !existsMember.isPresent()) {
			 log.info("첼린지 참가 ------> " + "첼린지 혹은 이메일이 존재하지 않습니다.");
            result.put("HttpStatus","1.00");		
    		 result.put("Msg","첼린지 혹은 이메일이 존재하지 않습니다.");
    	   return result ;
		 }				
		
		 ChallengeEntity challenge =  existsChallenge.get();
						 
		 MemberEntity member =  existsMember.get();
		
	 	Optional<UserChallengeEntity>  exsistUserChallenge = userChallengeRepository.findByMemberEntityAndChallengeEntity(member,challenge); // 첼린지 참가를 했는지 확인
	 	 
	 	 if(!exsistUserChallenge.isPresent()) {
	 		 log.info("첼린지 제출 ------> " + "첼린지에 참여하고 계시지 않습니다.");
             result.put("HttpStatus","1.00");		
     		 result.put("Msg","첼린지에 참여하고 계시지 않습니다.");
     	   return result ; 
	 	 }
	 	 		 	
	 	UserChallengeEntity userChallenge = exsistUserChallenge.get();		 			 	
	 	
	 	boolean submitYn = challengeSubmissionRepository.findByUserChallengeAndSubmissionTime(userChallenge, LocalDate.now()).isPresent();
	 	 
	 	if(submitYn) {
	 		 log.info("첼린지 제출 ------> " + "오늘은 이미 제출이 완료되었습니다.");
             result.put("HttpStatus","1.00");		
     		 result.put("Msg","오늘은 이미 제출이 완료되었습니다.");
     	   return result ; 
		 	}
		 	
	 	
	 	// 파일 업로드 
	 	String submissionImageRoute = fileService.uploadFile(submissonDto.getSubmissionImageRoute(),submissonDto.getSubmissionImageName() ,userEmail);
	 	
	 	// 파일 업로드 성공시 첼린지 인증 성공
	 	if(!submissionImageRoute.equals("N")) {	 			 	
		 	ChallengeSubmissionEntity challengeSubmission = ChallengeSubmissionEntity.builder()
				   											.submissionTime(LocalDateTime.now()) // 제출 일시				   											
				   											.submissionText(submissonDto.getSubmissionText()) // 내용
				   											.nickName(member.getNickName()) // 닉네임
				   											.submissionImageRoute(submissionImageRoute) // 이미지 경로
				   											.userChallenge(userChallenge) // 유저 첼린지 ID 
				   											.submissionCompleted("Y") // 인증 성공 유무
				   											.build();
	        challengeSubmissionRepository.save(challengeSubmission);	                	       
	        
	        	             
			log.info("첼린지 제출 ------> " + Constants.SUCCESS);
			
			
			Optional<ChallengeSubmissionEntity> successYn = challengeSubmissionRepository.findByUserChallengeAndSubmissionTime(userChallenge, LocalDate.now());
				
			String tagExistN = "" ; // 태그 존재 유무
			
			System.out.println("successYn.isPresent() "+ successYn.isPresent());
			
			if(successYn.isPresent()) {								
				
		    	List<String> list = Arrays.asList(submissonDto.getHashTag().split("\\|")); // hashTag | 기준으로 잘라서 리스트에 넣기    	
		    	    	    
		    	
		    	for(int i = 0 ; list.size() > i; i++) { // 리스트 사이즈만큼 돌면서 map에 담기
		    		Optional<HashTagEntity> tagExistYn = hashTagRepository.findByTagName(list.get(i));
		    		if(tagExistYn.isPresent()) {		    			
		    			SubmissionHashTagEntity hashTagEntity = SubmissionHashTagEntity.builder()
								.challengeSubmissionId(successYn.get())
								.hashTagId(tagExistYn.get())						
								.build();
			    		submissionHashTagRepository.save(hashTagEntity);
			    		log.info("미션인증 해쉬태그 저장 ------> "+list.size() +" 중 " +i+ "번째 " + Constants.SUCCESS);
		    		}else {
		    			tagExistN += list.get(i)+ "," ;  
		    		}		
		    	}

			}
			// 첼린지 미션 제출 후 누적 미션 수 체크 
			MemberDto memberDto = new MemberDto();
			memberDto.setEmail(userEmail);
			int missionSum = memberService.missionCompletedSum(memberDto);						
									
			int updateGrade = 0 ;
			
			// 누적 미션 횟수에 따른 등급 업데이트
			if(missionSum == 0 ) {
				result.put("HttpStatus","2.00");		
				result.put("Msg","첼린지 제출이 완료되었으나 예상치 못한 에러로 등급 업데이트에 실패하였습니다.");	
			}else if(missionSum >= 0 && missionSum <= 19) { // 0~19회 책갈피
				memberDto.setGrade("책갈피");
				updateGrade = memberService.updateGrade(memberDto);		
			}else if(missionSum >= 20 && missionSum <= 49) { // 20~49회 책린이
				memberDto.setGrade("책린이");
				updateGrade = memberService.updateGrade(memberDto);						
			}else if(missionSum >= 50 && missionSum <= 79) { // 50~79회 책벌레
				memberDto.setGrade("책벌레");
				updateGrade = memberService.updateGrade(memberDto);				
			}else if(missionSum >= 80 && missionSum <= 99) { // 50~79회 책탐험가
				memberDto.setGrade("책탐험가");
				updateGrade = memberService.updateGrade(memberDto);				
			}else if(missionSum >= 100) { // 100회 책박사
				memberDto.setGrade("책박사");
				updateGrade = memberService.updateGrade(memberDto);
			}
						
			log.info("등급 업데이트 결과 0(실패),1(성공) ----->" + updateGrade);
			
			// 등급 업데이트 실패 메시지 
			if(updateGrade == 0 ) {
				result.put("HttpStatus","2.00");		
				result.put("Msg","첼린지 제출이 완료되었으나 예상치 못한 에러로 등급 업데이트에 실패하였습니다.");	
				return result ;
			}
			
			if(!tagExistN.equals("")) {
				tagExistN += " -- 등록된 태그가 아니기 때문에 추가에서 제외 되었습니다."; 				
				result.put("tagExistN",tagExistN);	
			}
			 result.put("HttpStatus","2.00");		
			 result.put("Msg","첼린지 제출이 완료되었습니다.");	 
						
	 	} else {
	 		 log.info("첼린지 제출 ------> " + Constants.FAIL);
		        result.put("HttpStatus","1.00");		
				result.put("Msg",Constants.FAIL);	       
				log.info("첼린지 제출 ------> " + "End");				
	 	}
	 	return result ;
	}
	
	
	/**
	 * 첼린지 완료
	 * @param challengeDTO
	 * @return
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> completedChallenge (String challengeName , String email) {
		Map<String, String> result = new HashMap<String, String>();
		
		log.info("첼린지 완료 ------> " + "Start");				
	        
	    return result ;			    		   
	}
	
	}
