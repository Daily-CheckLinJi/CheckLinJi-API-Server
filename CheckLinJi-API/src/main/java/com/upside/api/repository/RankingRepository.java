package com.upside.api.repository;




import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.RankingEntity;

public interface RankingRepository extends JpaRepository<RankingEntity, Integer> {
	
	List<RankingEntity> findByEmail(String email);
	
	List<RankingEntity> findByNickName(String nickName);
		

}

