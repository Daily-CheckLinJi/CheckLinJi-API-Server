package com.upside.api.service;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.entity.ChallengeSubmissionEntity;
import com.upside.api.entity.HashTagEntity;
import com.upside.api.entity.SubmissionHashTagEntity;
import com.upside.api.mapper.MemberMapper;
import com.upside.api.repository.ChallengeSubmissionRepository;
import com.upside.api.repository.HashTagRepository;
import com.upside.api.repository.ReportSubmissionRepository;
import com.upside.api.repository.SubmissionHashTagRepository;
import com.upside.api.scheduler.RankingTopInsert;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RequiredArgsConstructor
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@Service
public class MissionService {
		
	@Value("${file.url}")
	private String fileUrl; 
	
	private final MemberMapper memberMapper ;
	
	private final HashTagRepository hashTagRepository;
	
	private final SubmissionHashTagRepository submissionHashTagRepository;
	
	private final ChallengeSubmissionRepository challengeSubmissionRepository;
			
	private final FileService fileService ;
	
	private final ReportSubmissionRepository reportSubmissionRepository;
	
	private final RankingTopInsert rankingTopInsert ;
	 	 
	
	
	
	
	 /**
	  * 미션 성공 총 횟수 (월)
	  * @param memberDto
	  * @return
	  */
	public Map<String, Object> missionCompletedCnt (String userEmail) {
		Map<String, Object> result = new HashMap<String, Object>();		
		
		try {
					
			// 현재 날짜와 시간을 LocalDateTime 객체로 가져옵니다.
	        LocalDateTime now = LocalDateTime.now();
	        
	        // 현재 년도와 월을 가져옵니다.
	        int year = now.getYear();
	        int month = now.getMonthValue();
	                                                 		
	        Map<String, String> data = new HashMap<String, String>();
	        
	        data.put("year", String.valueOf(year));
	        data.put("month", String.valueOf(month));
	        data.put("email", userEmail);
	                        
	        result = memberMapper.missionCompletedCnt(data);
	              
	        
	        if (String.valueOf(result.get("own")) == null || String.valueOf(result.get("own")).equals("0")) {
	        	result.put("HttpStatus","2.00");		
	    		result.put("Msg",Constants.SUCCESS);
	    		return result ;
	        }
	        
	        result.put("HttpStatus","2.00");		
			result.put("Msg",Constants.SUCCESS);
			
			log.info("미션 성공 총 횟수 (월) ------> " + Constants.SUCCESS);
		
		} catch (Exception e) {			
	      	result.put("HttpStatus","1.00");		
    		result.put("Msg",Constants.SYSTEM_ERROR);    		   
    		log.error("미션 성공 총 횟수 (월) ------> " + Constants.SYSTEM_ERROR , e);    		
		}
	    return result ;			    		   
	}
	
	 /**
	  * 미션 성공 총 횟수 (전체)
	  * @param memberDto
	  * @return
	  */
	public Map<String, Object> missionCompletedCntAll(String userEmail) {
		Map<String, Object> result = new HashMap<String, Object>();		
		
		try {
					                                                			   	                       
			int missionCompletedCnt = memberMapper.missionCompletedCntAll(userEmail);
	             	       
	        result.put("HttpStatus","2.00");
	        result.put("missionCompletedCnt",missionCompletedCnt);
			result.put("Msg",Constants.SUCCESS);
			
			log.info("미션 성공 총 횟수 (전체) ------> " + Constants.SUCCESS);
		
		} catch (Exception e) {			
	      	result.put("HttpStatus","1.00");		
	      	result.put("Msg",Constants.SYSTEM_ERROR);    		   
	      	log.error("미션 성공 총 횟수 (전체) ------> " + Constants.SYSTEM_ERROR , e);    		
		}
	    return result ;			    		   
	}	
	
