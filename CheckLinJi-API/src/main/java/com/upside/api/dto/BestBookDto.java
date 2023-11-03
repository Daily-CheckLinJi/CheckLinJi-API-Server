package com.upside.api.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BestBookDto {
 
 private int seq;
 private String name;
 private String date;
 private Integer rank;
 private String image;
 private LocalDate updateDate;
 private String type;
 
 
}
