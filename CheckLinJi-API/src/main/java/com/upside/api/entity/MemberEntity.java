package com.upside.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "MemberInfo")
public class MemberEntity { // User 테이블: 사용자 정보를 저장하는 테이블

 @Id
 private String email;
 
 @Column(nullable = false , name = "password")
 private String password;
 
 @Column(nullable = false , name = "name")
 private String name;
 
 @Column(nullable = false , name = "nickName" ,  unique = true )
 private String nickName;
  
 @Column(nullable = false , name = "birth")
 private String birth;
 
 @Column(nullable = false , name = "sex")
 private String sex;
 
 @Column(nullable = false , name = "joinDate")
 private String joinDate;
 
 @Column(nullable = false , name = "loginDate")
 private String loginDate;
 
 @Column(nullable = false , name = "authority")
 private String authority;
 
 @Column(nullable = false , name = "profile")
 private String profile;
 
 @Column(nullable = false , name = "grade")
 private String grade;
 
 @Column(nullable = false , name = "fcmToken")
 private String fcmToken;
 
 @Column(name = "refreshToken")
 private String refreshToken;
 

@Builder
public MemberEntity(String email, String password, String name, String nickName, String birth, String sex,
		String joinDate, String loginDate , String authority , String profile , String fcmToken , String grade) {
	super();
	this.email = email;
	this.password = password;
	this.name = name;
	this.nickName = nickName;		
	this.birth = birth;
	this.sex = sex;
	this.joinDate = joinDate;
	this.loginDate = loginDate;
	this.authority = authority;
	this.profile = profile;
	this.fcmToken = fcmToken;
	this.grade = grade;
	
}
}
