package com.upside.api.repository;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.LikeEntity;

public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
	
	List<LikeEntity> findByEmail(String email);
		
		


}

