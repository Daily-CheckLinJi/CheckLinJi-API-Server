package com.upside.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate  // 변경된 필드만 적용
@DynamicInsert  // 변경된 필드만 적용
@Table(name = "RankingTop")
public class RankingEntity { // User 테이블: 사용자 정보를 저장하는 테이블


 @Id
 @Column(name = "rank")
 private Integer rank;
 
 @Column(name = "email")
 private String email;
 
 @Column(name = "nickName")
 private String nickName;
 
 @Column(name = "userSeq")
 private String userSeq;
 
 @Column(name = "successCnt")
 private String successCnt;
 
 @Column(name = "joinDate")
 private String joinDate;
 
 @Column(name = "grade")
 private String grade;
 
 @Column(name = "profile")
 private String profile;
 
 @Column(name = "updateDate")
 private String updateDate;
 
 

}




