package com.upside.api.service;



import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.upside.api.dto.ChallengeDto;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.dto.MemberDto;
import com.upside.api.entity.ChallengeEntity;
import com.upside.api.entity.ChallengeSubmissionEntity;
import com.upside.api.entity.MemberEntity;
import com.upside.api.entity.UserChallengeEntity;
import com.upside.api.repository.ChallengeRepository;
import com.upside.api.repository.ChallengeSubmissionRepository;
import com.upside.api.repository.MemberRepository;
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
	 private final MemberService memberService;
	 private final FileService fileService;
	 	 
	
	
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
		 
		 boolean exsistUserChallenge = userChallengeRepository.findByMemberEntityAndChallengeEntity(member,challenge).isPresent();
		 		 		 
		 if(exsistUserChallenge) {
			 log.info("첼린지 참가 ------> " + "이미 참여하였습니다.");
             result.put("HttpStatus","1.00");		
     		 result.put("Msg","이미 참여하였습니다.");
     	   return result ;
		 }
		 
		 UserChallengeEntity userChallenge =  UserChallengeEntity.builder()
				 							   .memberEntity(member)
				 							   .challengeEntity(challenge)
				 							   .registrationTime(LocalDate.now())
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
	public Map<String, String> submitChallenge (@RequestParam("file") MultipartFile file , ChallengeSubmissionDto submissonDto) throws IOException {
		Map<String, String> result = new HashMap<String, String>();
		
	    log.info("첼린지 제출 ------> " + "Start");
	    
	    System.out.println(submissonDto.getChallengeName());
	    
		Optional<ChallengeEntity> existsChallenge = challengeRepository.findById(submissonDto.getChallengeName());
		
		Optional<MemberEntity> existsMember = memberRepository.findById(submissonDto.getEmail());
						
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
		 	
//	 	FileUploadDto fileUploadDto = new FileUploadDto();
//	 	fileUploadDto.setEmail(submissonDto.getEmail());
//	 	fileUploadDto.setUserFeeling(submissonDto.getSubmissionText());
	 	
	 	// 파일 업로드 
	 	String submissionImageRoute = fileService.uploadFile(file, submissonDto.getEmail());
	 	
	 	// 파일 업로드 성공시 첼린지 인증 성공
	 	if(!submissionImageRoute.equals("N")) {	 			 	
		 	ChallengeSubmissionEntity challengeSubmission = ChallengeSubmissionEntity.builder()
				   											.submissionTime(LocalDate.now()) // 제출 일시
				   											.submissionTitle(submissonDto.getSubmissionTitle()) // 제목
				   											.submissionText(submissonDto.getSubmissionText()) // 내용
				   											.submissionImageRoute(submissionImageRoute)
				   											.userChallenge(userChallenge) // 유저 첼린지 ID 
				   											.submissionCompleted("Y") // 인증 성공 유무
				   											.build();
	        challengeSubmissionRepository.save(challengeSubmission);
	        userChallenge.setCompleted(true);	        	        
	        
	        log.info("첼린지 제출 ------> " + Constants.SUCCESS);
	        result.put("HttpStatus","2.00");		
			result.put("Msg","첼린지 제출이 완료되었습니다.");	       
			log.info("첼린지 제출 ------> " + "End");
			
			
			// 첼린지 미션 제출 후 누적 미션 수 체크 
			MemberDto memberDto = new MemberDto();
			int missionSum = memberService.missionCompletedSum(submissonDto.getEmail());						
			memberDto.setEmail(submissonDto.getEmail());						
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
			}
			
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
