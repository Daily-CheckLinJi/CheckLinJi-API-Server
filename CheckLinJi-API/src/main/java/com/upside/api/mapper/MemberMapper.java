package com.upside.api.mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.upside.api.dto.ChallengeSubmissionDto;
import com.upside.api.dto.MemberDto;
import com.upside.api.dto.RankingDto;

@Mapper
public interface MemberMapper {
				
	
	/**
	 * 내 미션 달성 횟수 , 참가자 평균
	 * @param data
	 * @return
	 */
	Map<String,Object> missionCompletedCnt (Map<String, String> data);
	
	/**
	 * 내 미션 달성 횟수 (전체)
	 * @param data
	 * @return
	 */
	int missionCompletedCntAll (String data);	
	
	/**
	 * 내 미션 수정
	 * @param data
	 * @return
	 */
	int missionUpdate (ChallengeSubmissionDto challengeSubmissionDto);
	
	/**
	 * 내 미션 수정
	 * @param data
	 * @return
	 */
	int deleteTag (ChallengeSubmissionDto challengeSubmissionDto);
	
	
	/**
	 * 실시간 TOP3 랭킹
	 * @param data
	 * @return
	 */
	ArrayList<Map<String, Object>> missionRankingTop ();
			
	/**
	 * 실시간 본인 랭킹
	 * @param data
	 * @return
	 */
	HashMap<String,String> missionRankingOwn (Map<String, String> data);
	
	/**
	 * 실시간 본인 랭킹
	 * @param data
	 * @return
	 */
	HashMap<String,String> missionRankingOwnInfo (Map<String, String> data);
	
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
	HashMap<String,Object> missionAuthInfo (ChallengeSubmissionDto challengeSubmissionDto);
	
	/**
	 * 본인 미션 상세보기 댓글
	 * @param data
	 * @return
	 */
	ArrayList<Map<String, Object>> missionComment (ChallengeSubmissionDto challengeSubmissionDto);
	
	/**
	 * 본인 미션 상세보기 좋아요
	 * @param data
	 * @return
	 */
	ArrayList<Map<String, Object>> missionLikes (ChallengeSubmissionDto challengeSubmissionDto);
	
	/**
	 * 본인 미션 상세정보 해쉬태그
	 * @param data
	 * @return
	 */
	ArrayList<Map<String, Object>> missionHashTag (ChallengeSubmissionDto challengeSubmissionDto);
	
	/**
	 * 본인 미션 삭제
	 * @param data
	 * @return
	 */
	int missionDeleteComment (ChallengeSubmissionDto challengeSubmissionDto);
	int missionDeleteLikes (ChallengeSubmissionDto challengeSubmissionDto);
	int missionDeleteHashTag (ChallengeSubmissionDto challengeSubmissionDto);
	int missionDeleteSubmission (ChallengeSubmissionDto challengeSubmissionDto);
		
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
	
	/**
	 * 서비스 이용 날짜
	 * @param data
	 * @return
	 */
	int joinDate (String userEmail);
	
	/**
	 * 총 사용자 수 
	 * @param data
	 * @return
	 */
	int findMemCnt ();
	
	/**
	 * 오늘 미션 성공 유무
	 * @param data
	 * @return
	 */
	int missionYn(String email);
	
	/**
	 * 실시간 본인 랭킹
	 * @param data
	 * @return
	 */
	HashMap<String,String> OwnRanking (Map<String, String> data);
	
	/**
	 * 미션 랭킹 Top 3 인지 확인
	 * @return
	 */
	String isTopRank (Map<String, String> data);
	
	/**
	 * 미션 랭킹 Top 3 업데이트
	 * @param data
	 * @return
	 */
	int insertRankingTop ();
	
	/**
	 * 미션 랭킹 Top 3 삭제
	 * @return
	 */
	int deleteRankingTop ();
	
}
