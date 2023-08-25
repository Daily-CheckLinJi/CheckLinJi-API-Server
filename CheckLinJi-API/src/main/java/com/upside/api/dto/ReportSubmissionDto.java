package com.upside.api.dto;

import lombok.Data;

@Data
public class ReportSubmissionDto { //  게시글 신고 테이블

	 private Long ReportSubmissionSeq; // 게시글 신고 고유 시퀀스
	 private Long challengeSubmissionId; // 신고할 게시글
	 private String email; // 신고한 유저 이메일
 	 private String reportMsg; // 신고 메시지
 	 private String reportDate; // 신고 일시


}
