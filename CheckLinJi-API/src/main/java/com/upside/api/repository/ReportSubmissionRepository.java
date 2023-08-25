package com.upside.api.repository;




import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.ReportSubmissionEntity;

public interface ReportSubmissionRepository extends JpaRepository<ReportSubmissionEntity, String> {
	
	Long countByChallengeSubmissionId(Long submissionId); // 게시글 누적 신고횟수 확인
	
	Long countByChallengeSubmissionIdAndEmail(Long submissionId, String email); // 같은 게시글을 신고한 이력이 있는지 확인

}

