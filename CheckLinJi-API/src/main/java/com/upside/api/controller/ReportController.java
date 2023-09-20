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
import com.upside.api.dto.ReportCommentDto;
import com.upside.api.dto.ReportSubmissionDto;
import com.upside.api.service.ReportService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/report")
public class ReportController {
		
	private final ReportService reportService;	
	private final JwtTokenProvider jwtTokenProvider;	
	
	
	
	
	/**
	 * 게시글 신고
	 * @param authHeader
	 * @param repoSubmissionDto
	 * @return
	 */
	@PostMapping("/submission") 
	public ResponseEntity<Map<String, String>> reportSubmission (@RequestHeader("Authorization") String authHeader , @RequestBody ReportSubmissionDto repoSubmissionDto) {
		
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		repoSubmissionDto.setEmail(userEmail);
		
		Map<String, String> result = reportService.reportSubmission(repoSubmissionDto);
				
		if(result.get("HttpStatus").equals("2.00")) { // 성공			
			return new ResponseEntity<>(result,HttpStatus.OK);					
		}else{						
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
					
	}
	
	/**
	 * 게시글 댓글 신고
	 * @param authHeader
	 * @param repoSubmissionDto
	 * @return
	 */
	@PostMapping("/comment") 
	public ResponseEntity<Map<String, String>> detail (@RequestHeader("Authorization") String authHeader , @RequestBody ReportCommentDto reCommentDto) {		
											
		String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
		reCommentDto.setEmail(userEmail);
		
		Map<String, String> result = reportService.reportComment(reCommentDto);
				
		if(result.get("HttpStatus").equals("2.00")) { // 성공			
			return new ResponseEntity<>(result,HttpStatus.OK);					
		}else{						
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 
				
	}
}
