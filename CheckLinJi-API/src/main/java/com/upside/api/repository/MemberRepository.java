package com.upside.api.repository;




import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {
		 
	Page<MemberEntity> findAll(Pageable pageable);		
		
	Optional<MemberEntity> findByNickName (String nickName);
	
	long count();
}

