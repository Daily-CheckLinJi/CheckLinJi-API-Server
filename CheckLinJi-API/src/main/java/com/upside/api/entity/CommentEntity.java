package com.upside.api.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate  // 변경된 필드만 적용
@DynamicInsert  // 변경된 필드만 적용
@Table(name = "Comment") 
public class CommentEntity { // 게시글 테이블

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long commentSeq; // 댓글 고유 식별자
 
 @Column(nullable = false)
 private Long challengeSubmissionId; // 댓글이 달린 게시글의 식별자
 
 @Column(nullable = false , name = "email")
 private String email; // 댓글 내용
 
 @Column(nullable = false , name = "content")
 private String content; // 댓글 내용
 
 @Column(nullable = false , name = "nickName")
 private String nickName; // 댓글 작성자의 식별자
 
 @Column(nullable = false , name = "registDate")
 private String registDate; // 댓글 등록 시각
 
 @Column(name = "updateDate")
 private String updateDate; // 댓글 최종 수정 시각
 
 @Column(name = "commentState")
 private String commentState; // 댓글 상태
 
 /**
  * 댓글 테이블에서 parent_id 컬럼은 대댓글이 있을 때,
  * 그 대댓글이 속한 부모 댓글의 식별자를 저장하는 역할을 합니다. 
  * 그러나, 대댓글이 아닌 일반 댓글의 경우 parent_id 컬럼에 저장할 부모 댓글이 없기 때문에,
  * 이 경우에는 parent_id 컬럼에 NULL 값을 저장합니다.
  * 즉, parent_id 컬럼의 값이 NULL이라는 것은 해당 댓글이 대댓글이 아닌 일반 댓글임을 나타냅니다.
  * 반면, parent_id 컬럼의 값이 NULL이 아니라면 해당 댓글은 대댓글이며, 그 값은 해당 대댓글이 속한 부모 댓글의 식별자를 나타냅니다.
  * 
  * 댓글의 parent_id 값은 NULL로 표시하고, 대댓글의 parent_id 값은 해당 대댓글의 부모 댓글의 식별자를 저장하면 된다.
  * 댓글 : parent_id = null | 대댓글 : parent_id = 댓글의 식별자. 
  */
 
 private Long parentId; // 대댓글일 경우, 부모 댓글의 식별자
 
 

@Builder
public CommentEntity(Long challengeSubmissionId, String content, String nickName, 
		Long parentId , String registDate , String updateDate , String commentState  
		) {
	super();
	this.challengeSubmissionId = challengeSubmissionId;
	this.content = content;
	this.nickName = nickName;
	this.parentId = parentId;
	this.registDate = registDate;
	this.updateDate = updateDate;
	this.commentState = commentState;
 }
}
