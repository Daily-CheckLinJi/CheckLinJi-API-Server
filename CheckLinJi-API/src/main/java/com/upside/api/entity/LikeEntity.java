package com.upside.api.entity;

import java.time.LocalDate;

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
@Table(name = "likes")
public class LikeEntity { // Like 테이블: 인증글 좋아요 테이블 - 서브미션,멤버 테이블 조인

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long likeSeq;
 
 @ManyToOne
 @JoinColumn(name = "challenge_submission_id")
 private ChallengeSubmissionEntity challengeSubmissionId;
 
 @ManyToOne(fetch = FetchType.LAZY) // Challenge 입장에선 Member와 다대일 관계이므로 @ManyToOne이 됩니다.
 @JoinColumn(name = "email") // 외래 키를 매핑할 때 사용합니다. name 속성에는 매핑 할 외래 키 이름을 지정합니다.
 private MemberEntity memberEntity;
  
 @Column(name = "createDate")
 private LocalDate createDate;
 
 
 

 
 
 
@Builder
public LikeEntity(ChallengeSubmissionEntity challengeSubmissionId , MemberEntity memberEntity , LocalDate createDate) {
	super();
	this.challengeSubmissionId = challengeSubmissionId;
	this.memberEntity = memberEntity;
	this.createDate = createDate;	
	
	}

}




