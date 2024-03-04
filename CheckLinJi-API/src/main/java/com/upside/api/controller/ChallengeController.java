package com.upside.api.controller;



import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upside.api.config.JwtTokenProvider;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.dto.PageDto;
import com.upside.api.service.ChallengeService;
import com.upside.api.util.Utill;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge")
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
public class ChallengeController {
	
	private final ChallengeService challengeSerivce ;
	private final JwtTokenProvider jwtTokenProvider;	
	
	
	
	
	/**
	 * 첼린지 인증글 리스트
	 * @param pageDto
	 * @return
	 */
	@PostMapping("/list") 
	public ResponseEntity<Map<String, Object>> viewChallengeList (@RequestBody PageDto pageDto) {
			
		Map<String, Object> result = challengeSerivce.viewChallengeListTest(pageDto);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공			
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}
		
	
	/**
	 * 첼린지 인증글 상세페이지
	 * @param submissionDto
	 * @return
	 */
	@PostMapping("/list/detail") 
	public ResponseEntity<Map<String, Object>> detail (@RequestHeader("Authorization") String authHeader , @RequestBody ChallengeSubmissionDto submissionDto) {		
											
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		submissionDto.setEmail(userEmail);
		
		Map<String, Object> result = challengeSerivce.detail(submissionDto);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공			
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}
	
	/**
	 * 본인 첼린지 인증글 리스트
	 * @param authHeader
	 * @param pageDto
	 * @return
	 */
	@PostMapping("/myList") 
	public ResponseEntity<Map<String, Object>> viewChallenge (@RequestHeader("Authorization") String authHeader , @RequestBody PageDto pageDto) {
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		pageDto.setEmail(userEmail);
		
		Map<String, Object> result = challengeSerivce.userChallengeList(pageDto);
		
		if (result.get("HttpStatus").equals("2.00")) { // 성공			
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {						
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}
	
	/**
	 * 유저 상세페이지
	 * @param authHeader
	 * @param pageDto
	 * @return
	 */
	@PostMapping("/userDetail") 
	public ResponseEntity<Map<String, Object>> userList (@RequestHeader("Authorization") String authHeader , @RequestBody PageDto pageDto) {
				
		Map<String, Object> result = challengeSerivce.userChallengeList(pageDto);
		
		if (result.get("HttpStatus").equals("2.00")) { // 성공			
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}	
	
	
	/**
	 * 본인 첼린지 인증 성공 횟수 (월)
	 * @param authHeader
	 * @param pageDto
	 * @return
	 */
	@PostMapping("/missionCompletedCnt") 
	public ResponseEntity<Map<String, String>> missionCompletedCnt (@RequestHeader("Authorization") String authHeader ) {				
				
				String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
				
				Map<String, String> result = challengeSerivce.missionCompletedCnt(userEmail);
						
				if (result.get("HttpStatus").equals("2.00")) { // 성공					
					return new ResponseEntity<>(result,HttpStatus.OK);					
				} else {							
					return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
				} 
					
	}
	
	/**
	 * 본인 첼린지 인증 성공 횟수 (월)
	 * @param authHeader
	 * @param pageDto
	 * @return
	 */
	@PostMapping("/missionCompletedCntAll") 
	public ResponseEntity<Map<String, String>> missionCompletedCntAll (@RequestHeader("Authorization") String authHeader ) {				
				
				String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
				
				Map<String, String> result = challengeSerivce.missionCompletedCntAll(userEmail);
						
				if (result.get("HttpStatus").equals("2.00")) { // 성공					
					return new ResponseEntity<>(result,HttpStatus.OK);					
				} else {							
					return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
				} 
					
	}
	
	
	/**
	 * 첼린지 제출
	 * @param submissonDto
	 * @param authHeader
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/submit") 
	public ResponseEntity<Map<String, String>> submitChallenge (@RequestBody ChallengeSubmissionDto submissonDto , @RequestHeader("Authorization") String authHeader ) {					
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, String> result = challengeSerivce.submitChallenge(submissonDto , userEmail);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
									
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
	}
	
//	@PostMapping("/completed") // 챌린지 완료 처리
//	public ResponseEntity<Map<String, String>> completedChallenge (@RequestBody ChallengeDto challengeDto) {
//							
//		Map<String, String> result = challengeSerivce.createChallenge(challengeDto);
//				
//		if (result.get("HttpStatus").equals("2.00")) { // 성공														
//			return new ResponseEntity<>(result,HttpStatus.OK);					
//		} else {											
//			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
//		} 
//  }
				

		/**
		 * 사람들이 많이 구경하는 첵린저 리스트
		 * @param pageDto
		 * @return
		 */
		@PostMapping("/checkRinger") 
		public ResponseEntity<Map<String, Object>> viewCheckRinger (@RequestBody PageDto pageDto) {
			
			 						
			Map<String, Object> result = challengeSerivce.viewCheckRinger(pageDto);
					
			if (result.get("HttpStatus").equals("2.00")) { // 성공			
				return new ResponseEntity<>(result,HttpStatus.OK);					
			} else {			
				
				return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
			} 
						
		}
		
		/**
		 * 첼린지 인증글 리스트
		 * @param pageDto
		 * @return
		 */
		@PostMapping("/test") 
		public ResponseEntity<Map<String, Object>> viewChallengeListTest (@RequestBody PageDto pageDto) {
					 						
			Map<String, Object> result = challengeSerivce.viewChallengeListTest(pageDto);
					
			if (result.get("HttpStatus").equals("2.00")) { // 성공			
				return new ResponseEntity<>(result,HttpStatus.OK);					
			} else {			
				
				return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
			} 
						
		}		
		
		/**
		 * 첼린지 인증글 리스트
		 * @param pageDto
		 * @return
		 */
		@GetMapping("/subImage")		
		public ResponseEntity<byte[]> subImage (@RequestParam String imageRoute) {
					 	
			   try {
				   
				    log.info("파일 다운로드 Start ------- > " + imageRoute);
					        	
		            // 이미지를 읽어옴
		            Path imagePath = Paths.get(imageRoute);
		            byte[] imageData = Files.readAllBytes(imagePath);
		            
		            // 헤더 이미지 타입 
		            HttpHeaders headers = Utill.getFileExtension(imagePath);
		            
		            // ResponseEntity로 이미지 데이터와 헤더를 포함한 응답
		            return new ResponseEntity<>(imageData,headers,HttpStatus.OK);
		        } catch (IOException e) {
		        	log.error("파일 다운로드 Error ------- > ",e);
		            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		        }
		    }
		
}
