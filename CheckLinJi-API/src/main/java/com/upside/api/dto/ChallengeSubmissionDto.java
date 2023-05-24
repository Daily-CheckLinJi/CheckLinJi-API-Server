package com.upside.api.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class ChallengeSubmissionDto { // ChallengeSubmission 테이블: 사용자가 첼린지에 대한 제출 정보를 저장하는 테이블

  
 private String challengeName;
 private String email;
 private LocalDate submissionTime;
 private String submissionTitle;
 private String  submissionText; 
 private String  nickName; 
 private String  submissionImageRoute; 
 private String year ; 
 private String month ;
 private String day ;
  
 
}
