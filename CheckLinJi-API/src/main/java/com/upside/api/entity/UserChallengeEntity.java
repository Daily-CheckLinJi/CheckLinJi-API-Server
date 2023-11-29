package com.upside.api.entity;

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
@Table(name = "UserChallenge")
public class UserChallengeEntity { // 사용자가 참여한 첼린지 정보를 저장하는 테이블

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 @Column(name = "user_challenge_id")
 private Long userChallengeId;
 
 @Column(nullable = false) // 이메일
 private String email;
 
 @Column(nullable = false) // 등록일
 private LocalDateTime registrationTime;

 @Column(nullable = false) // 완료 여부
 private boolean completed;
 
 
@Builder
public UserChallengeEntity(String email , LocalDateTime registrationTime , boolean completed) {		
	super();
	this.email = email;	
	this.registrationTime = registrationTime;
	this.completed = completed;
}
}
