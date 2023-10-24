package com.upside.api.controller;



import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upside.api.config.JwtTokenProvider;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.service.MissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission")
public class MissionController {
	
	private final MissionService missionSerivce ;
	private final JwtTokenProvider jwtTokenProvider;				
	
	
	/**
	  * 미션 성공 총 횟수 (월)
	  * @param memberDto
	  * @return
	  */	
	@PostMapping("/completed") // 첼린지 생성
	public ResponseEntity<Map<String, Object>> missionCompletedCnt (@RequestHeader("Authorization") String authHeader) {
								
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
				
		Map<String, Object> result = missionSerivce.missionCompletedCnt(userEmail);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}
	
	/**
	  * 실시간 랭킹
	  * @param memberDto
	  * @return
	  */
	@PostMapping("/ranking") 
	public ResponseEntity<Map<String, Object >> missionRanking (@RequestHeader("Authorization") String authHeader) {
		
					
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, Object > result = missionSerivce.missionRanking(userEmail);				
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
			return new ResponseEntity<>(result,HttpStatus.OK);	
		} else {			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		}
		
				
	}
	
	/**
	 * 본인 미션 달력
	 * @param challengeSubmissionDto
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/myAuth")
    public ResponseEntity<Map<String, Object>> myAuth(@RequestHeader("Authorization") String authHeader , @RequestBody ChallengeSubmissionDto challengeSubmissionDto) throws Exception {
	 	
					
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, Object> result = missionSerivce.myAuth(challengeSubmissionDto , userEmail);
	  	
		if (result.get("HttpStatus").equals("2.00")) { // 성공			
			return new ResponseEntity<>(result,HttpStatus.OK);
		} else {						
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		}
						
	} 
 
	
	/**
	 * 본인 미션 상세보기
	 * @param challengeSubmissionDto
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/info")
    public ResponseEntity<Map<String,Object>> myAuthInfo(@RequestHeader("Authorization") String authHeader , @RequestBody ChallengeSubmissionDto challengeSubmissionDto) throws Exception {
	 								
		Map<String, Object > result = missionSerivce.myAuthInfo(challengeSubmissionDto);
	  	
		if(result.get("HttpStatus").equals("2.00")) { // 성공															    
			return new ResponseEntity<>(result,HttpStatus.OK);			
		}else{
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		}		
	} 
	
	/**
	 * 본인 미션 수정
	 * @param challengeSubmissionDto
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/update")
    public ResponseEntity<Map<String,String>> missionUpdate(@RequestHeader("Authorization") String authHeader , @RequestBody ChallengeSubmissionDto challengeSubmissionDto) throws Exception {
	 	
			
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		challengeSubmissionDto.setEmail(userEmail);
		
		Map<String, String > result = missionSerivce.missionUpdate(challengeSubmissionDto);
	  	
		if (result.get("HttpStatus").equals("2.00")) { // 성공															    
			return new ResponseEntity<>(result,HttpStatus.OK);			
		} else {
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		}
		
				
	} 
	
	/**
	 * 본인 미션 삭제하기
	 * @param challengeSubmissionDto
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/myAuth/delete")
    public ResponseEntity<Map<String, Object>> myAuthDelete(@RequestHeader("Authorization") String authHeader , @RequestBody ChallengeSubmissionDto challengeSubmissionDto) throws Exception {
	 					
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, Object> result = missionSerivce.myAuthDelete(challengeSubmissionDto , userEmail);
	  	
		if (result.get("HttpStatus").equals("2.00")) { // 성공											
			return new ResponseEntity<>(result,HttpStatus.OK);			
		} else {
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		}
		
				
	} 
}
