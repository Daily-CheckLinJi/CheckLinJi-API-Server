package com.upside.api.controller;




import java.io.ByteArrayInputStream;
import java.io.OutputStream;

import org.apache.poi.util.IOUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.upside.api.util.ExcelApiDownload;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/excel")
public class ExcelController {
    
    
    private final ExcelApiDownload excelApi;
        


    
//    @PostMapping("/memberList")     
//    public void MemberListExcel(@ModelAttribute MemberDto memberDto ,  HttpServletResponse response) throws Exception {    	
//    	String data = "memberListExcel";    	
//    	
//    	boolean result = excel.getReserveExcel(memberDto,data,response);         
//         	
//    } 
    
    
    @ResponseBody
    @PostMapping("/memberList")     
    public void MemberListExcel(HttpServletResponse response) throws Exception {
    	
    	String data = "memberListExcel";    	
    	    	
    	// 엑셀 데이터 생성
    	ByteArrayInputStream result = excelApi.getReserveExcel(data,response);  
    	
    	if(result != null) {    		 
    		 
    		// 생성된 엑셀 데이터를 response로 전송
    		 response.setContentType("application/octet-stream");             
             response.setHeader("Content-Disposition", "attachment; filename=memberListExcel.xlsx");
                                       
             try (OutputStream out = response.getOutputStream()) { // HttpServletResponse 객체의 출력 스트림. 
            	    IOUtils.copy(result, out); // 입력 스트림으로부터 데이터를 읽어 출력 스트림으로 전송하는 기능 ( result 객체에서 데이터를 읽어 out 객체로 전송하는 역할 )
            	} finally {
            	    IOUtils.closeQuietly(result);
            	}
    		
    	}     	    	                
    }
}
