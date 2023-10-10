package com.upside.api.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.dto.PageDto;

@Mapper
public interface ChallengeMapper {
				
	
	/**
	 * 첼린지 인증글 리스트
	 * @param data
	 * @return
	 */
	ArrayList<Map<String, Object>> viewChallengeList (PageDto pageDto);
	
	
	/**
	 * 첼린지 인증글 리스트
	 * @param data
	 * @return
	 */
	ArrayList<Map<String, Object>> viewCheckRinger (PageDto pageDto);
	
	
	/**
	 * 첼린지 인증글 리스트
	 * @param data
	 * @return
	 */
	int listTotalCnt (PageDto pageDto);
	
	/**
	 * 첼린지 인증글 상세페이지
	 * @param submissionDto
	 * @return
	 */
	HashMap<String,String> detail (ChallengeSubmissionDto submissionDto);
	
	/**
	 * 첼린지 인증글 상세페이지 좋아요 갯수 
	 * @param submissionDto
	 * @return
	 */
	int likesCount (ChallengeSubmissionDto submissionDto);
	
	/**
	 * 첼린지 인증글 상세페이지 댓글
	 * @param submissionDto
	 * @return
	 */
	ArrayList<Map<String, Object>> commentList (ChallengeSubmissionDto submissionDto);
	
	/**
	 * 본인 미션 달성 횟수 (월)
	 * @param data
	 * @return
	 */
	int missionCompletedCnt (Map<String, String> data);
	
	
	/**
	 * 본인 미션 달성 횟수 
	 * @param data
	 * @return
	 */
	int missionCompletedCntAll (Map<String, String> data);
}
