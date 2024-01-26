package com.upside.api.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.upside.api.dto.ApplePublicKeys;
import com.upside.api.dto.AppleTokenDto;

// 호출할 URL 설정
@FeignClient(name = "httpToApple", url = "https://appleid.apple.com/auth")
public interface AppleHttpClient {

	 // apple publicKey 목록 가져오기
	 @GetMapping("/keys")
	 ApplePublicKeys getAppleAuthPublicKey();
	 
	//아래 내용 추가
	@PostMapping(value = "/token", consumes = "application/x-www-form-urlencoded")
	AppleTokenDto.Response getToken(AppleTokenDto.Request request);
}