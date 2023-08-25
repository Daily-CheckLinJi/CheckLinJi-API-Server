package com.upside.api.repository;




import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.ReportCommentEntity;

public interface ReportCommentRepository extends JpaRepository<ReportCommentEntity, String> {
		
	Long countByCommentSeq(Long commentSeq); // 게시글 누적 신고횟수 확인
	
	Long countByCommentSeqAndEmail(Long commentSeq, String email); // 같은 게시글을 신고한 이력이 있는지 확인

}

