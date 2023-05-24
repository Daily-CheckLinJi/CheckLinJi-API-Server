package com.upside.api.controller;



import java.io.IOException;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.upside.api.dto.ChallengeDto;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.dto.MemberDto;
import com.upside.api.dto.MessageDto;
import com.upside.api.dto.UserChallengeDto;
import com.upside.api.service.ChallengeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/challenge")
public class ChallengeController {
	
	private final ChallengeService challengeSerivce ;
				
	
	
	
	@PostMapping("/list") 
	public ResponseEntity<Map<String, Object>> viewChallenge (@RequestBody UserChallengeDto userChallengeDto) {
							
		Map<String, Object> result = challengeSerivce.viewChallenge(userChallengeDto.getEmail());
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공			
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}
	
	@PostMapping("/create") // 첼린지 생성
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
	
	@PostMapping("/join") // 첼린지 참가
	public ResponseEntity<MessageDto> joinChallenge (@RequestBody UserChallengeDto userChallengeDto) {
			
		MessageDto message = new MessageDto();		
		Map<String, String> result = challengeSerivce.joinChallenge(userChallengeDto.getChallengeName() , userChallengeDto.getEmail());
				
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
	
	@PostMapping("/submit") // 첼린지 제출
	public ResponseEntity<MessageDto> submitChallenge (@RequestParam("file") MultipartFile file ,  ChallengeSubmissionDto submissonDto) throws IOException {
			
		MessageDto message = new MessageDto();		
		Map<String, String> result = challengeSerivce.submitChallenge(file , submissonDto);
				
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
