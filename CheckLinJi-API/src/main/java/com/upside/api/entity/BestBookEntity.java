package com.upside.api.entity;

import java.time.LocalDate;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
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
@Table(name = "BestBook")
public class BestBookEntity { // User 테이블: 사용자 정보를 저장하는 테이블

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long saySeq;
 
 @Column(name = "name")
 private String name;
 
 @Column(name = "date")
 private String date;
 
 @Column(name = "rank")
 private Integer rank;
 
 @Column(name = "type")
 private String type;
 
 @Lob
 @Column(columnDefinition = "LONGBLOB")
 private byte[] image;
 
 @Column(name = "updateDate")
 private LocalDate updateDate;
 
 
 

 
 
 
@Builder
public BestBookEntity(String name , String date , Integer rank , String type , byte[] image ,LocalDate updateDate) {
	super();
	this.name = name;
	this.date = date;
	this.rank = rank;
	this.type = type;
	this.image = image;
	this.updateDate = updateDate;
	
	}

}