	/**
	  * 실시간 랭킹
	  * @param memberDto
	  * @return
	  */
	public Map<String, Object> missionRanking (String userEmail) {
		
	   Map<String, Object> result = new HashMap<String, Object>();	   	   
              		
       Map<String, String> data = new HashMap<String, String>();              
       
       data.put("email", userEmail);
       
       try {
		             
	       ArrayList<Map<String, Object>> missionRankingTop = memberMapper.missionRankingTop();
	       
	       Map<String, String> missionRankingOwn = memberMapper.missionRankingOwn(data);
	                     
	       if (missionRankingTop.get(0) == null ) {
	    	    log.info("실시간 랭킹 ------> " + "게시글이 없음");
	    	    result.put("HttpStatus","2.00");		
	   			result.put("Msg","게시글이 없습니다.");
	   		 return result ;
	       }
	       
	
	       
	       for(int i =0; i < missionRankingTop.size(); i++) {    	   
	    	   missionRankingTop.get(i).put("profile", fileUrl + (String) missionRankingTop.get(i).get("profile"));
	       }
	       
	       result.put("HttpStatus","2.00");		
		   result.put("Msg",Constants.SUCCESS);
		   result.put("missionRankingTop",missionRankingTop);
		   if (missionRankingOwn != null ) {
			   missionRankingOwn.put("profile",fileUrl + (String) missionRankingOwn.get("profile"));
	    	   result.put("missionRankingOwn",missionRankingOwn);
	       }else {
	    	   Map<String, String> missionRankingOwnInfo = memberMapper.missionRankingOwnInfo(data);
	    	   missionRankingOwnInfo.put("profile",fileUrl + (String) missionRankingOwnInfo.get("profile"));
	    	   missionRankingOwnInfo.put("ranking","0");
	    	   result.put("missionRankingOwn",missionRankingOwnInfo);
	       }
		   
			
			log.info("실시간 랭킹 ------> " + Constants.SUCCESS);
		
		} catch (Exception e) {
    	    log.error("실시간 랭킹 ------> " + Constants.SYSTEM_ERROR , e);
    	    result.put("HttpStatus","1.00");		
   			result.put("Msg",Constants.SYSTEM_ERROR);
		}
		
	    return result ;			    		   
	}
	
	/**
	 * 본인 미션 달력
	 * @param fileUploadDto
	 * @return
	 */
	public Map<String, Object> myAuth(ChallengeSubmissionDto challengeSubmissionDto , String userEmail) {
		
		log.info("본인 미션 달력 ------> " + "Start");
		Map<String, Object> result = new HashMap<String, Object>();
				        
        // 현재 년도와 월을 가져옵니다.
        String year = challengeSubmissionDto.getYear();
        String month = challengeSubmissionDto.getMonth();                        
        String date = year+"-"+ month+"%";                                
        
        Map<String, String> data = new HashMap<String, String>();
                
        data.put("date", date);
        data.put("email", userEmail);
        
        try {
        	
        	ArrayList<Map<String, Object>> missionCalendarOwn = memberMapper.missionCalendarOwn(data);
        	        	        	
        	if (missionCalendarOwn.size() == 0 ) {
        		log.info("본인 미션 달력 ------> " + "참여중이 아니거나 이력이 없습니다.");
        	    result.put("HttpStatus","1.00");		
       			result.put("Msg","참여중이 아니거나 이력이 없습니다.");
       		 return result ;
           } else {
        	   	log.info("본인 미션 달력 ------> " + Constants.SUCCESS);
        	    	   	
        	   	result.put("HttpStatus","2.00");		
      			result.put("Msg",Constants.SUCCESS);
      			result.put("missionCalendarOwn",missionCalendarOwn);
           }
        	
		} catch (Exception e) {
			log.error("본인 미션 달력 ------> " + Constants.SYSTEM_ERROR , e);
    	    result.put("HttpStatus","1.00");		
   			result.put("Msg",Constants.SYSTEM_ERROR);
   		 return result ;			
		}               		 
	  return result ;				 	    			    		   
	}
	
