package com.upside.api.repository;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.upside.api.entity.LikeEntity;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
	
	List<LikeEntity> findByEmail(String email);
	
	
 /* 회원 탈퇴 시 유저 게시글에 좋아요를 삭제하는 쿼리
  * 테이블명과 컬럼명은 Entity에 설정한 컬럼명과 변수명이 같아야한다. */	
	@Transactional
	@Modifying
	@Query("DELETE FROM LikeEntity l WHERE l.challengeSubmissionId IN :challengeSubmissionId")
	void deleteMissionLikes(@Param("challengeSubmissionId") List<Long> challengeSubmissionId);
		
		


}

