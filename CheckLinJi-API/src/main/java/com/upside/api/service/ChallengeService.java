package com.upside.api.service;



import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.dto.MemberDto;
import com.upside.api.dto.PageDto;
import com.upside.api.entity.ChallengeSubmissionEntity;
import com.upside.api.entity.HashTagEntity;
import com.upside.api.entity.MemberEntity;
import com.upside.api.entity.SubmissionHashTagEntity;
import com.upside.api.mapper.ChallengeMapper;
import com.upside.api.mapper.MemberMapper;
import com.upside.api.repository.ChallengeSubmissionRepository;
import com.upside.api.repository.HashTagRepository;
import com.upside.api.repository.MemberRepository;
import com.upside.api.repository.ReportSubmissionRepository;
import com.upside.api.repository.SubmissionHashTagRepository;
import com.upside.api.repository.UserChallengeRepository;
import com.upside.api.util.Constants;
import com.upside.api.util.DateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RequiredArgsConstructor
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@Service
public class ChallengeService {
		
	 
	 private final UserChallengeRepository userChallengeRepository;
	 private final MemberRepository memberRepository;
	 private final ChallengeSubmissionRepository challengeSubmissionRepository;
	 private final SubmissionHashTagRepository submissionHashTagRepository;
	 private final HashTagRepository hashTagRepository;
	 private final MemberService memberService;
	 private final MemberMapper memberMapper;
	 private final FileService fileService;
	 private final ChallengeMapper challengeMapper;
	 private final ReportSubmissionRepository reportSubmissionRepository;
	 	 
	
	 
	 
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
				  
					if(pageDto.getTag() != null) {
						pageDto.setTagList(Arrays.asList(pageDto.getTag().split("\\|")));  // hashTag | 기준으로 잘라서 리스트에 넣기
					}
				  	
					// 첼린지 인증글 리스트 가져오기
		        	ArrayList<Map<String, Object>> viewChallengeList = challengeMapper.viewChallengeList(pageDto);
		        	        	        	
