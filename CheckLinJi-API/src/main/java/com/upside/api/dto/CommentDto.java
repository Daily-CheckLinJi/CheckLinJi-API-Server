package com.upside.api.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class CommentDto { // 게시글 테이블


 private Long commentSeq; // 댓글 고유 식별자
 
 private Long challengeSubmissionId ; // 댓글이 달린 게시글의 식별자
 
 private String content; // 댓글 내용
 
 private String nickName; // 댓글 작성자의 식별자
  
 private LocalDate updateDate; // 댓글 최종 수정 시각
 
 
 private String parentId; // 대댓글일 경우, 부모 댓글의 식별자
 
 


}
