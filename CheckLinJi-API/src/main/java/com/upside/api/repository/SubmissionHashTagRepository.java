package com.upside.api.repository;




import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.SubmissionHashTagEntity;

public interface SubmissionHashTagRepository extends JpaRepository<SubmissionHashTagEntity, Long> {
		 
//	void deleteByDate(String date);
//	
//	List<BestBookEntity> findByDate(String date);
}

