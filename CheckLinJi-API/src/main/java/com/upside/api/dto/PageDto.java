package com.upside.api.dto;

import lombok.Data;

@Data
public class PageDto {
	
	private String email;
	private Long startPage;
	private Long endPage;

}
