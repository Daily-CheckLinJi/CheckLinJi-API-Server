package com.upside.api.controller;




import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upside.api.dto.HashTagDto;
import com.upside.api.service.TagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/tag")
public class TagController {
    
    private final TagService tagService;
        


    @PostMapping("/list")     
    public ResponseEntity<Map<String, Object>> listTag(@RequestBody HashTagDto hashTagDto ) throws Exception {    	
    	    	    	
    	Map<String, Object> result = tagService.listTag(hashTagDto);
    	    	    	
		if(result.get("HttpStatus").equals("1.00")){
				
		return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		
		} 
		
		return new ResponseEntity<>(result,HttpStatus.OK);
		
    	}  
    
    @PostMapping("/add")     
    public ResponseEntity<Map<String, String>> addTag(@RequestBody HashTagDto hashTagDto ) throws Exception {    	
    	    	    	
    	Map<String, String> result = tagService.addTag(hashTagDto);
    	    	    	
		if(result.get("HttpStatus").equals("1.00")){
				
		return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		
		} 
		
		return new ResponseEntity<>(result,HttpStatus.OK);
		
    	} 
    
        
    @PostMapping("/delete")     
    public ResponseEntity<Map<String, String>> deleteTag(@RequestBody HashTagDto hashTagDto ) throws Exception {    	
    	    	    	
    	Map<String, String> result = tagService.deleteTag(hashTagDto);
    	    	    	
		if(result.get("HttpStatus").equals("1.00")){
				
		return new ResponseEntity<>(result,HttpStatus.BAD_REQUEST);
		
		} 
		
		return new ResponseEntity<>(result,HttpStatus.OK);
		
    	} 
  
}
