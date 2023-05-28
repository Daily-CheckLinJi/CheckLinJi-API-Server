package com.upside.api.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate  // 변경된 필드만 적용
@DynamicInsert  // 변경된 필드만 적용
@Table(name = "HashTag")
public class HashTagEntity { // HashTag 테이블: 

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "hashTagId")
private Long hashTagId;

@Column(name = "tagName")
private String tagName;

@Column(name = "createDate")
private LocalDateTime createDate;
 
 
 
@Builder
public HashTagEntity(String tagName , LocalDateTime createDate) {
	super();
	this.tagName = tagName;
	this.createDate = createDate;
	
	}

}




