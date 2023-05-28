package com.upside.api.service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.tomcat.util.bcel.Const;
import org.springframework.stereotype.Service;

import com.upside.api.dto.HashTagDto;
import com.upside.api.entity.HashTagEntity;
import com.upside.api.repository.HashTagRepository;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {
 
	private final HashTagRepository hashTagRepository;
	
	
	
	
	/**
	 * 해쉬태그 추가
	 * @param hashTagDto
	 * @return
	 */
	public Map<String, String> addTag (HashTagDto hashTagDto) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		log.info("해쉬태그 추가 ------> " + "Start");
		
		try {
		
		Optional<HashTagEntity> tagExist = hashTagRepository.findByTagName(hashTagDto.getTagName());
		
		if(tagExist.isPresent()) {
			 result.put("HttpStatus","1.00");		
    		 result.put("Msg","이미 존재하는 태그입니다.");
    		 log.info("해쉬태그 추가 ------> " + "이미 존재하는 태그입니다.");
    		 return result ;
		}
		
		HashTagEntity hashTagEntity = HashTagEntity.builder()
															.tagName(hashTagDto.getTagName())
															.createDate(LocalDateTime.now())
															.build();
		
		hashTagRepository.save(hashTagEntity);
		
		
		 result.put("HttpStatus","2.00");		
		 result.put("Msg",Constants.SUCCESS);
		 result.put("addTag",hashTagDto.getTagName());
		log.info("해쉬태그 추가 ------> " + Constants.SUCCESS);
		
		} catch (Exception e) {
			log.info("해쉬태그 추가 ------> " + Constants.FAIL);
			 result.put("HttpStatus","1.00");		
    		 result.put("Msg",Constants.FAIL);
    		 return result ;
		}
		
		
		
		return result;
	}
	
	
	/**
	 * 해쉬태그 삭제
	 * @param hashTagDto
	 * @return
	 */
	public Map<String, String> deleteTag (HashTagDto hashTagDto) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		log.info("해쉬태그 삭제 ------> " + "Start");
		
		try {
		
		Optional<HashTagEntity> tagExist = hashTagRepository.findByTagName(hashTagDto.getTagName());
		
		if(!tagExist.isPresent()) {
			 result.put("HttpStatus","1.00");		
    		 result.put("Msg","존재 하지 않는 태그입니다.");
    		 log.info("해쉬태그 삭제 ------> " + "존재 하지 않는 태그입니다.");
    		 return result ;
		}
						
		hashTagRepository.deleteById(tagExist.get().getHashTagId());
		
		
		 result.put("HttpStatus","2.00");		
		 result.put("Msg",Constants.SUCCESS);
		 result.put("addTag",hashTagDto.getTagName());
		log.info("해쉬태그 삭제 ------> " + Constants.SUCCESS);
		
		} catch (Exception e) {
			log.info("해쉬태그 삭제 ------> " + Constants.FAIL);
			 result.put("HttpStatus","1.00");		
    		 result.put("Msg",Constants.FAIL);
    		 return result ;
		}
		
		
		
		return result;
	}
		
}
