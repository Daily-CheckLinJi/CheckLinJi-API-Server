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
	int userCommentSubmit (CommentDto commentDto);
	
	/**
	 * 유저 코멘트 수정
	 * @param memberDto
	 * @return
	 */
	int userCommentUpdate (CommentDto commentDto);
	
	/**
	 * 유저 코멘트 삭제
	 * @param memberDto
	 * @return
	 */
	int userCommentDelete (CommentDto commentDto);
	
	
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
		
}
