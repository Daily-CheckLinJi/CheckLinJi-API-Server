package com.upside.api.scheduler;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class RankingTopScheduler {

	private final RankingTopInsert rankingTopInsert ;
	
	  @Scheduled(cron = "0 */10 * * * *") // 매 10분마다 동작
	  public void rankingTopInsert() {	  	
		   rankingTopInsert.rankingTopInsert();
	  	
	  } 
}
