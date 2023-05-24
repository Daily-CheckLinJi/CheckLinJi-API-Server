//package com.upside.api.repository;
//
//
//
//
//import java.time.LocalDate;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//
//import com.upside.api.entity.FileUploadEntity;
//import com.upside.api.entity.MemberEntity;
//
//public interface FileUploadRepository extends JpaRepository<FileUploadEntity, Long> {
//		 
//
//
//		
//	Optional<FileUploadEntity> findByMemberEntityAndUploadDate (MemberEntity memberEntity , LocalDate uploadDate);
//}
//
