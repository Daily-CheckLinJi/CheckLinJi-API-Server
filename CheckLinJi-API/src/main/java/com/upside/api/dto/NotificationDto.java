package com.upside.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class NotificationDto {
	
    private String to;

    private Data data;

    private Notification notification;

    // 생성자, 게터, 세터 등을 추가할 수 있습니다.
    
    @Setter
    @Getter
    public static class Data {
        private String title;
        private String message;

        
    }

    @Setter
    @Getter
    public static class Notification {
        private String title;
        private String body;

        
    }

}