	/**
	 * 본인 미션 상세보기
	 * @param fileUploadDto
	 * @return
	 * @throws JsonProcessingException 
	 * @throws ParseException 
	 */
	public Map<String, Object> myAuthInfo(ChallengeSubmissionDto challengeSubmissionDto) throws JsonProcessingException, ParseException {
		
		log.info("본인 미션 상세보기 ------> " + "Start");
		Map<String, Object> result = new HashMap<String, Object>();
				                                               
                
        try {
        	Map<String, Object> missionAuthInfo = memberMapper.missionAuthInfo(challengeSubmissionDto); // 해당날짜에 해당하는 본인 데이터
        	
        	ArrayList<Map<String, Object>> missionComment = memberMapper.missionComment(challengeSubmissionDto);
		    	for(int i = 0 ; i < missionComment.size(); i++) {
		    		missionComment.get(i).put("PROFILE", fileUrl + (String) missionComment.get(i).get("PROFILE"));		        			
		    	}
        	
        	ArrayList<Map<String, Object>> missionLikes = memberMapper.missionLikes(challengeSubmissionDto);
        	
	        	for(int i = 0 ; i < missionLikes.size(); i++) {
	        		missionLikes.get(i).put("PROFILE", fileUrl + (String) missionLikes.get(i).get("PROFILE"));		        			
		    	}
        	
        	ArrayList<Map<String, Object>> missionHashTag = memberMapper.missionHashTag(challengeSubmissionDto);
        	        	        	
        	if (missionAuthInfo == null ) {
        		log.info("본인 미션 상세보기 ------> " + "참여중이 아니거나 이력이 없습니다.");
        	    result.put("HttpStatus","1.00");		
       			result.put("Msg","참여중이 아니거나 이력이 없습니다.");
       		 return result ;
       		 
           } else { // 해당날짜에 해당하는 본인 데이터가 있을 시
        	   
   	   			
   	   	   	   			
   	   			
        	    ObjectMapper objectMapper = new ObjectMapper();
				
        	    // MAP 객체를 JSON으로 변환
				String json = objectMapper.writeValueAsString(missionAuthInfo); 
																					
				// JSON 문자열을 파싱할 JSONParser 객체 생성
				JSONParser parser = new JSONParser();
			
			    // JSON 문자열을 파싱하여 JSONObject 객체로 변환
			    JSONObject jsonObject = (JSONObject) parser.parse(json);
			    			    			    			    
			    // SUBMISSION_IMAGE_ROUTE 컬럼 값 가져오기
			    String missionImageRoute = (String) jsonObject.get("SUBMISSION_IMAGE_ROUTE");
			    String profileImageRoute = (String) jsonObject.get("PROFILE");
			    
			    // Base64로 인코딩된 이미지 파일 문자열로 가져옴
			    String missionImage = fileUrl + missionImageRoute;
			    String profileImage = fileUrl + profileImageRoute;
        	   
			    if(missionImage.equals("N")) {
			    	log.info("본인 미션 상세보기 ------> " + "이미지를 표시할 수 없습니다.");
			    	missionAuthInfo.put("SUBMISSION_IMAGE_ROUTE", "이미지를 표시할 수 없습니다.");			    	
			    	result.put("HttpStatus","2.00");		
	      			result.put("Msg","이미지를 표시할 수 없습니다.");
	      			result.put("missionAuthInfo",missionAuthInfo);	      			
	      			
			    } else {	        	   		        	   	
	        	   	if(profileImage.equals("N")) {
	        	   		missionAuthInfo.put("PROFILE", "이미지를 표시할 수 없습니다.");
	        	   	} else {
	        	   		missionAuthInfo.put("PROFILE", profileImage);
	        	   	}
	        	   	
	        	 // 게시글 신고 유무
	        	Long existsReport = reportSubmissionRepository.countByChallengeSubmissionIdAndEmail(Long.valueOf(challengeSubmissionDto.getChallengeSubmissionId()),challengeSubmissionDto.getEmail());
	        	   		        	         			
      			Map<String, String> data = new HashMap<String, String>();              
	   		       
        		data.put("email", (String) missionAuthInfo.get("EMAIL"));
 				 
 		        // 유저 미션 랭킹 가져오기
 				String missionRankingUser = memberMapper.isTopRank(data);
 						 						 				
 				 // 미션 정보가 없으면 랭크 0 처리
				 if(missionRankingUser == null) {
					 missionRankingUser = "0" ;
				 }        				        						       			
				 
        	   	log.info("본인 미션 상세보기 ------> " + Constants.SUCCESS);	        	   		        	   	
        	   	missionAuthInfo.put("SUBMISSION_IMAGE_ROUTE", missionImage);	        	   	
        	   	result.put("HttpStatus","2.00");		
      			result.put("Msg",Constants.SUCCESS);
      			result.put("missionAuthInfo",missionAuthInfo);
      			result.put("missionComment",missionComment);
      			result.put("missionHashTag",missionHashTag);
      			result.put("missionLikes",missionLikes);				 
				result.put("ranking",missionRankingUser); // 유저 게시글 리스트
				 
      			 // 같은 게시글을 신고한 이력이 있으면 이미 신고되었음 처리
	  			  if(existsReport != 0) { 
	  				  result.put("existsReport","Y");
	  			  }else {
	  				  result.put("existsReport","N");
	  			  }
	      			
			    }
           }
		} catch (Exception e) {
			log.error("본인 미션 상세보기 ------> " + Constants.SYSTEM_ERROR , e);
    	    result.put("HttpStatus","1.00");		
   			result.put("Msg",Constants.SYSTEM_ERROR);   		 		
		}               		 
	  return result ;				 	    			    		   
	}

	
	 /**
	  * 본인 미션 수정
	  * @param memberDto
	  * @return
	  */
	public Map<String, String> missionUpdate (ChallengeSubmissionDto chaSubmissionDto) {
		
		  Map<String, String> result = new HashMap<String, String>();
		
		  log.info("본인 미션 수정 ------> " + "Start");
  		   
		  try {
					  
		   int updateMission = memberMapper.missionUpdate(chaSubmissionDto);
		   
		   int deleteTag = memberMapper.deleteTag(chaSubmissionDto);

		   String tagExistN = "" ; // 태그 존재 유무
		   
		   if (updateMission > 0) {
			   	result.put("HttpStatus","2.00");		
			   	result.put("Msg",Constants.SUCCESS);       		
		   } else if(updateMission == 0) {
			   	result.put("HttpStatus","1.00");		
		  	 	result.put("Msg",Constants.FAIL);
		  	 	return result ;		
		   } else if(deleteTag == 0) {
				result.put("HttpStatus","1.00");		
			  	result.put("Msg",Constants.FAIL);
			  	return result ;		
		   }
		   
		   
		   List<String> list = Arrays.asList(chaSubmissionDto.getHashTag().split("\\|")); // hashTag | 기준으로 잘라서 리스트에 넣기    	
	    	List<Long> lists = new ArrayList<Long>();
	    	for(int i = 0 ; list.size() > i; i++) {		    		
	    		lists.add(Long.valueOf(list.get(i)));
	    	}
	    	
	    	for(int i = 0 ; list.size() > i; i++) { // 리스트 사이즈만큼 돌면서 map에 담기
	    		Optional<HashTagEntity> tagExistYn = hashTagRepository.findById((lists.get(i)));
	    		Optional<ChallengeSubmissionEntity> missionExist = challengeSubmissionRepository.findById((long)chaSubmissionDto.getChallengeSubmissionId());	    		
	    		if(tagExistYn.isPresent()) {	    			
	    			SubmissionHashTagEntity hashTagEntity = SubmissionHashTagEntity.builder()
							.challengeSubmissionId(missionExist.get().getChallengeSubmissionId())
							.hashTagId(tagExistYn.get().getHashTagId())						
							.build();
		    		submissionHashTagRepository.save(hashTagEntity);
		    		log.info("미션인증 해쉬태그 저장 ------> "+list.size() +" 중 " +i+ "번째 " + Constants.SUCCESS);
	    		}else {
	    			tagExistN += list.get(i)+ "," ;  
	    		}		
	    	}
		   
	    	if(!tagExistN.equals("")) {
				tagExistN += " -- 등록된 태그가 아니기 때문에 추가에서 제외 되었습니다."; 				
				result.put("tagExistN",tagExistN);	
			}
		   		  
		   log.info("본인 미션 수정 결과 ------> " + updateMission);
		   
			} catch (Exception e) {
				log.error("본인 미션 수정 ------> " + Constants.SYSTEM_ERROR , e);
				 result.put("HttpStatus","1.00");		
			  	 result.put("Msg",Constants.SYSTEM_ERROR);  
			}
		   
		    return result ;			    		   
		}
	
