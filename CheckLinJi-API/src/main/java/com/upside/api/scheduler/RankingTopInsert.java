package com.upside.api.scheduler;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import com.upside.api.mapper.MemberMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RankingTopInsert  {
		
	private final MemberMapper memberMapper ;
    
                
    
    
	  public void rankingTopInsert() {
		  	
		   log.info("랭킹 스케쥴러 시작 ------> " + LocalDate.now());
	  	
		   try {		  					 
			   
			   memberMapper.deleteRankingTop();
			   
			   int result = memberMapper.insertRankingTop();
		  	
			   log.info("랭킹 스케쥴러 종료 ------> " + result);
			   
				} catch (Exception e) {
					log.error("랭킹 스케쥴러 에러 ------> " + LocalDate.now() , e);					
				}
	  	
	   		} 
    
}
