package com.upside.api.entity;

import com.upside.api.util.SubmissiontHashTagId;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(SubmissiontHashTagId.class) // 클래스를 복합 키 클래스로 지정합니다. 
@Table(name = "SubmissionHashTag")
public class SubmissionHashTagEntity { // SubmissionHashTag 테이블: 미션 테이블과 태그 테이블을 외래키로 삼아 연관관계 설정

@Id
@ManyToOne
@JoinColumn(name = "challenge_submission_id")
private ChallengeSubmissionEntity challengeSubmissionId;

@Id
@ManyToOne
@JoinColumn(name = "hashTagId")
private HashTagEntity hashTagId;
 
 
 
@Builder
public SubmissionHashTagEntity(ChallengeSubmissionEntity challengeSubmissionId , HashTagEntity hashTagId) {
	super();
	this.challengeSubmissionId = challengeSubmissionId;
	this.hashTagId = hashTagId;
	
	}

}




