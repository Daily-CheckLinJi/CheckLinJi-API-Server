package com.upside.api.service;



import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.mapper.MemberMapper;
import com.upside.api.repository.MemberRepository;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RequiredArgsConstructor
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@Service
public class MissionService {
		
	
	private final MemberMapper memberMapper ;
	
	private final MemberRepository memberRepository ;
	
	private final FileService fileService ;
	 	 
	
	
	
	
	 /**
	  * 미션 성공 총 횟수 (월)
	  * @param memberDto
	  * @return
	  */
	public Map<String, String> missionCompletedCnt (String userEmail) {
		Map<String, String> result = new HashMap<String, String>();
						
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
        	result.put("HttpStatus","1.00");		
    		result.put("Msg",Constants.FAIL);
    		return result ;
        }
        
        result.put("HttpStatus","2.00");		
		result.put("Msg",Constants.SUCCESS);
		
		log.info("미션 성공 총 횟수 (월) ------> " + Constants.SUCCESS);
		
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
        	   	for(int i = 0; i < missionCalendarOwn.size(); i++) {
        	   		Timestamp dbTimestamp = (Timestamp) missionCalendarOwn.get(i).get("SUBMISSION_TIME");
        	   		LocalDateTime nowDate = dbTimestamp.toLocalDateTime();
        	   		        	   	        	   		        	   		
            	   	missionCalendarOwn.get(i).put("SUBMISSION_DAY", nowDate.getYear()+"-"+String.format("%02d", nowDate.getMonthValue())+"-"+String.format("%02d", nowDate.getDayOfMonth()));
            	   	missionCalendarOwn.get(i).put("SUBMISSION_TIME",nowDate.getHour()+":"+nowDate.getMinute());
        	   	}
        	   	        	   	
