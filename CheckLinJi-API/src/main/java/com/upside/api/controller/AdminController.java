package com.upside.api.controller;




import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upside.api.dto.HashTagDto;
import com.upside.api.dto.MessageDto;
import com.upside.api.service.AdminService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
    
    private final AdminService adminService;
        


    
    @PostMapping("/addTag")     
    public ResponseEntity<Map<String, String>> addTag(@RequestBody HashTagDto hashTagDto ) throws Exception {    	
    	    	    	
    	Map<String, String> result = adminService.addTag(hashTagDto);
    	    	    	
		if(result.get("HttpStatus").equals("1.00")){
				
		return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		
		} 
		
		return new ResponseEntity<>(result,HttpStatus.OK);
		
    	} 
    
        
    @PostMapping("/deleteTag")     
    public ResponseEntity<Map<String, String>> deleteTag(@RequestBody HashTagDto hashTagDto ) throws Exception {    	
    	    	    	
    	Map<String, String> result = adminService.deleteTag(hashTagDto);
    	    	    	
		if(result.get("HttpStatus").equals("1.00")){
				
		return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		
		} 
		
		return new ResponseEntity<>(result,HttpStatus.OK);
		
    	} 
  
}
