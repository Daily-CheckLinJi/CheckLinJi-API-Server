package com.upside.api.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.upside.api.dto.RankingDto;
import com.upside.api.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingTopInsert  {
		
	private final MemberMapper memberMapper ;
    
                
    
    
	  public void rankingTopInsert() {
		  	
		   log.info("랭킹 Top 3 스케쥴러 시작 ------> " + LocalDate.now());
	  	
		   try {
		  		
			   ArrayList<Map<String, Object>> missionRankingTop = memberMapper.missionRankingTop();
			   	
			   ArrayList<RankingDto> rankingTopList = new ArrayList<RankingDto>();
			   
			   for(int i = 0; i < missionRankingTop.size(); i++) { 
				   RankingDto rank = new RankingDto();
				   double ranking = (double) missionRankingTop.get(i).get("ranking");
				   rank.setRank((int)ranking);
				   rank.setEmail((String) missionRankingTop.get(i).get("email"));
				   rank.setUpdateDate(LocalDateTime.now().toString());
				   
				   rankingTopList.add(rank);
			   }
			   
			   memberMapper.deleteRankingTop();
			   memberMapper.insertRankingTop(rankingTopList);
		  	
			   log.info("랭킹 Top 3 스케쥴러 종료 ------> " + LocalDate.now());
			   
				} catch (Exception e) {
					log.error("랭킹 Top 3 스케쥴러 에러 ------> " + LocalDate.now() , e);					
				}
	  	
	   		} 
    
}