		        	// 리스트 사이즈만큼 돌면서 DB에 저장된 이미지 경로로 이미지를 base64로 인코딩해서 값 덮어씌우기
		        	if (viewChallengeList.size() != 0 ) { 
		        		for(int i = 0; i < viewChallengeList.size(); i++) { 
		        			String image = fileService.myAuthImage((String) viewChallengeList.get(i).get("SUBMISSION_IMAGE"));	       			
		        			viewChallengeList.get(i).put("SUBMISSION_IMAGE", image);
		        		}
		        		
		        	 	log.info("첼린지 인증글 리스트 ------> " + Constants.SUCCESS);
		        	   	result.put("HttpStatus","2.00");		
		      			result.put("Msg",Constants.SUCCESS);
		      			result.put("totalCount",challengeMapper.listTotalCnt(pageDto));
		      			result.put("viewChallengeList",viewChallengeList);		        		
		       		 
		           } else {
		        	   log.info("첼린지 인증글 리스트 ------> " + "게시글이 없습니다.");
		        	    result.put("HttpStatus","1.00");		
		       			result.put("Msg","게시글이 없습니다.");
		       			return result ;
		           }
		        	
				} catch (Exception e) {
					log.error("첼린지 인증글 리스트 ------> " + Constants.SYSTEM_ERROR , e);					
		    	    result.put("HttpStatus","1.00");		
		   			result.put("Msg",Constants.SYSTEM_ERROR);		   			
		   		 return result ;			
				}               		 
			  return result ;					 	    		   
	}
	 
	 
	 
		/**
		 * 유저 첼린지 인증글 리스트
		 * @param memberDto
		 * @param challengeDto
		 * @return
		 */
		@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
		public Map<String, Object> userChallengeList (PageDto pageDto) {
			Map<String, Object> result = new HashMap<String, Object>();
			
			log.info("유저 첼린지 인증글 리스트 ------> " + "Start");			
									 						 					
			  try {
				  
					if(pageDto.getTag() != null) {
						pageDto.setTagList(Arrays.asList(pageDto.getTag().split("\\|")));  // hashTag | 기준으로 잘라서 리스트에 넣기
					}
				  		
					// 유저 첼린지 인증글 리스트 가져오기
		        	ArrayList<Map<String, Object>> userChallengeList = challengeMapper.userChallengeList(pageDto);
		        	        	        	
		        	// 리스트 사이즈만큼 돌면서 DB에 저장된 이미지 경로로 이미지를 base64로 인코딩해서 값 덮어씌우기
		        	
		        	 
	        		for(int i = 0; i < userChallengeList.size(); i++) { 
	        			String image = fileService.myAuthImage((String) userChallengeList.get(i).get("SUBMISSION_IMAGE"));
	        				if(!image.equals("N")) {
	        					userChallengeList.get(i).put("SUBMISSION_IMAGE", image);
	        				}
	        		}
	        		
	        		// 사용자의 가입 날짜 가져오기
	        		Optional<MemberEntity> user = memberRepository.findById(pageDto.getEmail());		        			        		
	        		
	        		// 사용자의 가입일로부터 현재까지의 경과 일수를 계산
	            	long differenceInDays =  DateTime.userJoinDate(user.get().getJoinDate());
	            	
	            	HashMap<String, Object> memberInfo = new HashMap<String, Object>();
	            			        		
	        		memberInfo.put("nickName", user.get().getNickName());
	        		memberInfo.put("profile", fileService.myAuthImage(user.get().getProfile()));
	        		memberInfo.put("grade", user.get().getGrade());
	        		memberInfo.put("totalMissionSuccessCnt", memberMapper.missionCompletedCntAll(pageDto.getEmail()));
	        		memberInfo.put("joinDate", differenceInDays);
	        					        		
	        		Map<String, String> data = new HashMap<String, String>();              
	   		       
	        		data.put("email", pageDto.getEmail());
	 				 
	 		        // 유저 미션 랭킹 가져오기
	 				Map<String, String> missionRankingUser = memberMapper.OwnRanking(data);
	 						 						 				
	 				 // 미션 정보가 없으면 랭크 0 처리
					 if(missionRankingUser == null) {
						 missionRankingUser = new HashMap<String, String>();
						 missionRankingUser.put("rank", "0");
					 }
	        				        		
					 missionRankingUser.remove("email");
					 
	        	   	result.put("HttpStatus","2.00");		
	      			result.put("Msg",Constants.SUCCESS);
	      			result.put("userInfo",memberInfo); // 유저 정보
	      			result.put("missionTotalList",challengeMapper.listTotalCnt(pageDto)); // 사용 안하는거 같음 
	      			result.put("userChallengeList",userChallengeList); // 유저 게시글 리스트
	      			result.put("ranking",missionRankingUser); // 유저 게시글 리스트
	      			
	       		 
	      			log.info("첼린지 인증글 리스트 ------> " + Constants.SUCCESS);
	           
		        	
		        	
				} catch (Exception e) {
					log.error("첼린지 인증글 리스트 ------> " + Constants.SYSTEM_ERROR , e);					
		    	    result.put("HttpStatus","1.00");		
		   			result.put("Msg",Constants.SYSTEM_ERROR);		   			
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
				  
				   // 게시글 상세정보 가져오기
				   HashMap<String,String> submissionDetail = challengeMapper.detail(submissionDto);
				   					   
				    // 리스트 사이즈만큼 돌면서 DB에 저장된 이미지 경로로 이미지를 base64로 인코딩해서 값 덮어씌우기
		        	if (submissionDetail.size() != 0 ) { 		        	
		        		String image = fileService.myAuthImage(submissionDetail.get("SUBMISSION_IMAGE_ROUTE"));
		        		if(image.equals("N")) {
		        			submissionDetail.put("SUBMISSION_IMAGE_ROUTE", "파일을 표시할 수 없습니다...");
		        		}else {
		        			submissionDetail.put("SUBMISSION_IMAGE_ROUTE", image);
		        		}
		        		
		        		// 좋아요 갯수 가져오기
		        		int likesCount = challengeMapper.likesCount(submissionDto);
		        		ArrayList<Map<String, Object>> commentList = challengeMapper.commentList(submissionDto);
		        		
		        		// 댓글 가져오기
		        		for(int i = 0 ; i < commentList.size(); i++) {
		        			 commentList.get(i).put("PROFILE", fileService.myAuthImage((String) commentList.get(i).get("PROFILE")));		        			
		        		}
		        				        		
		        		// 게시글 신고 유무
		        		Long existsReport = reportSubmissionRepository.countByChallengeSubmissionIdAndEmail(Long.valueOf(submissionDto.getChallengeSubmissionId()),submissionDto.getEmail());
		  			  		  			 		        				        	
		        	 	log.info("첼린지 인증글 상세페이지 ------> " + Constants.SUCCESS);
		        	   	result.put("HttpStatus","2.00");		
		      			result.put("Msg",Constants.SUCCESS);
		      			result.put("submissionDetail",submissionDetail);
		      			result.put("likesCount",likesCount);
		      			result.put("commentList",commentList);		      			
		      			 // 같은 게시글을 신고한 이력이 있으면 이미 신고되었음 처리
			  			  if(existsReport != 0) { 
			  				  result.put("existsReport","Y");
			  			  }else {
			  				  result.put("existsReport","N");
			  			  }
		       		 
		           } else {
		        	   log.info("첼린지 인증글 상세페이지 ------> " + "게시글이 없습니다.");
		        	    result.put("HttpStatus","1.00");		
		       			result.put("Msg","게시글이 없습니다.");
		       			return result ;
		           }
		        	
				} catch (Exception e) {
					log.error("첼린지 인증글 상세페이지 ------> " + Constants.SYSTEM_ERROR , e);
		    	    result.put("HttpStatus","1.00");		
		   			result.put("Msg",Constants.SYSTEM_ERROR);
		   		 return result ;			
				}               		 
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
	        
	        // 미션 성공 횟수 가져오기
	        int completedCnt = challengeMapper.missionCompletedCnt(data);	        
	        
	        result.put("HttpStatus","2.00");		
			result.put("Msg",Constants.SUCCESS);
			result.put("completedCnt",String.valueOf(completedCnt));
			
			log.info("미션 성공 총 횟수 (월) ------> " + Constants.SUCCESS);
			
			} catch (Exception e) {
				 result.put("HttpStatus","1.00");		
				 result.put("Msg",Constants.SYSTEM_ERROR);
				 log.error("본인 미션 성공 총 횟수 (월) ---> " + Constants.SYSTEM_ERROR , e);
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
								        
	        try {
                                                 		
	        Map<String, String> data = new HashMap<String, String>();
	        

	        data.put("email", userEmail);
	        
	        int completedCnt = challengeMapper.missionCompletedCntAll(data);	        
	        
	        result.put("HttpStatus","2.00");		
			result.put("Msg",Constants.SUCCESS);
			result.put("completedCnt",String.valueOf(completedCnt));
			
			log.info("미션 성공 총 횟수 (월) ------> " + Constants.SUCCESS);
			
			} catch (Exception e) {
				 result.put("HttpStatus","1.00");		
				 result.put("Msg",Constants.SYSTEM_ERROR);
				 log.error("본인 미션 성공 총 횟수 ---> " + Constants.SYSTEM_ERROR , e);
			}
	        
		    return result ;			    		   
		}
		
	 
	
	
	/**
	 * 첼린지 제출
	 * @param challengeDTO
	 * @return
	 * @throws IOException 
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> submitChallenge (ChallengeSubmissionDto submissonDto , String userEmail) {
		Map<String, String> result = new HashMap<String, String>();
		
	    log.info("첼린지 제출 ------> " + "Start");	    
	    
	    // 예시로 현재 LocalDateTime 생성
        LocalDateTime dateTime = LocalDateTime.now();

        // LocalDateTime을 LocalDate와 LocalTime으로 분리
        LocalDate date = dateTime.toLocalDate();
        LocalTime time = dateTime.toLocalTime();

        // LocalDate와 LocalTime을 문자열 형식으로 포맷팅
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String submissionDate = date.format(dateFormatter);
        String submissionTime = time.format(timeFormatter);
	    
	    try {
			
		    			
			// 해당 유저가 있는지 확인
			Optional<MemberEntity> existsMember = memberRepository.findById(userEmail);
												
			// 없으면 에러 응답
			if(!existsMember.isPresent()) {
				 log.info("첼린지 참가 ------> " + "이메일이 존재하지 않습니다.");
	            result.put("HttpStatus","1.00");		
	    		 result.put("Msg","이메일이 존재하지 않습니다.");
	    	   return result ;
			 }				
													 
			MemberEntity member =  existsMember.get();
			
			
			// 해당 유저가 있는지 확인
			Long userChallengeId = userChallengeRepository.findByEmail(userEmail).get().getUserChallengeId();
		 	
		 	// 오늘 첼린지 제출했는지 확인
		 	boolean submitYn = challengeSubmissionRepository.findByUserChallengeIdAndSubmissionDate(userChallengeId, submissionDate).isPresent();
		 			
		 	// 제출했으면 에러 응답
		 	if(submitYn) {
		 		 log.info("첼린지 제출 ------> " + "오늘은 이미 제출이 완료되었습니다.");
	             result.put("HttpStatus","1.00");		
	     		 result.put("Msg","오늘은 이미 제출이 완료되었습니다.");
	     	   return result ; 
			}
			 	
		 	
		 	// 파일 업로드 
		 	String submissionImageRoute = fileService.uploadFile(submissonDto.getSubmissionImageRoute(),submissonDto.getSubmissionImageName() ,userEmail);
		 	
		 	// 파일 업로드 성공시 첼린지 제출 완료로 저장
		 	if(!submissionImageRoute.equals("N")) {	 			 	
			 	ChallengeSubmissionEntity challengeSubmission = ChallengeSubmissionEntity.builder()
			 													.submissionDate(submissionDate)
					   											.submissionTime(submissionTime) // 제출 일시				   											
					   											.submissionText(submissonDto.getSubmissionText()) // 내용
					   											.nickName(member.getNickName()) // 닉네임
					   											.submissionImageRoute(submissionImageRoute) // 이미지 경로
					   											.userChallengeId(userChallengeId) // 유저 첼린지 ID 
					   											.submissionCompleted("Y") // 인증 성공 유무
					   											.build();
		        challengeSubmissionRepository.save(challengeSubmission);	                	       
		        
		        	             
				log.info("첼린지 제출 ------> " + Constants.SUCCESS);
				
				
				// 첼린지 제출이 완료 되었는지 확인 
				Optional<ChallengeSubmissionEntity> successYn = challengeSubmissionRepository.findByUserChallengeIdAndSubmissionDate(userChallengeId, submissionDate);
					
				String tagExistN = "" ; // 태그 존재 유무
							
				// 제출이 완료 되었으면 사용자가 태그한 해쉬태그도 저장
				if(successYn.isPresent()) {								
					
			    	List<String> list = Arrays.asList(submissonDto.getHashTag().split("\\|")); // hashTag | 기준으로 잘라서 리스트에 넣기    	
			    	List<Long> lists = new ArrayList<Long>();
			    	for(int i = 0 ; list.size() > i; i++) {		    		
			    		lists.add(Long.valueOf(list.get(i)));
			    	}
			    	
			    	for(int i = 0 ; list.size() > i; i++) { // 리스트 사이즈만큼 돌면서 map에 담기
			    		Optional<HashTagEntity> tagExistYn = hashTagRepository.findById((lists.get(i)));
			    		if(tagExistYn.isPresent()) {		    			
			    			SubmissionHashTagEntity hashTagEntity = SubmissionHashTagEntity.builder()
									.challengeSubmissionId(successYn.get().getChallengeSubmissionId())
									.hashTagId(tagExistYn.get().getHashTagId())						
									.build();
				    		submissionHashTagRepository.save(hashTagEntity);
				    		log.info("미션인증 해쉬태그 저장 ------> "+list.size() +" 중 " +i+ "번째 " + Constants.SUCCESS);
			    		}else {
			    			tagExistN += list.get(i)+ "," ;  
			    		}		
			    	}
	
				}
								
				MemberDto memberDto = new MemberDto();
				memberDto.setEmail(userEmail);
				
				// 첼린지 미션 제출 후 누적 미션 수 체크 
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
		 	}
	 	
		} catch (Exception e) {
	        result.put("HttpStatus","1.00");		
			result.put("Msg",Constants.SYSTEM_ERROR);	       
			log.error("첼린지 제출 ------> " + Constants.SYSTEM_ERROR , e);	
		}
        
	 	
	 	return result ;
	}
	
		
	
	 /**
		 * 사람들이 많이 구경하는 첵린저 리스트
		 * @param memberDto
		 * @param challengeDto
		 * @return
		 */
		@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
		public Map<String, Object> viewCheckRinger (PageDto pageDto) {
			Map<String, Object> result = new HashMap<String, Object>();
			
			log.info("사람들이 많이 구경하는 첵린저 리스트 ------> " + "Start");
									 						 					
			  try {
				  	
				    // 좋아요 수가 많은 사용자 구하기
		        	ArrayList<Map<String, Object>> viewCheckRinger = challengeMapper.viewCheckRinger(pageDto);
		        	        	        	
		        	// 리스트 사이즈만큼 돌면서 DB에 저장된 이미지 경로로 이미지를 base64로 인코딩해서 값 덮어씌우기
		        	if (viewCheckRinger.size() != 0 ) { 
		        		for(int i = 0; i < viewCheckRinger.size(); i++) { 
		        			String image = fileService.myAuthImage((String) viewCheckRinger.get(i).get("PROFILE"));
		        				if(!image.equals("N")) {
		        					viewCheckRinger.get(i).put("PROFILE", image);
		        				}
		        	}
		        		
	        	 	log.info("사람들이 많이 구경하는 첵린저 리스트 ------> " + Constants.SUCCESS);
	        	   	result.put("HttpStatus","2.00");		
	      			result.put("Msg",Constants.SUCCESS);		      			
	      			result.put("viewCheckRinger",viewCheckRinger);		        		
		       		 
		           } else {
		        	   log.info("사람들이 많이 구경하는 첵린저 리스트 ------> " + "게시글이 없습니다.");
		        	    result.put("HttpStatus","1.00");		
		       			result.put("Msg","게시글이 없습니다.");
		       			return result ;
		           }
		        	
				} catch (Exception e) {
					log.error("사람들이 많이 구경하는 첵린저 리스트 ------> " + Constants.SYSTEM_ERROR , e);					
		    	    result.put("HttpStatus","1.00");		
		   			result.put("Msg",Constants.SYSTEM_ERROR);		   			
		   		 return result ;			
				}               		 
			  return result ;					 	    		   
	}	
			
	
	 /**
		 * 첼린지 인증글 리스트
		 * @param memberDto
		 * @param challengeDto
		 * @return
		 */
		@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
		public Map<String, Object> viewChallengeListTest (PageDto pageDto) {
			Map<String, Object> result = new HashMap<String, Object>();
			
			log.info("첼린지 인증글 리스트 ------> " + "Start");
									 						 					
			  try {
				  
					if(pageDto.getTag() != null) {
						pageDto.setTagList(Arrays.asList(pageDto.getTag().split("\\|")));  // hashTag | 기준으로 잘라서 리스트에 넣기
					}
				  	
					// 첼린지 인증글 리스트 가져오기
		        	ArrayList<Map<String, Object>> viewChallengeList = challengeMapper.viewChallengeList(pageDto);
		        	        	        	
		        	// 리스트 사이즈만큼 돌면서 DB에 저장된 이미지 경로로 이미지를 base64로 인코딩해서 값 덮어씌우기
		        	if (viewChallengeList.size() != 0 ) { 
		        		for(int i = 0; i < viewChallengeList.size(); i++) { 
		        			String image = (String) viewChallengeList.get(i).get("SUBMISSION_IMAGE");		        			
		        			viewChallengeList.get(i).put("SUBMISSION_IMAGE", image);
		        		}
		        		
		        	 	log.info("첼린지 인증글 리스트 ------> " + Constants.SUCCESS);
		        	   	result.put("HttpStatus","2.00");		
		      			result.put("Msg",Constants.SUCCESS);
		      			result.put("totalCount",challengeMapper.listTotalCnt(pageDto));
		      			result.put("viewChallengeList",viewChallengeList);		        		
		       		 
		           } else {
		        	   log.info("첼린지 인증글 리스트 ------> " + "게시글이 없습니다.");
		        	    result.put("HttpStatus","1.00");		
		       			result.put("Msg","게시글이 없습니다.");
		       			return result ;
		           }
		        	
				} catch (Exception e) {
					log.error("첼린지 인증글 리스트 ------> " + Constants.SYSTEM_ERROR , e);					
		    	    result.put("HttpStatus","1.00");		
		   			result.put("Msg",Constants.SYSTEM_ERROR);		   			
		   		 return result ;			
				}               		 
			  return result ;					 	    		   
	}	
	
	}
