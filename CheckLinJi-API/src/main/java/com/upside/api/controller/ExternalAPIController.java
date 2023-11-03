package com.upside.api.controller;



import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upside.api.dto.BestBookDto;
import com.upside.api.dto.MessageDto;
import com.upside.api.service.ExternalAPIService;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ExternalAPIController {
	
	private final ExternalAPIService externalAPIService;	
	

	
	/**
	 * 명언 API	
	 * @return
	 */
	@GetMapping("/wiseSaying") 						  	
	public ResponseEntity<Map<String,String>> wiseSayingAPI () {
		
						
		Map<String,String> result = externalAPIService.getWiseSayingAPI();
		
		if(result.get("HttpStatus").equals("2.00")) {			
			return new ResponseEntity<>(result,HttpStatus.OK);
		} else {
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		}
		
	 }
	
	/**
	 * 베스트셀러 Top 10 
	 * @return
	 */
	@PostMapping("/bestSeller") 						  	
	public ResponseEntity<Map<String,Object>> bestSeller (@RequestBody BestBookDto bestBookDto) {
										
		Map<String,Object> result = new HashMap<String, Object>();
		
		// 요청 온 데이터가 날짜가 아닐때 -> 잘못된 요청
		if(bestBookDto.getDate() == null || bestBookDto.getType() == null) {
			result.put("HttpStatus", "1.00");
			result.put("Msg", Constants.FAIL);
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);		
		}else {
			result = externalAPIService.bestSeller(bestBookDto);
		}
												
		if(result.get("HttpStatus").equals("2.00")) {			
			return new ResponseEntity<>(result ,HttpStatus.OK);
		} else {
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		}
		
	 }
	
	

	
	/**
	 * ChatGpt API
	 * @param request
	 * @return
	 */
	@PostMapping("/chatGpt")						  	
	public ResponseEntity<MessageDto> chatGptAPI (@RequestBody String request) {
		
		MessageDto message = new MessageDto();
		
		try {
			String result = externalAPIService.chatGptAPI(request);
			
			
			if(result != null ) {
				 JSONParser jsonParser = new JSONParser(); // 1. JSON 형태의 스트링을 파싱하기 위한 JSONParser 객체 생성.
				 JSONObject jsonObject =  (JSONObject) jsonParser.parse(result); // 2. JSON 형태로 파싱한 문자열을 JSONObject 객체에 저장	
				 
			Object choices = jsonObject.get("choices");
			
			  JSONArray jsonArray = (JSONArray) choices; // 5. 리스트(배열)는 JSONArray로 저장 해야하기에 JSONArray로 캐스팅하여 저장
			  
			  JSONObject object = (JSONObject) jsonArray.get(0); 
			  
			  String res = (String) object.get("text");
			  
			  String answer = res.substring(res.lastIndexOf(":")+1)
					  			 .replace("\"", "")
					  			 .replace("{", "")
					  			 .replace("</code>", "")
					  			 .replace("\r", "")
					  			 .replace("\n", "")
					  			.replace("}", "");
			  
			  message.setMsg(answer);
			  message.setStatusCode("2.00");
			  
			  return new ResponseEntity<>(message,HttpStatus.OK);
			  
			}else {
				message.setMsg(Constants.FAIL);
				message.setStatusCode("1.00");
				return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
			}			
		} catch (Exception e) {
			message.setMsg(Constants.FAIL);
			message.setStatusCode("1.00");
			return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
		}
	}	
 }
