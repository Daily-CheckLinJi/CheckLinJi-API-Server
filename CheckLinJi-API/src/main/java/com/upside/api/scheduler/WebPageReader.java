package com.upside.api.scheduler;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.upside.api.dto.BestBookDto;
import com.upside.api.entity.BestBookEntity;
import com.upside.api.repository.BestBookRepository;
import com.upside.api.service.FileService;
import com.upside.api.service.MemberService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebPageReader  {
		
    private final BestBookRepository bookRepository;
    private final FileService fileService;
    
    
    /**
     * 전날 베스트셀러 Top 10 
     * @param url
     * @return
     * @throws Exception
     */
    @Transactional
	public Boolean readWebPageYesterDay(String url) throws Exception {
		
		 // 저장 성공유무
		 boolean insertYN = false;
		 
		 // 반복횟수
		 Integer seq = 1 ;
		 Integer image_seq = 1 ;
		
        // Jsoup을 사용하여 URL에 접속하고 웹 페이지를 파싱한다.
        Document doc = Jsoup.connect(url).get();
        List<BestBookEntity> list = new ArrayList<>();
        
        Elements items = doc.select("li");                               
        // li 갯수만큼 반복하면서 .bo3 옆 b 태그 값 가져오기(책이름)
        for (Element item : items) {       	
            String bookName = item.select(".bo3 > b").text();
            
            // .bo3 옆 b 태그 값이 공백이 아니며 베스트 셀러 상위 10개 가져오기
            if(!bookName.equals("") && seq <= 10) {            
            	BestBookEntity bookDto = BestBookEntity.builder()
	        			.name(bookName)
	        			.date("yesterDay")
	        			.rank(seq)
	        			.updateDate(LocalDate.now())
	        			.build();           	
                list.add(bookDto);
                seq++;    
            }   
            
        }                             
                        
        // 이미지 크롤링으로 받아오기       
        Elements imgElements = doc.select("img.front_cover");
        int count = 0;
        for (Element imgElement : imgElements) {
            String imageUrl = imgElement.attr("src");
            
            System.out.println("imageURL : " + imageUrl);
            // 이미지 URL로 다운받아 디코딩한 값 
            byte[] image = fileService.ImageUrlDownload(imageUrl);
                       
            list.get(count).setImage(image);
            count++;
            if (count >= 10) {
                break;
            }
        }

        
        // 전날 베스트셀러 Top 10 출력
        for (int i = 0; i < list.size(); i++) {
        	int rank = i+1;
        	log.info("전날 베스트셀러 " +rank+ "위 " + list.get(i).getName());            
        }  
        
        // 가져온 데이터가 10개가 맞으면 기존 데이터 삭제 후 인서트
        if(list.size() == 10) {
        	bookRepository.deleteByDate("yesterDay");
        	List<BestBookEntity> result = bookRepository.saveAll(list);
        	
        	if(result.size()==10) {
        		insertYN = true;
        	} else {
        		insertYN = false;
        	}
        }
              
        // 데이터가 저장된 배열을 반환한다.
        return insertYN;
    }
    
    
    
    
    
    /**
     * 오늘 베스트셀러 Top 10 
     * @param url
     * @return
     * @throws Exception
     */
    @Transactional
	public Boolean readWebPageToDay(String url) throws Exception {
		
		 // 저장 성공유무
		 boolean insertYN = false;
		 
		 // 반복횟수
		 Integer seq = 1 ;
		
        // Jsoup을 사용하여 URL에 접속하고 웹 페이지를 파싱한다.
        Document doc = Jsoup.connect(url).get();
              
        Elements items = doc.select("li");
                
        List<BestBookEntity> list = new ArrayList<>();
        
        // li 갯수만큼 반복하면서 .bo3 옆 b 태그 값 가져오기(책이름)
        for (Element item : items) {       	
            String bookName = item.select(".bo3 > b").text();
            
            // .bo3 옆 b 태그 값이 공백이 아니며 베스트 셀러 상위 10개 가져오기
            if(!bookName.equals("") && seq <= 10) {            
            	BestBookEntity bookDto = BestBookEntity.builder()
	        			.name(bookName)
	        			.date("toDay")
	        			.rank(seq)
	        			.updateDate(LocalDate.now())
	        			.build();           	
                list.add(bookDto);
                seq++;    
            }   
            
        }                             
        
        // 이미지 크롤링으로 받아오기       
        Elements imgElements = doc.select("img.i_cover");
        int count = 0;
        for (Element imgElement : imgElements) {
            String imageUrl = imgElement.attr("src");
            System.out.println("imageURL : " + imageUrl);
            // 이미지 URL로 다운받아 디코딩한 값 
            byte[] image = fileService.ImageUrlDownload(imageUrl);
                       
            list.get(count).setImage(image);
            count++;
            if (count >= 10) {
                break;
            }
        }
        
        
        
        // 전날 베스트셀러 Top 10 출력
        for (int i = 0; i < list.size(); i++) {
        	int rank = i+1;            
            log.info("오늘 베스트셀러 " +rank+ "위 " + list.get(i).getName());
        }  
        
        // 가져온 데이터가 10개가 맞으면 기존 데이터 삭제 후 인서트
        if(list.size() == 10) {
        	bookRepository.deleteByDate("toDay");
        	List<BestBookEntity> result = bookRepository.saveAll(list);
        	
        	if(result.size()==10) {
        		insertYN = true;
        	} else {
        		insertYN = false;
        	}
        }
              
        
        return insertYN;
    }
    
    
    /**
     * 이번주 베스트셀러 Top 10 
     * @param url
     * @return
     * @throws Exception
     */
    @Transactional
	public Boolean readWebPageWeek(String url) throws Exception {
		
		 // 저장 성공유무
		 boolean insertYN = false;
		 
		 // 반복횟수
		 Integer seq = 1 ; 
		
        // Jsoup을 사용하여 URL에 접속하고 웹 페이지를 파싱한다.
        Document doc = Jsoup.connect(url).get();
              
        Elements items = doc.select("li");
                
        List<BestBookEntity> list = new ArrayList<>();
        
        // li 갯수만큼 반복하면서 .bo3 옆 b 태그 값 가져오기(책이름)
        for (Element item : items) {       	
            String bookName = item.select(".bo3 > b").text();
            
            // .bo3 옆 b 태그 값이 공백이 아니며 베스트 셀러 상위 10개 가져오기
            if(!bookName.equals("") && seq <= 10) {            
            	BestBookEntity bookDto = BestBookEntity.builder()
	        			.name(bookName)
	        			.date("week")
	        			.rank(seq)
	        			.updateDate(LocalDate.now())
	        			.build();           	
                list.add(bookDto);
                seq++;    
            }   
            
        }                             
        
        
        // 이미지 크롤링으로 받아오기       
        Elements imgElements = doc.select("img.front_cover");
        int count = 0;
        for (Element imgElement : imgElements) {
            String imageUrl = imgElement.attr("src");
            
            System.out.println("imageURL : " + imageUrl);
            // 이미지 URL로 다운받아 디코딩한 값 
            byte[] image = fileService.ImageUrlDownload(imageUrl);
                       
            list.get(count).setImage(image);
            count++;
            if (count >= 10) {
                break;
            }
        }
        
        // 전날 베스트셀러 Top 10 출력
        for (int i = 0; i < list.size(); i++) {
        	int rank = i+1;            
            log.info("이번주 베스트셀러 " +rank+ "위 " + list.get(i).getName());
        }  
        
        // 가져온 데이터가 10개가 맞으면 기존 데이터 삭제 후 인서트
        if(list.size() == 10) {
        	bookRepository.deleteByDate("week");
        	List<BestBookEntity> result = bookRepository.saveAll(list);
        	
        	if(result.size()==10) {
        		insertYN = true;
        	} else {
        		insertYN = false;
        	}
        }
              
        
        return insertYN;
    }
    
}
