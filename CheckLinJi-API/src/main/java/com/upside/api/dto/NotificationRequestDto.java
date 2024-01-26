package com.upside.api.dto;

import lombok.Data;

@Data
public class NotificationRequestDto {
	
	private String fcmToken;
	
	private String nickName;
	
    private String title;

    private String message;
        

}
