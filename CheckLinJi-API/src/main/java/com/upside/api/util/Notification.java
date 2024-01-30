package com.upside.api.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.internal.bind.TreeTypeAdapter;
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
	 * 알림 기능
	 * @param notiDto
	 */
	@Async
	public void pushNofication (NotificationRequestDto notiDto) {
						
		try {
			
			// 알람 객체 생성
			NotificationDto notificationDto = new NotificationDto();
			
			// 알람 토큰 저장
			notificationDto.setTo(notiDto.getFcmToken());
			
			// 알람 Android 용 데이터 저장
			NotificationDto.Data data = new NotificationDto.Data();
			
			data.setTitle(notiDto.getTitle());
			data.setMessage(notiDto.getMessage());
			notificationDto.setData(data);
			
			// 게시글,댓글 일경우 param 2개 , 좋아요일 경우 param 1개 
			if(notiDto.getParamsDepsYn().equals("Y")) {
						
				// param_1 은 게시글  				
				NotificationDto.Params params_1 = new NotificationDto.Params();            		
          		params_1.setRoute(notiDto.getParams().get(0).getRoute());
          		params_1.setPostId(notiDto.getParams().get(0).getPostId());          		
          		          		          		          		
          		// param_2는 댓글
          		NotificationDto.Param params_2 = new NotificationDto.Param();
				params_2.setRoute(notiDto.getParams().get(1).getRoute());
				params_2.setPostId(notiDto.getParams().get(1).getPostId());          		
				params_2.setParams(null);
          		
				params_1.setParams(params_2);
				
          		data.setParams(params_1); 
								       		
			}else {
				
				// 좋아요 
				NotificationDto.Params params_1 = new NotificationDto.Params();            		
          		params_1.setRoute(notiDto.getParams().get(0).getRoute());
          		params_1.setPostId(notiDto.getParams().get(0).getPostId());          		
          		params_1.setParams(null);
          		
          		data.setParams(params_1);             		
			}
						
			// 알람 ios 용 데이터 저장
			NotificationDto.Notification notification = new NotificationDto.Notification();
			notification.setTitle(notiDto.getTitle());
			notification.setBody(notiDto.getMessage());
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
