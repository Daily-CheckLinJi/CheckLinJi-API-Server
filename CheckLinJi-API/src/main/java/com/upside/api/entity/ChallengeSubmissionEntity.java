package com.upside.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@DynamicUpdate  // 변경된 필드만 적용
@DynamicInsert  // 변경된 필드만 적용
@Table(name = "ChallengeSubmission")
public class ChallengeSubmissionEntity { // ChallengeSubmission 테이블: 사용자가 첼린지에 대한 제출 정보를 저장하는 테이블

	 @Id
	 @GeneratedValue(strategy = GenerationType.IDENTITY)
	 @Column(name = "challenge_submission_id")
	 private Long challengeSubmissionId;
	 	 
	 @Column(nullable = false) // 제출 일시 (일)
	 private Long  userChallengeId;
		 
	 @Column(nullable = false) // 제출 일시 (일)
	 private String submissionDate;
	 
	 @Column(nullable = false) // 제출 일시 (시간)
	 private String submissionTime;
	 	 
	 @Column(nullable = true) // 내용
	 private String  submissionText; 
	 
	 @Column(nullable = true) // 내용
	 private String  nickName; 
	 
	 @Column(nullable = true) // 사진
	 private String  submissionImageRoute; 
	 
	 @Column(nullable = true) // 인증 성공 유무 
	 private String  submissionCompleted;
 


@Builder
public ChallengeSubmissionEntity(Long userChallengeId , String submissionDate , String submissionTime 
		, String submissionText  , String nickName , String submissionImageRoute , String submissionCompleted) {		
	super();
	this.userChallengeId = userChallengeId;	
	this.submissionDate = submissionDate;
	this.submissionTime = submissionTime;
	this.submissionText = submissionText;
	this.nickName = nickName;
	this.submissionImageRoute = submissionImageRoute ;
	this.submissionCompleted = submissionCompleted;

}
}
