package com.upside.api.entity;

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
@Table(name = "ReportSubmission")
public class ReportSubmissionEntity {  //  게시글 신고 테이블

	
	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 private Long ReportSubmissionSeq; // 게시글 신고 고유 시퀀스
	
	 @Column(nullable = false)
	 private Long challengeSubmissionId; // 신고할 게시글
	 
	 @Column(nullable = false)
	 private String email; // 신고한 유저 이메일
		 
	 @Column(nullable = false) 
	 private String reportMsg; // 신고 메시지
	 
	 @Column(nullable = false) 
	 private String reportDate; // 신고 일시

 


@Builder
public ReportSubmissionEntity(Long challengeSubmissionId , String email , String reportMsg , String reportDate ) {				
	super();
	this.challengeSubmissionId = challengeSubmissionId;	
	this.email = email;
	this.reportMsg = reportMsg;
	this.reportDate = reportDate;
	
	}
}
