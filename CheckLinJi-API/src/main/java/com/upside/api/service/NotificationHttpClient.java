package com.upside.api.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import com.upside.api.dto.NotificationDto;

// 호출할 URL 설정
@FeignClient(name = "alarmPush", url = "https://fcm.googleapis.com/fcm/")
public interface NotificationHttpClient {
	
	
	 // apple publicKey 목록 가져오기
	 @PostMapping("/send")
	 Map<String, Object> PushNotification( @RequestHeader("Authorization") String authorizationHeader , @RequestBody NotificationDto notificationDto);
	 

}
