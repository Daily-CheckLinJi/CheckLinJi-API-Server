package com.upside.api.controller;



import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upside.api.config.JwtTokenProvider;
import com.upside.api.dto.MemberDto;
import com.upside.api.entity.MemberEntity;
import com.upside.api.service.MemberService;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
	
	private final MemberService memberService ;
			
	private final JwtTokenProvider jwtTokenProvider;
	

	
	
	
	@GetMapping 						  /* default size = 10 */
	public Page<MemberEntity> memberList(@PageableDefault (sort = "email", direction = Sort.Direction.DESC) Pageable pageable  ) {
				
				
		return memberService.memberList(pageable);
	}
	
	@PostMapping("/email")
	public ResponseEntity<Map<String , Object>> selectMember(@RequestHeader("Authorization") String authHeader) {				
		Map<String , Object> result = new HashMap<String, Object>();	
			
		    String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		
			result = memberService.selectMember(userEmail);
		
		if (result.get("HttpStatus").equals("2.00")) { // 성공
			
			return new ResponseEntity<>(result,HttpStatus.OK);			
		} else {			
			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 			
	}
	
	@PostMapping("/validateDuplicated")
	public ResponseEntity<Map<String , String>> validateDuplicated (@RequestBody MemberDto memberDto) {
		
		Map<String , String> result = new HashMap<String, String>();
		
		if(memberDto.getEmail() != null) {
			 result = memberService.validateDuplicatedEmail(memberDto.getEmail());
			 if (result.get("HttpStatus").equals("2.00")) { // 성공
				 return new ResponseEntity<>(result,HttpStatus.OK); 
			 }else {
				 return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
			 }			 
		}
		
		if(memberDto.getNickName() != null) {
			 result = memberService.validateDuplicatedNickName(memberDto.getNickName());
			 if (result.get("HttpStatus").equals("2.00")) { // 성공
				 return new ResponseEntity<>(result,HttpStatus.OK); 
			 }else {
				 return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
			 }			 
		}
				
		return new ResponseEntity<>(result,HttpStatus.OK);		
					
	}
	
	
	@PostMapping("/sign")
	public ResponseEntity<Map<String , String>> signUp(@RequestBody MemberDto memberDto) {
			
		Map<String, String> result = memberService.signUp(memberDto);
				
		if (result.get("HttpStatus").equals("2.00")) { // 성공						
			return new ResponseEntity<>(result,HttpStatus.OK);			
		} else {			
			return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		} 					
	}
	
	/**
	 * 서비스 이용 날짜
	 * @param authHeader
	 * @param pageDto
	 * @return
	 */
	@GetMapping("/joinDate") 
	public ResponseEntity<Map<String, Object>> joinDate (@RequestHeader("Authorization") String authHeader ) {				
				
				String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
				
				Map<String, Object> result = memberService.joinDate(userEmail);
						
				if (result.get("HttpStatus").equals("2.00")) { // 성공					
					return new ResponseEntity<>(result,HttpStatus.OK);					
				} else {							
					return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
				} 
					
	}
	
	
	@PostMapping("/update")
	public ResponseEntity<Map<String, String>> updateMember(@RequestBody MemberDto memberDto) {
		
		 Map<String, String> result = memberService.updateMember(memberDto);
		 		 		 
		 if(result.get("HttpStatus").equals("2.00")) {
			 return new ResponseEntity<>(result, HttpStatus.OK);
		 } else {			 			
			 return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		 }		
	}
	
	@PostMapping("/delete")
	public ResponseEntity<Map<String, String>> deleteMember(@RequestBody MemberDto memberDto) {
		
		Map<String, String> result = memberService.deleteMember(memberDto.getEmail());
		 		 		
		 if(result.get("HttpStatus").equals("2.00")) {			 			
			 return new ResponseEntity<>(result, HttpStatus.OK);
		 } else {			 			 
			 return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
		 }				
		
	}
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> loginMember(@RequestBody MemberDto memberDto) {			
				 		 		  		 
		 Map<String, String> result = memberService.loginMember(memberDto);
		 
		 		 
		 if(result.get("HttpStatus").equals("2.00")) {			 		 
			 return new ResponseEntity<>(result, HttpStatus.OK);
		 } else {			 
			 return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST); 
		 }
	}
	
	 // JWT 토큰 검증 요청 처리
	@PostMapping("/refreshToken")
	public ResponseEntity<Map<String, String>> validateRefreshToken (@RequestBody MemberDto memberDto) {							 

		 Map<String, String> result = memberService.validateRefreshToken(memberDto);
		 		 		 
		 if(result.get("HttpStatus").equals("2.00")) {			 		 			 
			 return new ResponseEntity<>(result, HttpStatus.OK);
		 } else {			 
			 return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST); 
		 }
	}
	
	
	 // 프로필 업데이트
		@PostMapping("/update/profile")
		public ResponseEntity<Map<String, String>> updateProfile (@RequestBody MemberDto memberDto , @RequestHeader("Authorization") String authHeader) {							 
			 
			
			 String userEmail = jwtTokenProvider.getEmail(authHeader); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
			 
			 Map<String, String> result = memberService.updateProfile(memberDto.getProfile(),memberDto.getProfileName(),userEmail);
			 			 			 
			 if(result.get("HttpStatus").equals("2.00")) {			 		 			 				 				 				 				 				 
				 return new ResponseEntity<>(result, HttpStatus.OK);
			 } else {			 				 				 
				 return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST); 
			 }
		}
		
		/**
		 * 총 사용자 수 
		 * @return
		 */
		@PostMapping("/cnt")
		public ResponseEntity<Map<String, String>> memberCnt () {
						 			 
			 Map<String, String> result = memberService.findMemCnt();
			 
			 
			 if(result.get("HttpStatus").equals("2.00")) {

				 return new ResponseEntity<>(result, HttpStatus.OK);
			 } else {

				 return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
			 }		
		}	
	
	
		
	@PostMapping("/changePassword")
	public ResponseEntity<Map<String, String>> changePassword (@RequestBody MemberDto memberDto , @RequestHeader("Authorization") String accessToken) {			
		
		 String accEmail = jwtTokenProvider.getEmail(accessToken); // email을 얻기위해 헤더에서 토큰을 디코딩하는 부분이다.
		 		 		 		 
		 memberDto.setEmail(accEmail);
		 memberDto.setAccessToken(accessToken);		 		
		                                 
		 Map<String, String> result = memberService.changePassword(memberDto);
		 		 		 
		 if(result.get("HttpStatus").equals("2.00")) {			 		 
			 return new ResponseEntity<>(result, HttpStatus.OK);
		 } else {			 
			 return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST); 
		 }
	}
	
	@PostMapping("/changePasswordMail")
	public ResponseEntity<Map<String, String>> changePasswordMail (@RequestBody MemberDto memberDto) {			
		
		 		 		 
		 Map<String, String> result = memberService.changePasswordMail(memberDto);
		 		 		 
		 if(result.get("HttpStatus").equals("2.00")) {			 		 
			 return new ResponseEntity<>(result, HttpStatus.OK);
		 } else {			 
			 return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST); 
		 }
	}

  }
