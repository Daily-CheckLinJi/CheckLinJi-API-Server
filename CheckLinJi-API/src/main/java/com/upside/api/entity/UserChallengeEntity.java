package com.upside.api.entity;

import java.time.LocalDateTime;

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
@Table(name = "UserChallenge")
public class UserChallengeEntity { // 사용자가 참여한 첼린지 정보를 저장하는 테이블

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "user_challenge_id")
 private Long userChallengeId;
 
 @ManyToOne(fetch = FetchType.LAZY) // Challenge 입장에선 Member와 다대일 관계이므로 @ManyToOne이 됩니다.
 @JoinColumn(name = "email") // 외래 키를 매핑할 때 사용합니다. name 속성에는 매핑 할 외래 키 이름을 지정합니다.
 private MemberEntity memberEntity;

 @ManyToOne(fetch = FetchType.LAZY , optional = true) // optional = 널값 허용 
 @JoinColumn(name = "challenge_name")
 private ChallengeEntity challengeEntity ;
 
 @Column(nullable = false) // 등록일
 private LocalDateTime registrationTime;

 @Column(nullable = false) // 완료 여부
 private boolean completed;
 
 
@Builder
public UserChallengeEntity(MemberEntity memberEntity , ChallengeEntity challengeEntity , LocalDateTime registrationTime , boolean completed) {		
	super();
	this.memberEntity = memberEntity;
	this.challengeEntity = challengeEntity;	
	this.registrationTime = registrationTime;
	this.completed = completed;
}
}
