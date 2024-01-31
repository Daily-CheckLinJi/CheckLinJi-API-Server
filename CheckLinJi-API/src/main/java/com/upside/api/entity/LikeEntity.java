package com.upside.api.entity;

import java.time.LocalDate;

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
@Table(name = "likes")
public class LikeEntity { // Like 테이블: 인증글 좋아요 테이블 - 서브미션,멤버 테이블 조인

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long likeSeq;
 
 private Long challengeSubmissionId;
 
 private String email;
  
 private String writerEmail;
 
 @Column(name = "createDate")
 private LocalDate createDate;
 
 
 

 
 
 
@Builder
public LikeEntity(Long challengeSubmissionId ,String writerEmail , String email , LocalDate createDate) {
	super();
	this.challengeSubmissionId = challengeSubmissionId;
	this.writerEmail = writerEmail ;
	this.email = email;
	this.createDate = createDate;	
	
	}

}




