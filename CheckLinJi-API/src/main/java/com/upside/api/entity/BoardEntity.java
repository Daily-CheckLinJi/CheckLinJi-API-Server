//package com.upside.api.entity;
//
//import java.time.LocalDate;
//
//import org.hibernate.annotations.DynamicInsert;
//import org.hibernate.annotations.DynamicUpdate;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import jakarta.persistence.Table;
//import lombok.AccessLevel;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@DynamicUpdate  // 변경된 필드만 적용
//@DynamicInsert  // 변경된 필드만 적용
//@Table(name = "Board") 
//public class BoardEntity { // 게시글 테이블
//
// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// private Long boardId; // 게시글 고유 식별자
// 
// @Column(nullable = false , name = "title")
// private String title; // 게시글 제목
// 
// @Column(nullable = false , name = "content")
// private String content; // 게시글 내용
// 
// @ManyToOne(fetch = FetchType.LAZY) //  Member와 다대일 관계이므로 @ManyToOne이 됩니다.
// @JoinColumn(name = "nick_name") // 외래 키를 매핑할 때 사용합니다. name 속성에는 매핑 할 외래 키 이름을 지정합니다.
// private MemberEntity memberEntity; // 게시글 작성자 식별자
//  
// @Column(nullable = false , name = "createDate")
// private LocalDate createDate; // 게시글 생성 시각
// 
// @Column(nullable = false , name = "updateDate")
// private LocalDate updateDate; // 게시글 최종 수정 시각
// 
// 
// 
//
//@Builder
//public BoardEntity(String title, String content, MemberEntity memberEntity, LocalDate createDate, LocalDate updateDate
//		) {
//	super();
//	this.title = title;
//	this.content = content;
//	this.memberEntity = memberEntity;		
//	this.createDate = createDate;
//	this.updateDate = updateDate;		
// }
//}
