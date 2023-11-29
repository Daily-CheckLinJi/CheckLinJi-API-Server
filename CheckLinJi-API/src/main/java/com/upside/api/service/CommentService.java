package com.upside.api.service;



import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.upside.api.dto.CommentDto;
import com.upside.api.entity.MemberEntity;
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
	private final MemberRepository memberRepository ;
	
	
	 	 
	
	
	
	
	 /**
	  * 유저 댓글 입력 
	  * @param memberDto
	  * @return
	  */
	public Map<String, String> userCommentSubmit (CommentDto commentDto) {
		
		log.info("유저 댓글 입력  ------> " + "Start");
		
		Map<String, String> result = new HashMap<String, String>();
		
		try {
			
			// 유저가 존재 하는지 확인
			Optional<MemberEntity> userExsist = memberRepository.findById(commentDto.getEmail());
			
			// 유저가 존재 하지않으면 에러처리
			if(!userExsist.isPresent()) {
				result.put("HttpStatus","1.00");		
	    		result.put("Msg",Constants.FAIL);
	    		log.info("유저 댓글 입력  ------> " + Constants.FAIL);
	    		return result;
			}
			
			commentDto.setNickName(userExsist.get().getNickName());
			
			// 유저 댓글 등록
	        int insertYn = userCommentMapper.userCommentSubmit(commentDto);
	        	        
	        if (insertYn != 0) {
	        	result.put("HttpStatus","2.00");		
	    		result.put("Msg",Constants.SUCCESS);
	    		log.info("유저 댓글 입력  ------> " + Constants.SUCCESS);
	        }else {        	
	    		result.put("HttpStatus","1.00");		
	    		result.put("Msg",Constants.FAIL);
	    		log.info("유저 댓글 입력  ------> " + Constants.FAIL);
	        }
                						
		} catch (Exception e) {
        	result.put("HttpStatus","1.00");		
    		result.put("Msg",Constants.SYSTEM_ERROR);
    		log.error("유저 댓글 입력  ------> " + Constants.SYSTEM_ERROR , e);
		}
		
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
		
		try {
					
			// 유저 댓글 수정
	        int insertYn = userCommentMapper.userCommentUpdate(commentDto);
	       
	        if (insertYn != 0) {
	        	result.put("HttpStatus","2.00");		
	     		result.put("Msg",Constants.SUCCESS);
	     		log.info("유저 댓글 수정  ------> " + Constants.SUCCESS);
	        }else {
	        	result.put("HttpStatus","1.00");		
	     		result.put("Msg",Constants.FAIL); 
	     		log.info("유저 댓글 수정  ------> " + Constants.FAIL);
	        }
		
		} catch (Exception e) {
	       	result.put("HttpStatus","1.00");		
    		result.put("Msg",Constants.SYSTEM_ERROR);
    		log.error("유저 댓글 수정  ------> " + Constants.SYSTEM_ERROR , e);
		}
		
	    return result ;			    		   
	}
		

	 /**
	  * 유저 댓글 삭제 
	  * @param memberDto
	  * @return
	  */
	public Map<String, String> userCommentDelete (CommentDto commentDto) {
		
		  log.info("유저 댓글 삭제  ------> " + "Start");
			
		  Map<String, String> result = new HashMap<String, String>();
			
		  try {
				  
			  // 유저 댓글 삭제
		      int insertYn = userCommentMapper.userCommentDelete(commentDto);
		      		      
		      if (insertYn != 0) {
		    	  result.put("HttpStatus","2.00");		
		 		  result.put("Msg",Constants.SUCCESS);
		 		  log.info("유저 댓글 삭제  ------> " + Constants.SUCCESS);
		      }else {
		    	  result.put("HttpStatus","1.00");		
		    	  result.put("Msg",Constants.FAIL);
		    	  log.info("유저 댓글 삭제  ------> " + Constants.FAIL);
		      }	             					 
			
		 } catch (Exception e) {
	      	result.put("HttpStatus","1.00");		
	  		result.put("Msg",Constants.SYSTEM_ERROR);
	  		log.error("유저 댓글 삭제  ------> " + Constants.SYSTEM_ERROR , e);
		 }		
	    return result ;			    		   
	}
	
	
	 /**
	  * 유저 좋아요 등록
	  * @param memberDto
	  * @return
	  */
	public Map<String, String> insertLike (CommentDto commentDto) {
		
	   log.info("유저 좋아요 등록  ------> " + "Start");
		
	   Map<String, String> result = new HashMap<String, String>();
	   
	   if(commentDto.getWriterEmail() == null || commentDto.getWriterEmail().equals("")) {
		 	result.put("HttpStatus","1.00");		
			result.put("Msg",Constants.NOT_EXIST_PARAMETER);
			return result;
	   }
	   
	   int insertYn = 0 ;
	   	   
	   try {		   
			   	   	
		   // 유저 좋아요 등록
		   insertYn = userCommentMapper.insertLike(commentDto);
		   
		   if (insertYn != 0) {
			   result.put("HttpStatus","2.00");		
			   result.put("Msg",Constants.SUCCESS); 
			   log.info("유저 좋아요 등록  ------> " + Constants.SUCCESS);
		   }else{
			   result.put("HttpStatus","1.00");		
			   result.put("Msg",Constants.FAIL);
			   log.info("유저 좋아요 등록  ------> " + Constants.FAIL);
		   }
              			    		
	   } catch (Exception e) {
		result.put("HttpStatus","1.00");		
   		result.put("Msg",Constants.SYSTEM_ERROR);
   		log.error("유저 좋아요 등록 ------> " + Constants.SYSTEM_ERROR , e);
	   }
	    return result ;			    		   
	}
	
	 /**
	  * 유저 좋아요 취소
	  * @param memberDto
	  * @return
	  */
	public Map<String, String> deleteLike (CommentDto commentDto) {
		
	   log.info("유저 좋아요 취소  ------> " + "Start");
		
	   Map<String, String> result = new HashMap<String, String>();
	   
	   int insertYn = 0 ;
	   
	   try {
			 
		   // 유저 좋아요 취소
		   insertYn = userCommentMapper.deleteLike(commentDto);
		   
		   if (insertYn != 0) {
			   result.put("HttpStatus","2.00");		
			   result.put("Msg",Constants.SUCCESS);
			   log.info("유저 좋아요 취소 ------> " + Constants.SUCCESS);
		   }else{
			   result.put("HttpStatus","1.00");		
			   result.put("Msg",Constants.FAIL);
			   log.info("유저 좋아요 취소 ------> " + Constants.FAIL);
		   }
             			    		
	   } catch (Exception e) {
		result.put("HttpStatus","1.00");		
  		result.put("Msg",Constants.SYSTEM_ERROR);
  		log.error("유저 좋아요 취소 ------> " + Constants.SYSTEM_ERROR , e);
	   }
	    return result ;			    		   
	}
}
