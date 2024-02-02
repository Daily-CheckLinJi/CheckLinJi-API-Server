package com.upside.api.repository;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.upside.api.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
	
	List<CommentEntity> findByEmail(String email);
	
	List<CommentEntity> findByNickName(String nickName);
		
		
	/* 회원 탈퇴 시 댓글 및 하위 댓글 삭제하는 쿼리
	 * 테이블명과 컬럼명은 Entity에 설정한 컬럼명과 변수명이 같아야한다. */	   
    @Transactional
    @Modifying
    @Query("DELETE FROM CommentEntity c WHERE c.commentSeq IN :commentSeqs OR c.parentId IN :commentSeqs")
    void deleteCommentsAndChildren(@Param("commentSeqs") List<Long> commentSeqs);
    
	/* 게시글 삭제 시 댓글들을 삭제하는 쿼리
	 * 테이블명과 컬럼명은 Entity에 설정한 컬럼명과 변수명이 같아야한다. */       
    @Transactional
    @Modifying
    @Query("DELETE FROM CommentEntity c WHERE c.challengeSubmissionId IN :challengeSubmissionId")
    void deleteMissionComments(@Param("challengeSubmissionId") List<Long> challengeSubmissionId);



}

