package com.upside.api.service;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.upside.api.dto.CommentDto;
import com.upside.api.dto.NotificationRequestDto;
import com.upside.api.entity.ChallengeSubmissionEntity;
import com.upside.api.entity.MemberEntity;
import com.upside.api.entity.UserChallengeEntity;
import com.upside.api.mapper.UserCommentMapper;
import com.upside.api.repository.ChallengeSubmissionRepository;
import com.upside.api.repository.MemberRepository;
import com.upside.api.repository.UserChallengeRepository;
import com.upside.api.util.Constants;
import com.upside.api.util.Notification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RequiredArgsConstructor
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@Service
public class CommentService {
		
	
	private final UserCommentMapper userCommentMapper ;
	private final MemberRepository memberRepository ;
	private final ChallengeSubmissionRepository challengeSubmissionRepository;
	private final Notification notification ;
	private final UserChallengeRepository userChallengeRepository;
	
	
	 	 
	
	
	
	
	 /**
	  * 유저 댓글 입력 
	  * @param memberDto
	  * @return
	  */
	@Transactional
	public Map<String, String> userCommentSubmit (CommentDto commentDto) {
		
		log.info("유저 댓글 입력  ------> " + "Start");
		
		Map<String, String> result = new HashMap<String, String>();
		
		boolean alarmYn = true ;
		
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
	        Long insertYn = userCommentMapper.userCommentSubmit(commentDto);
	        	        	        	        
	        if (insertYn != 0) {
	        	result.put("HttpStatus","2.00");		
	    		result.put("Msg",Constants.SUCCESS);
	    		log.info("유저 댓글 입력  ------> " + Constants.SUCCESS);
	        }else {        	
	    		result.put("HttpStatus","1.00");		
	    		result.put("Msg",Constants.FAIL);
	    		log.info("유저 댓글 입력  ------> " + Constants.FAIL);
	        }	        	        
	        	        	        	        
	        
	        // 첼린지 정보에서 유저정보 가져오기
	        Optional<UserChallengeEntity> userChallenge = userChallengeRepository.findByEmail(commentDto.getEmail());	 
	        
	        // 게시글에서 유저 정보 가져오기        	
        	Optional<ChallengeSubmissionEntity> userSubmission = challengeSubmissionRepository.findById(commentDto.getChallengeSubmissionId());
	        
        	// 첼린지 정보와 게시글이 전부 존재하면 UserChallengeId를 비교해서 작성자와 코멘트 단 사람이 같은지 확인 
	        if(userChallenge.isPresent() && userSubmission.isPresent()) {	  
	        	// 작성자와 코멘트 단 사람이 같으면 알람 X 
	        	if(userChallenge.get().getUserChallengeId() == userSubmission.get().getUserChallengeId()) {
	        		alarmYn = false ;
	        	}
	        }
	        	        
	        if(alarmYn) {
	        	 // 대댓글인지 확인
		        if(commentDto.getParentId() != null && commentDto.getParentId() > 0) {
		        	// 대댓글 유저 정보 가져와서 fcm 값 확인
		        	String userEmail = userCommentMapper.findParentComment(commentDto);
		        	Optional<MemberEntity> user = memberRepository.findById(userEmail);
		        		        	
		        	if(user.isPresent() && user.get().getFcmToken() != null) {
		        			        			        		
		        		// 알림 기능 유저 fcm 토큰 , 타이틀 : 앱 이름 , 메시지 : 알림 입력
		        		NotificationRequestDto notiDto = new NotificationRequestDto();
		        		
		        		notiDto.setFcmToken(user.get().getFcmToken());
		        		notiDto.setTitle("데일리 책린지 알림");
		        		notiDto.setMessage(user.get().getNickName() + Constants.parentCommentAlarm);
		        		notiDto.setParamsDepsYn("Y");
		        		
	            		// 게시글 param
	            		List<NotificationRequestDto.Params> paramsList = new ArrayList<>();
	            		NotificationRequestDto.Params params_1 = new NotificationRequestDto.Params();            		
	            		params_1.setRoute(Constants.mission);
	            		params_1.setPostId(commentDto.getChallengeSubmissionId());                 		
	            		paramsList.add(params_1);
		        		                       			        		
		        		// 댓글 param
	              		NotificationRequestDto.Params params_2 = new NotificationRequestDto.Params();            		
	              		params_2.setRoute(Constants.comment);
	              		params_2.setPostId(commentDto.getParentId());              		
	              		paramsList.add(params_2);
	              		
	              		notiDto.setParams(paramsList);
		        			        			        			        	        		
		        		notification.pushNofication(notiDto);
		        			        		
		        	}else {
		        		log.error("부모 댓글이 없거나 FcmToken이 없어서 알림을 보내지 못했습니다.!!!");
		        	}
		        	
		        } else {
	        		            	        	
	            	if(userSubmission.isPresent() && userSubmission.get().getNickName() != null) {
	            		
	            		// 유저 닉네임으로 fcm토큰 값 가져오기
	            		Optional<MemberEntity> userInfo = memberRepository.findByNickName(userSubmission.get().getNickName());
	            		
	            		if(userInfo.isPresent() && userInfo.get().getFcmToken() != null) {
	            			        		
	            			// 알림 기능 유저 fcm 토큰 , 타이틀 : 앱 이름 , 메시지 : 알림 입력
	                		NotificationRequestDto notiDto = new NotificationRequestDto();
	                		
	                		notiDto.setFcmToken(userInfo.get().getFcmToken());
	                		notiDto.setTitle("데일리 책린지 알림");
	                		notiDto.setMessage(userInfo.get().getNickName() + Constants.commentAlarm);
	                		notiDto.setParamsDepsYn("Y");
	                		                		                		                		
	                		// 게시글 param
	                		List<NotificationRequestDto.Params> paramsList = new ArrayList<>();
	                		NotificationRequestDto.Params params_1 = new NotificationRequestDto.Params();            		
	                		params_1.setRoute(Constants.mission);
	                		params_1.setPostId(commentDto.getChallengeSubmissionId());     
	                		paramsList.add(params_1);
	    	        		                            		                		                		                		
	    	        		// 댓글 param
	                		int userCommentSeq = userCommentMapper.findCommentSeq(commentDto);
	                  		NotificationRequestDto.Params params_2 = new NotificationRequestDto.Params();            		
	                  		params_2.setRoute(Constants.comment);
	                  		params_2.setPostId((long) userCommentSeq);              		
	                  		paramsList.add(params_2);
	                		
	                  		notiDto.setParams(paramsList);
	                  		
	                		notification.pushNofication(notiDto);
	                		
	            		}else {
	            			log.error("유자 정보가 없거나 fcm 토큰을 찾지 못해서 알람을 보내지 못했습니다.");
	            		}
	            		        			        			        	        			        		
	            	}else {
	            		log.error("게시글이 없거나 닉네임을 찾지 못해서 알림을 보내지 못했습니다.");
	            	}
	        		
	        		log.error("부모 댓글이 없거나 FcmToken이 없어서 알림을 보내지 못했습니다.!!!");
	        	}
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
				  
			  // 유저 대댓글 삭제
			  userCommentMapper.userParentCommentDel(commentDto);
			  
			  // 유저 댓글 삭제
		      int userCommentDel = userCommentMapper.userCommentDel(commentDto);
		      		      
		      if (userCommentDel != 0) {
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
	  * 유저 댓글 신고여부 상태 확인 
	  * @param memberDto
	  * @return
	  */
	public Map<String, String> userCommentReportState (CommentDto commentDto) {
		
		  log.info("유저 댓글 신고여부 상태 확인  ------> " + "Start");
			
		  Map<String, String> result = new HashMap<String, String>();
			
		  try {
				  
			  // 유저 댓글 신고여부 상태 확인
		      int insertYn = userCommentMapper.userCommentReportState(commentDto);
		      		      
		      if (insertYn != 0) {		    	 
		 		  result.put("REPORT_YN","Y");		 		  
		      }else {		    	
		    	  result.put("REPORT_YN","N");
		      }	             					 
		      
		      result.put("HttpStatus","2.00");		
	 		  result.put("Msg",Constants.SUCCESS);
		      log.info("유저 댓글 신고여부 상태 확인 ------> " + Constants.SUCCESS);
		      
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
	   
	   boolean alarmYn = true ;
	   
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
		   
		   // 작성자 이메일과 좋아요 누른 이메일이 같으면 알람 X 
		   if(commentDto.getEmail().equals(commentDto.getWriterEmail())) {
			   alarmYn = false ;
		   }
		   
		   if(alarmYn) {
			   	// 게시글에서 유저 정보 가져오기        	
	        	Optional<ChallengeSubmissionEntity> userSubmission = challengeSubmissionRepository.findById(commentDto.getChallengeSubmissionId());
	        	        	
	        	if(userSubmission.isPresent() && userSubmission.get().getNickName() != null) {
	        		
	        		// 유저 닉네임으로 fcm토큰 값 가져오기
	        		Optional<MemberEntity> user = memberRepository.findByNickName(userSubmission.get().getNickName());
	        		
	        		if(user.isPresent() && user.get().getFcmToken() != null) {
	        			        		
	        			// 알림 기능 유저 fcm 토큰 , 타이틀 : 앱 이름 , 메시지 : 알림 입력
	            		NotificationRequestDto notiDto = new NotificationRequestDto();
	            		
	            		notiDto.setFcmToken(user.get().getFcmToken());
	            		notiDto.setTitle("데일리 책린지 알림");
	            		notiDto.setMessage(user.get().getNickName() + Constants.likeAlarm);
	            		notiDto.setParamsDepsYn("N");
	            		
	            		// 게시글 param
	            		List<NotificationRequestDto.Params> paramsList = new ArrayList<>();
	            		NotificationRequestDto.Params params = new NotificationRequestDto.Params();            		
	            		params.setRoute(Constants.mission);
	            		params.setPostId(commentDto.getChallengeSubmissionId());     
	            		
	            		paramsList.add(params);
		        		            
	            		notiDto.setParams(paramsList);
	            		
	            		notification.pushNofication(notiDto);
	            		
	        		}else {
	        			log.error("유자 정보가 없거나 fcm 토큰을 찾지 못해서 알람을 보내지 못했습니다.");
	        		}
	        		        			        			        	        			        		
	        	}else {
	        		log.error("게시글이 없거나 닉네임을 찾지 못해서 알림을 보내지 못했습니다.");
	        	}
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
