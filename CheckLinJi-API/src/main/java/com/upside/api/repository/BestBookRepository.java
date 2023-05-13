package com.upside.api.repository;




import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.BestBookEntity;

public interface BestBookRepository extends JpaRepository<BestBookEntity, Long> {
		 
	void deleteByDate(LocalDate date);
//	Optional<SayingEntity> findBysaySeq (Long saySeq);
}

