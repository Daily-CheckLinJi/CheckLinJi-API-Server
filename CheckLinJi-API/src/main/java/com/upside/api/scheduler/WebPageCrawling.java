package com.upside.api.scheduler;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class WebPageCrawling {
	
	@Value("${essay.best.book-today}")
	 private String essayBestBookToday;	 
	@Value("${essay.best.book-week}")
	 private String essayBestBookWeek;
	@Value("${essay.best.book-monthly}")
	 private String essayBestBookMonthly;	
	
	@Value("${self.dev.best.book-today}")
	 private String selfDevBestBookToday;	
	@Value("${self.dev.best.book-week}")
	 private String selfDevBestBookWeek;
	@Value("${self.dev.best.book-monthly}")
	 private String selfDevBestBookMonthly;	 
	
	@Value("${humanities.best.book-today}")
	 private String humanitiesBestBookToday;	
	@Value("${humanities.best.book-week}")
	 private String humanitiesBestBookWeek;
	@Value("${humanities.best.book-monthly}")
	 private String humanitiesBestBookMonthly;	 
	
	
    private final WebPageReader webPageReader;
      
    
//    @Scheduled(fixedDelay = 4000000)           
  
      
  @Scheduled(cron = "0 0 9 * * ?") // 매일 오전 9시에 현재 베스트 셀러 크롤링   
  public void readWebPageToDay() {
  	
	   log.info("현재 베스트 셀러 Crawling 시작 ------> " + LocalDate.now());
  	
	   try {
	  		// 알라딘 현재 베스트셀러 url
	  		Boolean read_essay = webPageReader.readWebPageToDay(essayBestBookToday,"essay");
	  		Boolean read_selfDev = webPageReader.readWebPageToDay(selfDevBestBookToday,"selfDev");
	  		Boolean read_humanities = webPageReader.readWebPageToDay(humanitiesBestBookToday,"humanities");
	  		
	  		if(read_essay && read_selfDev && read_humanities) {
	  			log.info("현재 베스트 셀러 Crawling 성공 ------> " + LocalDate.now());  			
	  		}else {
	  			String result = "";
	  			if(!read_essay) {
	  				result += "에세이 |";
	  			}
	  			
	  			if(!read_selfDev) {
	  				result += " 자기계발 |";
	  			}
	  			
	  			if(!read_humanities) {
	  				result += " 인문학 |";
	  			}
	  				  			
	  			log.error("현재 "+result+" Crawling 실패 ------> " + LocalDate.now());  	
	  		}
			} catch (Exception e) {
				log.error("현재 베스트 셀러 Crawling 에러 ------> " + LocalDate.now());
				e.printStackTrace();
			}
  	
   		} 
       
  @Scheduled(cron = "0 0 9 * * ?") // 매일 오전 9시에 이번주 베스트 셀러 크롤링  
  public void readWebPageWeek() {
  	
	  	log.info("이번주 베스트 셀러 Crawling 시작 -------> " + LocalDate.now());
	  	
	  	try {
	  		
	  		// 알라딘 이번주 베스트셀러 url
	  		Boolean read_essay = webPageReader.readWebPageWeek(essayBestBookWeek,"essay");
	  		Boolean read_selfDev = webPageReader.readWebPageWeek(selfDevBestBookWeek,"selfDev");
	  		Boolean read_humanities = webPageReader.readWebPageWeek(humanitiesBestBookWeek,"humanities");
	  		
	  		if(read_essay && read_selfDev && read_humanities) {
	  			log.info("현재 베스트 셀러 Crawling 성공 ------> " + LocalDate.now());  			
	  		}else {
	  			String result = "";
	  			if(!read_essay) {
	  				result += "에세이 |";
	  			}
	  			
	  			if(!read_selfDev) {
	  				result += " 자기계발 |";
	  			}
	  			
	  			if(!read_humanities) {
	  				result += " 인문학 |";
	  			}
	  				  			
	  			log.error("현재 "+result+" Crawling 실패 ------> " + LocalDate.now());  	
	  		}
	  		
	  		
			} catch (Exception e) {
				log.info("이번주 베스트 셀러 Crawling 에러 ------> " + LocalDate.now());
				e.printStackTrace();
			}
	  	
	  } 
  


  @Scheduled(cron = "0 0 9 * * ?") // 매일 오전 9시에 이번달 베스트 셀러 크롤링  
  public void readWebPageMonthly() {
  	
  	log.info("이번달 베스트 셀러 Crawling 시작 ------> " + LocalDate.now());
  	
  	try {
  		     		    		
	  		// 알라딘 전날 베스트셀러 url
	  		Boolean read_essay = webPageReader.readWebPageMonthly(essayBestBookMonthly,"essay");
	  		Boolean read_selfDev = webPageReader.readWebPageMonthly(selfDevBestBookMonthly,"selfDev");
	  		Boolean read_humanities = webPageReader.readWebPageMonthly(humanitiesBestBookMonthly,"humanities");
	  		
	  		if(read_essay && read_selfDev && read_humanities) {
	  			log.info("현재 베스트 셀러 Crawling 성공 ------> " + LocalDate.now());  			
	  		}else {
	  			String result = "";
	  			if(!read_essay) {
	  				result += "에세이 |";
	  			}
	  			
	  			if(!read_selfDev) {
	  				result += " 자기계발 |";
	  			}
	  			
	  			if(!read_humanities) {
	  				result += " 인문학 |";
	  			}
	  				  			
	  			log.error("현재 "+result+" Crawling 실패 ------> " + LocalDate.now());  	
	  		}
  		
		} catch (Exception e) {
			log.info("이번달 베스트 셀러 Crawling 에러 ------> " + LocalDate.now());
			e.printStackTrace();
		}
  	
  }  
  
//	  @PostConstruct
	  public void initialTask() throws Exception {
		  
	  		// 알라딘 현재 베스트셀러 url
	  		webPageReader.readWebPageToDay(essayBestBookToday,"essay");
	  		webPageReader.readWebPageToDay(essayBestBookToday,"selfDev");
	  		webPageReader.readWebPageToDay(essayBestBookToday,"humanities");
	  		Thread.sleep(3000);
	  			  		
	  		// 알라딘 이번주 베스트셀러 url
	  		webPageReader.readWebPageWeek(essayBestBookWeek,"essay");
	  		webPageReader.readWebPageWeek(selfDevBestBookWeek,"selfDev");
	  		webPageReader.readWebPageWeek(humanitiesBestBookWeek,"humanities");
	  		Thread.sleep(3000);
		  
	  		// 알라딘 이번달 베스트셀러 url	  	 
	  		webPageReader.readWebPageMonthly(essayBestBookMonthly,"essay");
	  		webPageReader.readWebPageMonthly(selfDevBestBookMonthly,"selfDev");
	  		webPageReader.readWebPageMonthly(humanitiesBestBookMonthly,"humanities");
	  		Thread.sleep(3000);
	  		
	  }
    
   
}
