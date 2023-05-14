package com.upside.api.scheduler;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
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
	
	@Value("${best.book-today}")
	 private String bestBookToday;
	
	@Value("${best.book-yesterday}")
	 private String bestBookYesterDay;
	 
	@Value("${best.book-week}")
	 private String bestBookWeek;
	
	
    private final WebPageReader webPageReader;
      
    
//    @Scheduled(fixedDelay = 4000000)           
  
      
  @Scheduled(cron = "0 0 9 * * ?") // 매일 오전 9시에 현재 베스트 셀러 크롤링   
  public void readWebPageToDay() {
  	
	   log.info("현재 베스트 셀러 Crawling 시작 ------> " + LocalDate.now());
  	
	   try {
	  		// 알라딘 전날 베스트셀러 url
	  		Boolean read = webPageReader.readWebPageToDay(bestBookToday); 
	  		
	  		if(read) {
	  			log.info("현재 베스트 셀러 Crawling 성공 ------> " + LocalDate.now());  			
	  		}else {
	  			log.info("현재 베스트 셀러 Crawling 실패 ------> " + LocalDate.now());  	
	  		}
			} catch (Exception e) {
				log.info("현재 베스트 셀러 Crawling 에러 ------> " + LocalDate.now());
				e.printStackTrace();
			}
  	
   		} 
    
    
    @Scheduled(cron = "0 0 1 * * ?") // 매일 새벽 1시에 전날 베스트 셀러 크롤링
    public void readWebPageYesterDay() {
    	
    	log.info("전날 베스트 셀러 Crawling 시작 ------> " + LocalDate.now());
    	
    	try {
    		// 알라딘 전날 베스트셀러 url
    		Boolean read = webPageReader.readWebPageYesterDay(bestBookYesterDay); 
    		
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
   
  @Scheduled(cron = "0 0 9 ? * MON") // 매일 새벽 1시에 전날 베스트 셀러 크롤링   
  public void readWebPageWeek() {
  	
	  	log.info("이번주 베스트 셀러 Crawling 시작 ------> " + LocalDate.now());
	  	
	  	try {
	  		// 알라딘 전날 베스트셀러 url
	  		Boolean read = webPageReader.readWebPageWeek(bestBookWeek); 
	  		
	  		if(read) {
	  			log.info("이번주 베스트 셀러 Crawling 성공 ------> " + LocalDate.now());  			
	  		}else {
	  			log.info("이번주 베스트 셀러 Crawling 실패 ------> " + LocalDate.now());  	
	  		}
			} catch (Exception e) {
				log.info("이번주 베스트 셀러 Crawling 에러 ------> " + LocalDate.now());
				e.printStackTrace();
			}
	  	
	  } 
    
   
}
