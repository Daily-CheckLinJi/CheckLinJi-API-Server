package com.upside.api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.upside.api.dto.MemberDto;
import com.upside.api.util.Utill;

@Controller
@RequestMapping("/api/test")
public class TestController {

	
	
	 // 카카오 로그인 페이지 테스트
    @GetMapping
    public String socialKakaoLogin(ModelAndView mav) {

        return "Login2";
    }
    
//    @GetMapping("/getImageUrl")
//    public ResponseEntity<Map<String, String>> getImageUrl() {
//    	
//    	Map<String, String> result = new HashMap<String, String>();
//    	
//    	// 이미지 파일의 경로를 가져옴
//        String imageUrl = Utill.getImagePath("profile/M-1.png").toString();
//    	
//    	result.put("image", imageUrl);
//    	                
//    	return new ResponseEntity<>(result,HttpStatus.OK);				
//    }
    
    // 카카오 로그인 페이지 테스트
    @PostMapping("/xsss")
    @ResponseBody
    public ResponseEntity<MemberDto> xss (@RequestBody MemberDto memberDto ,Model model) {
    	
    	MemberDto result = new MemberDto();
    	
    	result.setEmail(memberDto.getEmail());
    	
    	System.out.println(memberDto.getEmail());
    	
    	
        return new ResponseEntity<>(result,HttpStatus.OK);
    }
}