        	   	result.put("HttpStatus","2.00");		
      			result.put("Msg",Constants.SUCCESS);
      			result.put("missionCalendarOwn",missionCalendarOwn);
           }
        	
		} catch (DataAccessException e) {
			log.info("본인 미션 달력 ------> " + "Data 접근 실패");
    	    result.put("HttpStatus","1.00");		
   			result.put("Msg","Data 접근 실패");
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
	public Map<String, Object> myAuthInfo(ChallengeSubmissionDto challengeSubmissionDto , String userEmail) throws JsonProcessingException, ParseException {
		
		log.info("본인 미션 상세보기 ------> " + "Start");
		Map<String, Object> result = new HashMap<String, Object>();
				        
        
        // 현재 년도와 월을 가져옵니다.
        String year = challengeSubmissionDto.getYear();
        String month = challengeSubmissionDto.getMonth();
        String day = challengeSubmissionDto.getDay();
        String date = year+"-"+ month+"-"+day;                                
        
        System.out.println(userEmail);
        System.out.println(date);
        
        Map<String, String> data = new HashMap<String, String>();
        
        data.put("challengeName", challengeSubmissionDto.getChallengeName());
        data.put("date", date);
        data.put("email", userEmail);
        
        try {
        	Map<String, Object> missionAuthInfo = memberMapper.missionAuthInfo(data); // 해당날짜에 해당하는 본인 데이터
        	
        	ArrayList<Map<String, Object>> missionComment = memberMapper.missionComment(data);
        	        	        	
        	if (missionAuthInfo == null ) {
        		log.info("본인 미션 상세보기 ------> " + "참여중이 아니거나 이력이 없습니다.");
        	    result.put("HttpStatus","1.00");		
       			result.put("Msg","참여중이 아니거나 이력이 없습니다.");
       		 return result ;
       		 
           } else { // 해당날짜에 해당하는 본인 데이터가 있을 시
        	   
        	   Timestamp dbTimestamp = (Timestamp) missionAuthInfo.get("SUBMISSION_TIME");
   	   			LocalDateTime nowDate = dbTimestamp.toLocalDateTime();
   	   		
   	   			missionAuthInfo.put("SUBMISSION_DAY", nowDate.getYear()+"-"+String.format("%02d", nowDate.getMonthValue())+"-"+String.format("%02d", nowDate.getDayOfMonth()));
   	   			missionAuthInfo.put("SUBMISSION_TIME",nowDate.getHour()+":"+nowDate.getMinute());
   	   			
   	   			if(missionComment.size() > 0) { 
   	   				for(int i = 0 ; i < missionComment.size(); i++) {
	   	   				Timestamp timestamp_1 = (Timestamp) missionComment.get(i).get("USER_REGIST_DATE");
	   	   	   			LocalDateTime regist_date = timestamp_1.toLocalDateTime();
	   	   	   			
	   	   	   			Timestamp timestamp_2 = (Timestamp) missionComment.get(i).get("USER_UPDATE_DATE");
	   	   	   			LocalDateTime update_date = timestamp_2.toLocalDateTime();
   	   				   	   					
   	   					missionComment.get(i).put("USER_REGIST_DATE",regist_date.getYear()+"-"+String.format("%02d", regist_date.getMonthValue())+"-"+String.format("%02d", regist_date.getDayOfMonth()) + " " + regist_date.getHour()+":"+regist_date.getMinute());
   	   					missionComment.get(i).put("USER_UPDATE_DATE",update_date.getYear()+"-"+String.format("%02d", update_date.getMonthValue())+"-"+String.format("%02d", update_date.getDayOfMonth()) + " " + update_date.getHour()+":"+update_date.getMinute());   	   					
   	   				}   	   			
   	   			}
   	   			
        	    ObjectMapper objectMapper = new ObjectMapper();
				
        	    // MAP 객체를 JSON으로 변환
				String json = objectMapper.writeValueAsString(missionAuthInfo); 
																					
				// JSON 문자열을 파싱할 JSONParser 객체 생성
				JSONParser parser = new JSONParser();
			
			    // JSON 문자열을 파싱하여 JSONObject 객체로 변환
			    JSONObject jsonObject = (JSONObject) parser.parse(json);
			    			    			    			    
			    // SUBMISSION_IMAGE_ROUTE 컬럼 값 가져오기
			    String fileRoute = (String) jsonObject.get("SUBMISSION_IMAGE_ROUTE");			    			   
			    
			    // Base64로 인코딩된 이미지 파일 문자열로 가져옴
			    String file = fileService.myAuthImage(fileRoute); 
        	   
			    if(file.equals("N")) {
			    	log.info("본인 미션 상세보기 ------> " + "이미지를 표시할 수 없습니다.");
			    	missionAuthInfo.put("SUBMISSION_IMAGE_ROUTE", "이미지를 표시할 수 없습니다.");			    	
			    	result.put("HttpStatus","2.00");		
	      			result.put("Msg","이미지를 표시할 수 없습니다.");
	      			result.put("missionAuthInfo",missionAuthInfo);	      			
	      			
			    } else {
	        	   	log.info("본인 미션 상세보기 ------> " + Constants.SUCCESS);	        	   		        	   	
	        	   	missionAuthInfo.put("SUBMISSION_IMAGE_ROUTE", file);
	        	   	result.put("HttpStatus","2.00");		
	      			result.put("Msg",Constants.SUCCESS);
	      			result.put("missionAuthInfo",missionAuthInfo);
	      			result.put("missionComment",missionComment);
			    }
           }
		} catch (DataAccessException e) {
			log.info("본인 미션 상세보기 ------> " + "Data 접근 실패");
    	    result.put("HttpStatus","1.00");		
   			result.put("Msg","Data 접근 실패");
   		 return result ;			
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
		} catch (DataAccessException e) {
			log.info("본인 미션 삭제 ------> " + "Data 접근 실패");
    	    result.put("HttpStatus","1.00");		
   			result.put("Msg","Data 접근 실패");
   		 return result ;			
		}               		 
	  return result ;				 	    			    		   
	}
		
		
	
	
}
