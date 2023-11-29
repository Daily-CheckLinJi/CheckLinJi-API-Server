package com.upside.api.entity;

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
@Table(name = "SubmissionHashTag")
public class SubmissionHashTagEntity { // SubmissionHashTag 테이블: 미션 테이블과 태그 테이블을 외래키로 삼아 연관관계 설정

	
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long SubmissionHashTagSeq;	
	
@Column(nullable = false)
private Long challengeSubmissionId;

@Column(nullable = false)
private Long hashTagId;
 
 
 
@Builder
public SubmissionHashTagEntity(Long challengeSubmissionId , Long hashTagId) {
	super();
	this.challengeSubmissionId = challengeSubmissionId;
	this.hashTagId = hashTagId;
	
	}

}




