package com.upside.api.mapper;

import java.util.ArrayList;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.upside.api.dto.PageDto;

@Mapper
public interface ChallengeMapper {
				
	
	/**
	 * 첼린지 인증글 리스트
	 * @param data
	 * @return
	 */
	ArrayList<Map<String, Object>> viewChallengeList (PageDto pageDto);
}
