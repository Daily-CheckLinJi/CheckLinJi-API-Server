package com.upside.api.controller;



import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upside.api.config.JwtTokenProvider;
import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.dto.CommentDto;
import com.upside.api.dto.RankingMessageDto;
import com.upside.api.service.CommentService;
import com.upside.api.service.MissionService;
import com.upside.api.util.DateTime;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {
	
	private final CommentService commentService;
	private final JwtTokenProvider jwtTokenProvider;				
	
	
	/**
	  * 유저 댓글 등록
	  * @param commentDto
	  * @return
	  */	
	@PostMapping("/submit") // 첼린지 생성
	public ResponseEntity<Map<String,String>> userCommentSubmit (@RequestHeader("Authorization") String authHeader , @RequestBody CommentDto commentDto) {
							
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		commentDto.setEmail(userEmail);
		commentDto.setRegistDate(DateTime.nowDate());
		
		
		Map<String, String> result = commentService.userCommentSubmit(commentDto);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
		
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
		
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}

	/**
	  * 유저 댓글 수정
	  * @param commentDto
	  * @return
	  */	
	@PostMapping("/update") // 첼린지 생성
	public ResponseEntity<Map<String,String>> userCommentUpdate (@RequestBody CommentDto commentDto) {
							
		commentDto.setUpdateDate(DateTime.nowDate());
		
		Map<String, String> result = commentService.userCommentUpdate(commentDto);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
		
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
		
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}

	
	/**
	  * 유저 댓글 삭제
	  * @param commentDto
	  * @return
	  */	
	@PostMapping("/delete") // 첼린지 생성
	public ResponseEntity<Map<String,String>> userCommentDelete (@RequestBody CommentDto commentDto) {
							
		Map<String, String> result = commentService.userCommentDelete(commentDto);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공
		
			return new ResponseEntity<>(result,HttpStatus.OK);					
		} else {			
		
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}
}
