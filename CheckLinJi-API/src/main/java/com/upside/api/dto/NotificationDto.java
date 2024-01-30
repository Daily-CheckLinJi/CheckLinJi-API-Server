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
        private Params params;

        
    }

    @Setter
    @Getter
    public static class Notification {
        private String title;
        private String body;        

        
    }
    
    @Setter
    @Getter
    public static class Params {
        private String route;
        private Long postId;
        private Param params;

        
    }
    
    @Setter
    @Getter
    public static class Param {
        private String route;
        private Long postId;
        private String params;

        
    }

}
