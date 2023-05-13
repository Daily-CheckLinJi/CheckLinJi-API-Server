package com.upside.api.scheduler;

import java.time.LocalDate;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class WebPageCrawling {
	
      
    private final WebPageReader webPageReader;
       
//    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 전날 베스트 셀러 크롤링
     @Scheduled(fixedRate = 40000) // 테스트용 40초마다 실행
    public void WebCrawlingScheduler() {
    	
    	log.info("전날 베스트 셀러 Crawling 시작 ------> " + LocalDate.now());
    	
    	try {
    		// 알라딘 전날 베스트셀러 url
    		Boolean read = webPageReader.readWebPageYesterDay("https://www.aladin.co.kr/shop/common/wbest.aspx?BranchType=1&BestType=DailyBest"); 
    		
    		if(read) {
    			log.info("전날 베스트 셀러 Crawling 성공 ------> " + LocalDate.now());  			
    		}else {
    			log.info("전날 베스트 셀러 Crawling 실패 ------> " + LocalDate.now());  	
    		}
		} catch (Exception e) {
			log.info("전날 베스트 셀러 Crawling 에러 ------> " + LocalDate.now());
			e.printStackTrace();
		}
    	
    }
    
    
   
    
   
}
