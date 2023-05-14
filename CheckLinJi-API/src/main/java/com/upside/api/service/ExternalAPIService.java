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
	
//	private final ApiComponent apiCoponent;
	
	
	 /**
	  * 명언 API 
	  * @return
	  */
	 public  Map<String,String> getWiseSayingAPI () {
		 
		 Map<String,String> result = new HashMap<String, String>();
		 
		 LocalDate now = LocalDate.now();
	     Long day = (long) now.getDayOfMonth();	     
	     
	     Optional<SayingEntity> existYN = wiseSayingRepository.findBysaySeq(day);
	     
		 if(existYN.isPresent()) {
			 log.info("명언 ------> " + Constants.SUCCESS);
			 result.put("HttpStatus","2.00");
			 result.put("Msg",Constants.SUCCESS);
			 result.put("name",existYN.get().getName());
			 result.put("content",existYN.get().getMsg());		 			 
		 } else {
			 log.info("명언 ------> " + Constants.FAIL);
			 result.put("HttpStatus","1.00");
			 result.put("Msg",Constants.FAIL);
		 }
	
	        return result ;
	    }	
	 
	 /**
	  * 베스트 셀러 
	  * @return
	  */
	 public  Map<String,Object> bestSeller (String day) {
		 
		 Map<String,Object> result = new HashMap<String, Object>();
		 		 		 
		 List<BestBookEntity> existYN = bestBooRepository.findByDate(day);	     		
	     	     
		 if(existYN != null) {
			 log.info("베스트셀러 확인 ------> " + Constants.SUCCESS);
			 result.put("HttpStatus","2.00");
			 result.put("Msg",Constants.SUCCESS);
			 result.put("bestSeller",existYN);			 		 			 
		 } else {
			 log.info("베스트셀러 확인 ------> " + Constants.FAIL);
			 result.put("HttpStatus","1.00");
			 result.put("Msg",Constants.FAIL);
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

//	      private static String post(String apiUrl, Map<String, String> requestHeaders, JSONObject jsonData) {
//	          HttpURLConnection con = connect(apiUrl);
//
//	          try {
//	              con.setRequestMethod("POST"); // POST 형식으로
//	              for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
//	                  con.setRequestProperty(header.getKey(), header.getValue()); // Request 속성 값 설정하기 원래는 con.setRequestProperty("Content-type", "application/json"); 으로 설정하는데 for문으로 간단하게 처리
//	              }
//	                    
//	              con.setDoOutput(true); // 받아온 Json 데이터를 출력 가능한 상태(True)
//	              try (OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream(), "UTF-8")) {            	 
//	            	  wr.write(jsonData.toString());  // JSON형식을 String으로 다시 변환
//	                  wr.flush();
//	              }
//
//	              int responseCode = con.getResponseCode();
//	              if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
//	                  return readBody(con.getInputStream());
//	              } else {  // 에러 응답
//	                  return readBody(con.getErrorStream());
//	              }
//	          } catch (IOException e) {
//	              throw new RuntimeException("API 요청과 응답 실패", e);
//	          } finally {
//	              con.disconnect(); // Connection을 재활용할 필요가 없는 프로세스일 경우
//	          }
//	      }
//
//	      private static HttpURLConnection connect(String apiUrl) {
//	          try {
//	              URL url = new URL(apiUrl);
//	              return (HttpURLConnection) url.openConnection();
//	          } catch (MalformedURLException e) {
//	              throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
//	          } catch (IOException e) {
//	              throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
//	          }
//	      }
//
//	      private static String readBody(InputStream body) {
//	          InputStreamReader streamReader = new InputStreamReader(body, StandardCharsets.UTF_8);
//
//	          try (BufferedReader lineReader = new BufferedReader(streamReader)) {
//	              StringBuilder responseBody = new StringBuilder();
//
//	              String line;
//	              while ((line = lineReader.readLine()) != null) {
//	                  responseBody.append(line);
//	              }
//	              System.out.println(responseBody.toString());
//	              return responseBody.toString();
//	          } catch (IOException e) {
//	              throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
//	          }
//	      }
	}
