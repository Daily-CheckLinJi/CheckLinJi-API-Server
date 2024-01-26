package com.upside.api.util;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.upside.api.dto.NotificationDto;
import com.upside.api.dto.NotificationRequestDto;
import com.upside.api.service.NotificationHttpClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class Notification {
	
	
	 @Value("${fcm.auth.token}")
	 private String fcmAuthToken;
	 
	 private final NotificationHttpClient notificationHttpClient;
	
	/**
	 * 총 사용자 수 
	 * @param userEmail
	 * @return
	 */
	public void pushNofication (NotificationRequestDto notiRequestDto) {
						
		try {
			
			// 알람 객체 생성
			NotificationDto notificationDto = new NotificationDto();
			
			// 알람 토큰 저장
			notificationDto.setTo(notiRequestDto.getFcmToken());
			
			// 알람 Android 용 데이터 저장
			NotificationDto.Data data = new NotificationDto.Data();
			data.setTitle(notiRequestDto.getTitle());
			data.setMessage(notiRequestDto.getMessage());
			notificationDto.setData(data);
	
			// 알람 ios 용 데이터 저장
			NotificationDto.Notification notification = new NotificationDto.Notification();
			notification.setTitle(notiRequestDto.getTitle());
			notification.setBody(notiRequestDto.getMessage());
			notificationDto.setNotification(notification);
						       
			// fireBase 서버로 알람 전송
    	    Map<String, Object> pushAlarm = notificationHttpClient.PushNotification(fcmAuthToken, notificationDto);
    	   	
    	    int successYn = (int) pushAlarm.get("success");
    	    
    	    if(successYn > 0) {
    	    	log.info("알람 성공 ----------------------> " + successYn);    	    	
    	    }else {
    	    	log.error("알람 실패 ---------------------> " + successYn);
    	    }
    	        	   	    	   		    	     									
		} catch (Exception e) {		 
			 log.error("알람 실패 -----------------> " + Constants.SYSTEM_ERROR , e);
		}
       	    			    		  
	}	

}
