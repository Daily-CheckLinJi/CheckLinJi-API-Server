package com.upside.api.repository;




import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.HashTagEntity;

public interface HashTagRepository extends JpaRepository<HashTagEntity, Long> {
		 		
	Optional<HashTagEntity> findByTagName (String hashTagName);
	
	void deleteByTagName (String hashTagName);
}