	/**
	 * 본인 미션 삭제
	 * @param fileUploadDto
	 * @return
	 * @throws JsonProcessingException 
	 * @throws ParseException 
	 */
	public Map<String, Object> myAuthDelete(ChallengeSubmissionDto challengeSubmissionDto , String userEmail)  {
		
		log.info("본인 미션 삭제 ------> " + "Start");
		Map<String, Object> result = new HashMap<String, Object>();
				                
        
        try {
        	
        	HashMap<String,Object> missionAuthInfo = memberMapper.missionAuthInfo(challengeSubmissionDto);
        	
        	memberMapper.missionDeleteComment(challengeSubmissionDto); // 해당날짜에 해당하는 본인 데이터
        	
        	memberMapper.missionDeleteLikes(challengeSubmissionDto); // 해당날짜에 해당하는 본인 데이터
        	
        	memberMapper.missionDeleteHashTag(challengeSubmissionDto); // 해당날짜에 해당하는 본인 데이터
        	
        	int missionDeleteSubmission = memberMapper.missionDeleteSubmission(challengeSubmissionDto); // 해당날짜에 해당하는 본인 데이터
        	
        	boolean deleteImage = fileService.deleteFile(String.valueOf(missionAuthInfo.get("SUBMISSION_IMAGE_ROUTE")));
        	        	        	
        	if (missionDeleteSubmission == 0 && !deleteImage) {
        		log.info("본인 미션 삭제 ------> " + "요청이 제대로 처리되지 않았습니다.");
        	    result.put("HttpStatus","1.00");		
       			result.put("Msg","요청이 제대로 처리되지 않았습니다.");
       		 return result ;
       		 
           } else { // 본인 미션 삭제        	   
        	    // 유저 실시간 랭킹 업데이트
        	   	rankingTopInsert.rankingTopInsert();
        	    log.info("본인 미션 삭제 ------> " + Constants.SUCCESS);
       	    	result.put("HttpStatus","2.00");		
      			result.put("Msg",Constants.SUCCESS);    
           }
		} catch (Exception e) {
			log.error("본인 미션 삭제 ------> " + Constants.SYSTEM_ERROR , e);
    	    result.put("HttpStatus","1.00");		
   			result.put("Msg", Constants.SYSTEM_ERROR);
   		 return result ;			
		}               		 
	  return result ;				 	    			    		   
	}
		
		
	
	
}
