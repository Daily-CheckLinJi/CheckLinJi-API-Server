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

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebPageReader  {
		
    private final BestBookRepository bookRepository;   
    
    
    // 웹 페이지에서 데이터를 읽어와서 배열에 저장하는 메서드
    @Transactional
	public Boolean readWebPageYesterDay(String url) throws Exception {
		
		 // 저장 성공유무
		 boolean insertYN = false;
		 
		 // 반복횟수
		 int seq = 1 ; 
		
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
	        			.date(LocalDate.now().minusDays(1))
	        			.build();           	
                list.add(bookDto);
                seq++;    
            }   
            
        }                             
        
        for (int i = 0; i < list.size(); i++) {
        	int rank = i+1;
            System.out.println("저장될 베스트셀러 " +rank+ "위" + list.get(i));
        }  
              
        if(list.size() == 10) {
        	bookRepository.deleteByDate(LocalDate.now().minusDays(1));
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
}
