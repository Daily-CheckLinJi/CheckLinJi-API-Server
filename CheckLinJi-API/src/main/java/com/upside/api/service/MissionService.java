package com.upside.api.service;



import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.mapper.MemberMapper;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RequiredArgsConstructor
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@Service
public class MissionService {
		
	
	private final MemberMapper memberMapper ;
			
	private final FileService fileService ;
	 	 
	
	
	
	
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
	  * 실시간 랭킹
	  * @param memberDto
	  * @return
	  */
	public Map<String, Object> missionRanking (String userEmail) {
		
	   Map<String, Object> result = new HashMap<String, Object>();	   	   
              		
       Map<String, String> data = new HashMap<String, String>();              
       
       data.put("email", userEmail);
       
       try {
		             
       ArrayList<Map<String, Object>> missionRankingTop = memberMapper.missionRankingTop(data);
       
       Map<String, String> missionRankingOwn = memberMapper.missionRankingOwn(data);
                     
       if (missionRankingTop.get(0) == null ) {
    	    log.info("실시간 랭킹 ------> " + "TOP 3 조회 실패");
    	    result.put("HttpStatus","1.00");		
   			result.put("Msg","TOP 3 조회 실패");
   		 return result ;
       }
       
       if (missionRankingOwn == null ) {
    	    log.info("실시간 랭킹 ------> " + "해당 사용자 참여중 아닐땐 TOP3 만");
   	    	result.put("HttpStatus","2.01");		
  			result.put("Msg","참여중이 아닙니다.");
  			result.put("missionRankingTop",missionRankingTop);
  		 return result ;
       }
       
       for(int i =0; i < missionRankingTop.size(); i++) {    	   
    	   missionRankingTop.get(i).put("profile",fileService.myAuthImage((String) missionRankingTop.get(i).get("profile")));
       }
       
       missionRankingOwn.put("profile",fileService.myAuthImage((String) missionRankingOwn.get("profile")));
       
       result.put("HttpStatus","2.00");		
	   result.put("Msg",Constants.SUCCESS);
	   result.put("missionRankingTop",missionRankingTop);
	   result.put("missionRankingOwn",missionRankingOwn);
		
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
        
        data.put("challengeName", challengeSubmissionDto.getChallengeName());
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
				        
        
        // 현재 년도와 월을 가져옵니다.
        String year = challengeSubmissionDto.getYear();
        String month = challengeSubmissionDto.getMonth();
        String day = challengeSubmissionDto.getDay();
        String date = year+"-"+ month+"-"+day;                                

        Map<String, String> data = new HashMap<String, String>();
        
        data.put("challengeName", challengeSubmissionDto.getChallengeName());
        data.put("date", date);
                
        try {
        	Map<String, Object> missionAuthInfo = memberMapper.missionAuthInfo(challengeSubmissionDto); // 해당날짜에 해당하는 본인 데이터
        	
        	ArrayList<Map<String, Object>> missionComment = memberMapper.missionComment(challengeSubmissionDto);
		    	for(int i = 0 ; i < missionComment.size(); i++) {
		    		missionComment.get(i).put("PROFILE", fileService.myAuthImage((String) missionComment.get(i).get("PROFILE")));		        			
		    	}
        	
        	ArrayList<Map<String, Object>> missionLikes = memberMapper.missionLikes(challengeSubmissionDto);
        	
	        	for(int i = 0 ; i < missionLikes.size(); i++) {
	        		missionLikes.get(i).put("PROFILE", fileService.myAuthImage((String) missionLikes.get(i).get("PROFILE")));		        			
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
			    String missionImage = fileService.myAuthImage(missionImageRoute);
			    String profileImage = fileService.myAuthImage(profileImageRoute);
        	   
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
	        	   	
	        	   	log.info("본인 미션 상세보기 ------> " + Constants.SUCCESS);	        	   		        	   	
	        	   	missionAuthInfo.put("SUBMISSION_IMAGE_ROUTE", missionImage);	        	   	
	        	   	result.put("HttpStatus","2.00");		
	      			result.put("Msg",Constants.SUCCESS);
	      			result.put("missionAuthInfo",missionAuthInfo);
	      			result.put("missionComment",missionComment);
	      			result.put("missionHashTag",missionHashTag);
	      			result.put("missionLikes",missionLikes);
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
					  
		   int updateYN = memberMapper.missionUpdate(chaSubmissionDto);
		   
		   if (updateYN > 0) {
			 result.put("HttpStatus","2.00");		
			 result.put("Msg",Constants.SUCCESS);       		
		   } else {
			 result.put("HttpStatus","1.00");		
		  	 result.put("Msg",Constants.FAIL);   
		   }
		  
		   log.info("본인 미션 수정 결과 ------> " + updateYN);
		   
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
				        
        
        // 현재 년도와 월을 가져옵니다.
        String year = challengeSubmissionDto.getYear();
        String month = challengeSubmissionDto.getMonth();
        String day = challengeSubmissionDto.getDay();
        String date = year+"-"+ month+"-"+day;                                
        
        Map<String, String> data = new HashMap<String, String>();
        
        data.put("date", date);
        data.put("email", userEmail);
        
        try {
        	int missionAuthInfo = memberMapper.missionAuthDelete(data); // 해당날짜에 해당하는 본인 데이터
        	        	        	
        	if (missionAuthInfo == 0 ) {
        		log.info("본인 미션 삭제 ------> " + "요청이 제대로 처리되지 않았습니다.");
        	    result.put("HttpStatus","1.00");		
       			result.put("Msg","요청이 제대로 처리되지 않았습니다.");
       		 return result ;
       		 
           } else { // 본인 미션 삭제
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
