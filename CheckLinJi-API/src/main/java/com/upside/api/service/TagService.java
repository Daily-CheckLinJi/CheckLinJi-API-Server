package com.upside.api.service;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
public class TagService {
 
	private final HashTagRepository hashTagRepository;
	
	
	

	
	/**
	 * 해쉬태그 목록
	 * @param hashTagDto
	 * @return
	 */
	public Map<String, Object> listTag (HashTagDto hashTagDto) {
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		log.info("해쉬태그 목록 ------> " + "Start");
		
		try {
		
		List<HashTagEntity> tagList = hashTagRepository.findAll();
		
		if(tagList.isEmpty()) {
			 result.put("HttpStatus","2.00");		
    		 result.put("Msg","태그가 존재하지 않습니다.");
    		 log.info("해쉬태그 목록 ------> " + "태그가 존재하지 않습니다.");
    		 return result ;
		}						
		 result.put("HttpStatus","2.00");		
		 result.put("tagList",tagList);		 
		log.info("해쉬태그 목록 ------> " + Constants.SUCCESS);
		
		} catch (Exception e) {
			log.error("해쉬태그 목록 ------> " + Constants.SYSTEM_ERROR , e);
			 result.put("HttpStatus","1.00");		
    		 result.put("Msg",Constants.SYSTEM_ERROR);
    		 return result ;
		}
						
		return result;
	}
	
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
			log.error("해쉬태그 추가 ------> " + Constants.SYSTEM_ERROR , e);
			 result.put("HttpStatus","1.00");		
    		 result.put("Msg",Constants.SYSTEM_ERROR);
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
			log.error("해쉬태그 삭제 ------> " + Constants.SYSTEM_ERROR , e);
			 result.put("HttpStatus","1.00");		
    		 result.put("Msg",Constants.SYSTEM_ERROR);
    		 return result ;
		}
		
		
		
		return result;
	}
		
}
