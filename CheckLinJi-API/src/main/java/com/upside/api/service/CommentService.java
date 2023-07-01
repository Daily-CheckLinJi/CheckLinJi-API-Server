package com.upside.api.service;



import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.upside.api.dto.CommentDto;
import com.upside.api.mapper.MemberMapper;
import com.upside.api.mapper.UserCommentMapper;
import com.upside.api.repository.MemberRepository;
import com.upside.api.util.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RequiredArgsConstructor
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@Service
public class CommentService {
		
	
	private final UserCommentMapper userCommentMapper ;
	
	
	 	 
	
	
	
	
	 /**
	  * 유저 댓글 입력 
	  * @param memberDto
	  * @return
	  */
	public Map<String, String> userCommentSubmit (CommentDto commentDto) {
		
		log.info("유저 댓글 입력  ------> " + "Start");
		
		Map<String, String> result = new HashMap<String, String>();
						
        int insertYn = userCommentMapper.userCommentSubmit(commentDto);
        
        if (insertYn == 0) {
        	result.put("HttpStatus","1.00");		
    		result.put("Msg",Constants.FAIL);
    		return result ;
        }
        
        result.put("HttpStatus","2.00");		
		result.put("Msg",Constants.SUCCESS);
		
		log.info("유저 댓글 입력  ------> " + Constants.SUCCESS);
		
	    return result ;			    		   
	}
	
	 /**
	  * 유저 댓글 수정 
	  * @param memberDto
	  * @return
	  */
	public Map<String, String> userCommentUpdate (CommentDto commentDto) {
		
		log.info("유저 댓글 수정  ------> " + "Start");
		
		Map<String, String> result = new HashMap<String, String>();
						
       int insertYn = userCommentMapper.userCommentUpdate(commentDto);
       
       if (insertYn == 0) {
       	result.put("HttpStatus","1.00");		
   		result.put("Msg",Constants.FAIL);
   		return result ;
       }
       
       result.put("HttpStatus","2.00");		
		result.put("Msg",Constants.SUCCESS);
		
		log.info("유저 댓글 수정  ------> " + Constants.SUCCESS);
		
	    return result ;			    		   
	}
		
		
	
	
}
