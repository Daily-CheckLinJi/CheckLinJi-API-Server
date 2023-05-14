package com.upside.api.repository;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.BestBookEntity;

public interface BestBookRepository extends JpaRepository<BestBookEntity, Long> {
		 
	void deleteByDate(String date);
	
	List<BestBookEntity> findByDate(String date);
}

