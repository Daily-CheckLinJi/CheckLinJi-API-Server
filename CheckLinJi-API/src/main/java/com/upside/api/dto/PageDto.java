package com.upside.api.dto;

import java.util.List;

import lombok.Data;

@Data
public class PageDto {
	
	private String email;
	private String tag;
	private List<String> tagList;
	private Long startPage;
	private Long endPage;

}
