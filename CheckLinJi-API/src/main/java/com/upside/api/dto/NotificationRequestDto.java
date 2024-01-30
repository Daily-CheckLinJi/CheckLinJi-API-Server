package com.upside.api.dto;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class NotificationRequestDto {
	
	private String fcmToken;
	
	private String nickName;
	
    private String title;

    private String message;
            
    private List<Params> params;
    
    private String paramsDepsYn;
    
    
    @Setter
    @Getter
    public static class Params {
        private String route;
        private Long postId;

        
    }
    
        

}
