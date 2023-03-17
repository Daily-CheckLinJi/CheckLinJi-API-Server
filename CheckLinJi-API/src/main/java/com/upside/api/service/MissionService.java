package com.upside.api.service;



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
import com.upside.api.dto.MemberDto;
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
	public Map<String, String> missionCompletedCnt (MemberDto memberDto) {
		Map<String, String> result = new HashMap<String, String>();
						
		 // 현재 날짜와 시간을 LocalDateTime 객체로 가져옵니다.
        LocalDateTime now = LocalDateTime.now();
        
        // 현재 년도와 월을 가져옵니다.
        int year = now.getYear();
        int month = now.getMonthValue();
                                                 		
        Map<String, String> data = new HashMap<String, String>();
        
        data.put("year", String.valueOf(year));
        data.put("month", String.valueOf(month));
        data.put("email", memberDto.getEmail());
        
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
	public Map<String, Object> missionRanking (MemberDto memberDto) {
		
	   Map<String, Object> result = new HashMap<String, Object>();
	   
	   
              		
       Map<String, String> data = new HashMap<String, String>();              
       
       data.put("email", memberDto.getEmail());
       
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
	public Map<String, Object> myAuth(ChallengeSubmissionDto challengeSubmissionDto) {
		
		log.info("본인 미션 달력 ------> " + "Start");
		Map<String, Object> result = new HashMap<String, Object>();
		
		
        
        // 현재 년도와 월을 가져옵니다.
        String year = challengeSubmissionDto.getYear();
        String month = challengeSubmissionDto.getMonth();                        
        String date = year+"-"+ month+"%";                                
        
        Map<String, String> data = new HashMap<String, String>();
        
        data.put("date", date);
        data.put("email", challengeSubmissionDto.getEmail());
        
        try {
        	ArrayList<Map<String, Object>> missionCalendarOwn = memberMapper.missionCalendarOwn(data);
        	        	        	
        	if (missionCalendarOwn.get(0) == null ) {
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
	public Map<String, Object> myAuthInfo(ChallengeSubmissionDto challengeSubmissionDto) throws JsonProcessingException, ParseException {
		
		log.info("본인 미션 상세보기 ------> " + "Start");
		Map<String, Object> result = new HashMap<String, Object>();
				        
        
        // 현재 년도와 월을 가져옵니다.
        String year = challengeSubmissionDto.getYear();
        String month = challengeSubmissionDto.getMonth();
        String day = challengeSubmissionDto.getDay();
        String date = year+"-"+ month+"-"+day;                                
        
        Map<String, String> data = new HashMap<String, String>();
        
        data.put("date", date);
        data.put("email", challengeSubmissionDto.getEmail());
        
        try {
        	Map<String, String> missionAuthInfo = memberMapper.missionAuthInfo(data); // 해당날짜에 해당하는 본인 데이터
        	        	        	
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
			    			    			    			    
			    // FILE_ROUTE 키의 값 가져오기
			    String fileRoute = (String) jsonObject.get("FILE_ROUTE");			    			   
			    
			    // Base64로 인코딩된 이미지 파일 문자열로 가져옴
			    String file = fileService.myAuthImage(fileRoute); 
        	   
			    if(file.equals("N")) {
			    	log.info("본인 미션 상세보기 ------> " + "이미지를 표시할 수 없습니다.");
			    	missionAuthInfo.remove("FILE_ROUTE"); // 파일 경로는 클라이언트 측에서 알 필요없으므로 삭제
			    	result.put("HttpStatus","2.00");		
	      			result.put("Msg","이미지를 표시할 수 없습니다.");
	      			result.put("missionAuthInfo",missionAuthInfo);
	      			result.put("file","이미지를 표시할 수 없습니다.");
	      			
			    } else {
	        	   	log.info("본인 미션 상세보기 ------> " + Constants.SUCCESS);
	        	   	missionAuthInfo.remove("FILE_ROUTE"); // 파일 경로는 클라이언트 측에서 알 필요없으므로 삭제
	        	   	result.put("HttpStatus","2.00");		
	      			result.put("Msg",Constants.SUCCESS);
	      			result.put("missionAuthInfo",missionAuthInfo);
	      			result.put("file",file);
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
		
}
