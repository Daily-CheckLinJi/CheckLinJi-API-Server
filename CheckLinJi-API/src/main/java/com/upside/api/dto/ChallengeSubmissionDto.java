package com.upside.api.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ChallengeSubmissionDto { // ChallengeSubmission 테이블: 사용자가 첼린지에 대한 제출 정보를 저장하는 테이블

 private int challengeSubmissionId;
 private String email;
 private String hashTag;
 private LocalDateTime submissionTime; 
 private String  submissionText; 
 private String  nickName;
 private String  submissionImageName;
 private String  submissionImageRoute; 
 private String year ; 
 private String month ;
 private String day ;
 
  
 
}
