package com.upside.api.controller;



import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.upside.api.service.WiseSayingAPIService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/external")
public class ExternalAPIController {
	
	private final WiseSayingAPIService wiseSayingAPISerivce;
	
		
	@GetMapping 						  	
	public String wiseSayingAPI () {
				
		return wiseSayingAPISerivce.getWiseSaying();
					
	 }
	

	
	
 }
