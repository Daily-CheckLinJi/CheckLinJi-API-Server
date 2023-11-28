package com.upside.api.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateTime {

	
	
	public static String nowDate() {
		
        LocalDateTime now = LocalDateTime.now();

        // 출력 형식을 지정하여 변환합니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String result = now.format(formatter);        		

        
        return result;
	}
	
	public static String nowDateHour() {
		
        LocalDateTime now = LocalDateTime.now();

        // 출력 형식을 지정하여 변환합니다.
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
        String result = now.format(formatter);        		
        
        return result;
	}
	
	// 사용자의 가입일로부터 현재까지의 경과 일수를 계산
	public static Long userJoinDate(String userJoinDate) throws ParseException {
		
		// SimpleDateFormat을 사용하여 문자열을 Date로 변환
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        
        Date joinDate = dateTimeFormat.parse(userJoinDate);
       
    	Date currentDate = new Date();
    			            	
    	long differenceInMillis = currentDate.getTime() - joinDate.getTime();
    	long differenceInDays = differenceInMillis / (24 * 60 * 60 * 1000);  		
        
        return differenceInDays;
	}
	
}
