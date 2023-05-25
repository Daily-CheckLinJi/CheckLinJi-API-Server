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
import com.upside.api.dto.RankingMessageDto;
import com.upside.api.service.MissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/mission")
public class MissionController {
	
	private final MissionService rankingSerivce ;
	private final JwtTokenProvider jwtTokenProvider;				
	
	
	/**
	  * 미션 성공 총 횟수 (월)
	  * @param memberDto
	  * @return
	  */	
	@PostMapping("/completed") // 첼린지 생성
	public ResponseEntity<RankingMessageDto> missionCompletedCnt (@RequestHeader("Authorization") String authHeader) {
		
		RankingMessageDto message = new RankingMessageDto();		
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, String> result = rankingSerivce.missionCompletedCnt(userEmail);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
			message.setMsg(result.get("Msg"));
			message.setStatusCode(result.get("HttpStatus"));
			message.setOwn(String.valueOf(result.get("own")));
			message.setUser(String.valueOf(result.get("userAvg")));
			return new ResponseEntity<>(message,HttpStatus.OK);					
		} else {			
			message.setMsg(result.get("Msg"));
			message.setStatusCode(result.get("HttpStatus"));
			return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
		} 
					
	}
	
	/**
	  * 실시간 랭킹
	  * @param memberDto
	  * @return
	  */
	@PostMapping("/ranking") // 첼린지 생성
	public ResponseEntity<RankingMessageDto> missionRanking (@RequestHeader("Authorization") String authHeader) {
		
		RankingMessageDto message = new RankingMessageDto();	
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, Object > result = rankingSerivce.missionRanking(userEmail);				
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
			message.setMsg((String) result.get("Msg"));
			message.setStatusCode((String) result.get("HttpStatus"));
			message.setUserList(result.get("missionRankingTop"));
			message.setOwnList(result.get("missionRankingOwn"));													
		} else if (result.get("HttpStatus").equals("2.01")) { // 본인이 참여중이 아닐때			
			message.setMsg((String) result.get("Msg"));
			message.setStatusCode((String) result.get("HttpStatus"));
			message.setUserList(result.get("missionRankingTop"));			
		} else {			
			message.setMsg((String) result.get("Msg"));
			message.setStatusCode((String) result.get("HttpStatus"));			
		}
		
		return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);		
	}
	
	/**
	 * 본인 미션 달력
	 * @param challengeSubmissionDto
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/myAuth")
    public ResponseEntity<RankingMessageDto> myAuth(@RequestHeader("Authorization") String authHeader , @RequestBody ChallengeSubmissionDto challengeSubmissionDto) throws Exception {
	 	
		RankingMessageDto message = new RankingMessageDto();	
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, Object > result = rankingSerivce.myAuth(challengeSubmissionDto , userEmail);
	  	
		if (result.get("HttpStatus").equals("2.00")) { // 성공
			message.setMsg((String) result.get("Msg"));
			message.setStatusCode((String) result.get("HttpStatus"));
			message.setUserList(result.get("missionCalendarOwn"));
			return new ResponseEntity<>(message,HttpStatus.OK);
		} else {			
			message.setMsg((String) result.get("Msg"));
			message.setStatusCode((String) result.get("HttpStatus"));
			return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);
		}
		
				
	} 
 
	
	/**
	 * 본인 미션 상세보기
	 * @param challengeSubmissionDto
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/myAuth/info")
    public ResponseEntity<RankingMessageDto> myAuthInfo(@RequestHeader("Authorization") String authHeader , @RequestBody ChallengeSubmissionDto challengeSubmissionDto) throws Exception {
	 	
		RankingMessageDto message = new RankingMessageDto();
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, Object > result = rankingSerivce.myAuthInfo(challengeSubmissionDto , userEmail);
	  	
		if (result.get("HttpStatus").equals("2.00")) { // 성공											
			    message.setMsg((String) result.get("Msg"));
				message.setStatusCode((String) result.get("HttpStatus"));
				message.setUserList(result.get("missionAuthInfo"));			    
		return new ResponseEntity<>(message,HttpStatus.OK);
			
		} else {			
			message.setMsg((String) result.get("Msg"));
			message.setStatusCode((String) result.get("HttpStatus"));			
		}
		
		return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);		
	} 
	
	/**
	 * 본인 미션 삭제하기
	 * @param challengeSubmissionDto
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/myAuth/delete")
    public ResponseEntity<RankingMessageDto> myAuthDelete(@RequestHeader("Authorization") String authHeader , @RequestBody ChallengeSubmissionDto challengeSubmissionDto) throws Exception {
	 	
		RankingMessageDto message = new RankingMessageDto();	
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		Map<String, Object > result = rankingSerivce.myAuthDelete(challengeSubmissionDto , userEmail);
	  	
		if (result.get("HttpStatus").equals("2.00")) { // 성공											
			    message.setMsg((String) result.get("Msg"));
				message.setStatusCode((String) result.get("HttpStatus"));
				message.setUserList(result.get("missionAuthInfo"));
			    message.setFile((String) result.get("file"));
			
		} else {			
			message.setMsg((String) result.get("Msg"));
			message.setStatusCode((String) result.get("HttpStatus"));			
		}
		
		return new ResponseEntity<>(message,HttpStatus.BAD_REQUEST);		
	} 
}
