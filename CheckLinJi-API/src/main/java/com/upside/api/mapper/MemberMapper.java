package com.upside.api.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.upside.api.dto.MemberDto;

@Mapper
public interface MemberMapper {
				
	
	/**
	 * 내 미션 달성 횟수 , 참가자 평균
	 * @param data
	 * @return
	 */
	HashMap<String,String> missionCompletedCnt (Map<String, String> data);
	
	/**
	 * 실시간 TOP3 랭킹
	 * @param data
	 * @return
	 */
	ArrayList<Map<String, Object>> missionRankingTop (Map<String, String> data);
			
	/**
	 * 실시간 본인 랭킹
	 * @param data
	 * @return
	 */
	HashMap<String,String> missionRankingOwn (Map<String, String> data);
	
	/**
	 * 본인 미션 달력 
	 * @param data
	 * @return
	 */
	ArrayList<Map<String, Object>> missionCalendarOwn (Map<String, String> data);
	
	/**
	 * 본인 미션 상세보기
	 * @param data
	 * @return
	 */
	HashMap<String,String> missionAuthInfo (Map<String, String> data);
	
	/**
	 * 본인 미션 삭제
	 * @param data
	 * @return
	 */
	int missionAuthDelete (Map<String, String> data);
	
	/**
	 * 본인 미션 삭제
	 * @param param
	 * @return
	 */
	HashMap<String, Object> memberDelete (HashMap<String, String> param);
	
	
	/**
	 * 본인 누적미션 횟수
	 * @param email
	 * @return
	 */
	int missionCompletedSum (MemberDto memberDto);
	
	/**
	 * 등급 업데이트
	 * @param memberDto
	 * @return
	 */
	int updateGrade (MemberDto memberDto);
	
}
