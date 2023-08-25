package com.upside.api.repository;




import org.springframework.data.jpa.repository.JpaRepository;

import com.upside.api.entity.CommentEntity;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
		


}

