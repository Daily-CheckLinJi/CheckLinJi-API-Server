package com.upside.api.controller;



import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.upside.api.config.JwtTokenProvider;
import com.upside.api.dto.ChallengeDto;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.dto.MessageDto;
import com.upside.api.dto.PageDto;
import com.upside.api.dto.RankingMessageDto;
import com.upside.api.dto.UserChallengeDto;
import com.upside.api.service.ChallengeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge")
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
		
		 						
		Map<String, Object> result = challengeSerivce.viewChallengeList(pageDto);
				
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
	public ResponseEntity<Map<String, Object>> detail (@RequestBody ChallengeSubmissionDto submissionDto) {		
											
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
		
		Map<String, Object> result = challengeSerivce.viewChallengeList(pageDto);
		
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
	@GetMapping("/missionCompletedCnt") 
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
	@GetMapping("/missionCompletedCntAll") 
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
	 * 첼린지 생성
	 * @param challengeDto
	 * @return
	 */
	@PostMapping("/create") 
	public ResponseEntity<MessageDto>createChallenge (@RequestBody ChallengeDto challengeDto) {
		
		MessageDto message = new MessageDto();		
		Map<String, String> result = challengeSerivce.createChallenge(challengeDto);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
			message.setMsg(result.get("Msg"));
			message.setStatusCode(result.get("HttpStatus"));						
			return new ResponseEntity<>(message,HttpStatus.OK);					
		} else {			
			message.setMsg(result.get("Msg"));
			message.setStatusCode(result.get("HttpStatus"));
			return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
		} 
					
	}
	
	/**
	 * 첼린지 참가
	 * @param userChallengeDto
	 * @param authHeader
	 * @return
	 */
	@PostMapping("/join") 
	public ResponseEntity<MessageDto> joinChallenge (@RequestBody UserChallengeDto userChallengeDto , @RequestHeader("Authorization") String authHeader) {
			
		MessageDto message = new MessageDto();
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, String> result = challengeSerivce.joinChallenge(userChallengeDto.getChallengeName() , userEmail);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
			message.setMsg(result.get("Msg"));
			message.setStatusCode(result.get("HttpStatus"));						
			return new ResponseEntity<>(message,HttpStatus.OK);					
		} else {			
			message.setMsg(result.get("Msg"));
			message.setStatusCode(result.get("HttpStatus"));
			return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
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
	public ResponseEntity<Map<String, String>> submitChallenge (@RequestBody ChallengeSubmissionDto submissonDto , @RequestHeader("Authorization") String authHeader ) throws IOException {					
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, String> result = challengeSerivce.submitChallenge(submissonDto , userEmail);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
									
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
	}
	
		@PostMapping("/completed") // 챌린지 완료 처리
		public ResponseEntity<MessageDto> completedChallenge (@RequestBody ChallengeDto challengeDto) {
				
			MessageDto message = new MessageDto();		
			Map<String, String> result = challengeSerivce.createChallenge(challengeDto);
					
			if (result.get("HttpStatus").equals("2.00")) { // 성공
				message.setMsg(result.get("Msg"));
				message.setStatusCode(result.get("HttpStatus"));						
				return new ResponseEntity<>(message,HttpStatus.OK);					
			} else {			
				message.setMsg(result.get("Msg"));
				message.setStatusCode(result.get("HttpStatus"));
				return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
			} 
  }
				

}
