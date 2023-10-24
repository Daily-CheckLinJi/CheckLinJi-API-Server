package com.upside.api.repository;




import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.HashTagEntity;

public interface HashTagRepository extends JpaRepository<HashTagEntity, Long> {
	
	
	List<HashTagEntity> findAll();
	
	Optional<HashTagEntity> findByTagName (String hashTagName);
	
//	Optional<HashTagEntity> findById (String hashTagName);
	
	void deleteByTagName (String hashTagName);
}

