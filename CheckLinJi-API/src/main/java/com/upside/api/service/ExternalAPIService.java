package com.upside.api.service;



import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.upside.api.dto.BestBookDto;
import com.upside.api.entity.BestBookEntity;
import com.upside.api.entity.SayingEntity;
import com.upside.api.repository.BestBookRepository;
import com.upside.api.repository.WiseSayingRepository;
import com.upside.api.util.APIConnect;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;





@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@RequiredArgsConstructor
@Service
public class ExternalAPIService {

	@Value("${chatGpt.api.key}")
	private String chatGptKey;
	
	@Value("${naver.client.id}")
	private String clientId; //  클라이언트 아이디
	
	@Value("${naver.client.secret}")
	private String clientSecret; // //  클라이언트 시크릿
	
	private final WiseSayingRepository wiseSayingRepository;
	
	private final BestBookRepository bestBooRepository;
	
	private final FileService fileService;
	
	
	 /**
	  * 명언 API 
	  * @return
	  */
	 public  Map<String,String> getWiseSayingAPI () {
		 
		 Map<String,String> result = new HashMap<String, String>();
		 
		 LocalDate now = LocalDate.now();
	     Long day = (long) now.getDayOfMonth();	     
	     
	     try {
			
	    	 // 매일 날짜별로 명언 가져오기
		     Optional<SayingEntity> existYN = wiseSayingRepository.findBysaySeq(day);
		     
			 if(existYN.isPresent()) {				 
				 result.put("HttpStatus","2.00");
				 result.put("Msg",Constants.SUCCESS);
				 result.put("name",existYN.get().getName());
				 result.put("content",existYN.get().getMsg());
				 log.info("명언 가져오기 ------> " + Constants.SUCCESS);
			 } else {				 
				 result.put("HttpStatus","1.00");
				 result.put("Msg",Constants.FAIL);
				 log.info("명언 가져오기 ------> " + Constants.FAIL);
			 }
			 
		} catch (Exception e) {
			 log.error("명언 가져오기 ------> " + Constants.SYSTEM_ERROR , e);
			 result.put("HttpStatus","1.00");
			 result.put("Msg",Constants.SYSTEM_ERROR);
		}
	
	        return result ;
	    }	
	 
	 /**
	  * 베스트 셀러 
	  * @return
	  */
	 public  Map<String,Object> bestSeller (BestBookDto bestBookDto) {
		 
		 Map<String,Object> result = new HashMap<String, Object>();
		 
		 try {
					
			 // 날짜와 타입에 해당하는 베스트셀러 리스트 가져오기
			 List<BestBookEntity> existYN = bestBooRepository.findByDateAndType(bestBookDto.getDate(),bestBookDto.getType());	     		
		     
			 // 리스트가 없으면 에러 처리
			 if(existYN == null) {
				 log.info("베스트셀러 확인 ------> " + Constants.SYSTEM_ERROR);
				 result.put("HttpStatus","1.00");
				 result.put("Msg",Constants.SYSTEM_ERROR);
				 return result ;
			 } 
			 
			 List<BestBookDto> list = new ArrayList<BestBookDto>();
			 
			 // List<Entity> 사이즈만큼 돌면서 List<Dto> 에 담기
			 for(int i = 0; i < existYN.size(); i++) {
				 BestBookDto bestBook = new BestBookDto();
				 bestBook.setName(existYN.get(i).getName());
				 bestBook.setRank(existYN.get(i).getRank());
				 bestBook.setDate(existYN.get(i).getDate());
				 bestBook.setUpdateDate(existYN.get(i).getUpdateDate());
				 bestBook.setImage(fileService.encodingImageUrl(existYN.get(i).getImage()));
				 list.add(bestBook);				 			
			 }
			 			 
			 result.put("HttpStatus","2.00");
			 result.put("Msg",Constants.SUCCESS);
			 result.put("bestSeller",list);
			 log.info("베스트셀러 확인 ------> " + Constants.SUCCESS);
		 
		} catch (Exception e) {
			 result.put("HttpStatus","1.00");
			 result.put("Msg",Constants.SYSTEM_ERROR);
			 log.error("베스트셀러 확인 ------> " + Constants.SYSTEM_ERROR , e);			 
		}
		 
	        return result ;
	    }	
	 
	 /**
	  * ChatGpt API
	  * @param request
	  * @return
	  */
	 @SuppressWarnings("unchecked")
		public String chatGptAPI(String request) {
	    	        
		 	  
	          Map<String, String> requestHeaders = new HashMap<>();  // 헤더 값 	     
	          
	          requestHeaders.put("Authorization", "Bearer "+ chatGptKey); // API 키 값
	          
	          requestHeaders.put("Content-Type", "application/json");
	          
	          String apiUrl = "https://api.openai.com/v1/completions";
	          
	          JSONObject jsonData = new JSONObject(); // JSON 형식으로 전달
	          jsonData.put("model", "text-davinci-003"); // 어떤 GPT 모델을 사용할지 지정
	          jsonData.put("prompt", request); // 질문할 문장 
	          jsonData.put("max_tokens", 150); // 응답의 컨텍스트 길이
	          jsonData.put("temperature", 0); // 얼마나 창의적인 답을 작성하도록 할지 지정하는 값. 클수록 창의적
	          	              
	          String responseBody = APIConnect.post(apiUrl, requestHeaders, jsonData);
	                   
	          return responseBody;  
	      }


	}
