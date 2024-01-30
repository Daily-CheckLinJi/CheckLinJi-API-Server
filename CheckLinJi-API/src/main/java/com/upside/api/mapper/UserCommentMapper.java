package com.upside.api.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.upside.api.dto.CommentDto;

@Mapper
public interface UserCommentMapper {

	
	/**
	 * 유저 코멘트 등록
	 * @param memberDto
	 * @return
	 */
	Long userCommentSubmit (CommentDto commentDto);
	
	/**
	 * 유저 코멘트 수정
	 * @param memberDto
	 * @return
	 */
	int userCommentUpdate (CommentDto commentDto);
	
	/**
	 * 유저 댓글 삭제
	 * @param memberDto
	 * @return
	 */
	int userCommentDel (CommentDto commentDto);
	
	/**
	 * 유저 대댓글 삭제
	 * @param memberDto
	 * @return
	 */
	int userParentCommentDel (CommentDto commentDto);
	
	/**
	 * 유저 댓글 신고여부 상태 확인 
	 * @param memberDto
	 * @return
	 */
	int userCommentReportState (CommentDto commentDto);
	
	
	/**
	 * 유저 좋아요 등록
	 * @param memberDto
	 * @return
	 */
	int insertLike (CommentDto commentDto);	
	
	/**
	 * 유저 좋아요 취소
	 * @param memberDto
	 * @return
	 */
	int deleteLike (CommentDto commentDto);	
	
	/**
	 * 유저 부모 댓글 이메일 찾기
	 * @param commentDto
	 * @return
	 */
	String findParentComment (CommentDto commentDto);	
	
	/**
	 * 유저 댓글 찾기
	 * @param commentDto
	 * @return
	 */
	int findCommentSeq (CommentDto commentDto);	
		
}
