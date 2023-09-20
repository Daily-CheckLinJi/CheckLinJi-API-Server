package com.upside.api.service;



import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upside.api.dto.ReportCommentDto;
import com.upside.api.dto.ReportSubmissionDto;
import com.upside.api.entity.ChallengeSubmissionEntity;
import com.upside.api.entity.CommentEntity;
import com.upside.api.entity.ReportCommentEntity;
import com.upside.api.entity.ReportSubmissionEntity;
import com.upside.api.repository.ChallengeSubmissionRepository;
import com.upside.api.repository.CommentRepository;
import com.upside.api.repository.ReportCommentRepository;
import com.upside.api.repository.ReportSubmissionRepository;
import com.upside.api.util.Constants;
import com.upside.api.util.DateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;




@RequiredArgsConstructor
@Slf4j // 로깅에 대한 추상 레이어를 제공하는 인터페이스의 모음.
@Service
public class ReportService {
		
	private final ReportSubmissionRepository reportSubmissionRepository;
	private final ReportCommentRepository reportCommentRepository;
	private final ChallengeSubmissionRepository challengeSubmissionRepository;
	private final CommentRepository commentRepository;
	 	 
		 
		
	/**
	 * 게시글 신고하기
	 * @param reSubmissionDto
	 * @return
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> reportSubmission (ReportSubmissionDto reSubmissionDto) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		log.info("게시글 신고  ------> " + "Start");
								 						 					
		  try {
	        	
			  Long existsReport = reportSubmissionRepository.countByChallengeSubmissionIdAndEmail(reSubmissionDto.getChallengeSubmissionId() , reSubmissionDto.getEmail());
			  
			  // 같은 게시글을 신고한 이력이 있으면 이미 신고되었음 처리
			  if(existsReport != 0) { 
		    	    result.put("HttpStatus","2.00");		
		   			result.put("Msg","이미 신고된 게시글입니다.");
		   			return result ;
			  }
			  				  
			  // 게시글 신고 
			  ReportSubmissionEntity reSubmissionEntity =  ReportSubmissionEntity.builder()
					  	 .challengeSubmissionId(reSubmissionDto.getChallengeSubmissionId())
						 .email(reSubmissionDto.getEmail())
						 .reportMsg(reSubmissionDto.getReportMsg())
						 .reportDate(DateTime.nowDate())							 
						 .build();
			  				  
			  reportSubmissionRepository.save(reSubmissionEntity);
			  				  
			  // 게시글 신고 후 신고 카운트 5회 이상이면 게시글 상태 N으로 변경
			  Long sumReportCnt = reportSubmissionRepository.countByChallengeSubmissionId(reSubmissionDto.getChallengeSubmissionId());
			  				  				  
			  if(sumReportCnt >= 5) {					  
				  Optional<ChallengeSubmissionEntity> submission = challengeSubmissionRepository.findById(reSubmissionDto.getChallengeSubmissionId());					  
				  ChallengeSubmissionEntity changeSubmission = submission.get();			 
				  changeSubmission.setSubmissionCompleted("N");
			  }
			  				  
			  result.put("HttpStatus","2.00");		
	   		  result.put("Msg","신고가 완료되었습니다.");
			  
	   		  log.info("게시글 신고  ------> " + "End");
	   		  
			} catch (Exception e) {										
	    	    result.put("HttpStatus","1.00");		
	   			result.put("Msg",Constants.SYSTEM_ERROR);
	   			log.error("게시글 신고 ----------> " + Constants.SYSTEM_ERROR ,  e);
	   		 return result ;			
			}               		 
		  return result ;					 	    		   
}
	 
	
	/**
	 * 게시글 댓글 신고하기
	 * @param reSubmissionDto
	 * @return
	 */
	@Transactional // 트랜잭션 안에서 entity를 조회해야 영속성 상태로 조회가 되고 값을 변경해면 변경 감지(dirty checking)가 일어난다.
	public Map<String, String> reportComment(ReportCommentDto reCommentDto) {
		
		Map<String, String> result = new HashMap<String, String>();
		
		log.info("게시글 댓글 신고  ------> " + "Start");
								 						 					
		  try {
	        	
			  Long existsReport = reportCommentRepository.countByCommentSeqAndEmail(reCommentDto.getCommentSeq() , reCommentDto.getEmail());
			  
			  // 같은 게시글을 신고한 이력이 있으면 이미 신고되었음 처리
			  if(existsReport != 0) { 
		    	    result.put("HttpStatus","2.00");		
		   			result.put("Msg","이미 신고된 댓글입니다.");
		   			return result ;
			  }
			  				  
			  // 게시글 신고 
			  ReportCommentEntity reCommentEntity =  ReportCommentEntity.builder()
					  	 .commentSeq(reCommentDto.getCommentSeq())
						 .email(reCommentDto.getEmail())
						 .reportMsg(reCommentDto.getReportMsg())
						 .reportDate(DateTime.nowDate())							 
						 .build();
			  				  
			  reportCommentRepository.save(reCommentEntity);
			  				  
			  // 게시글 신고 후 신고 카운트 5회 이상이면 게시글 상태 N으로 변경
			  Long sumReportCnt = reportCommentRepository.countByCommentSeq(reCommentDto.getCommentSeq());
			  				  				  
			  if(sumReportCnt >= 5) {					  
				  Optional<CommentEntity> comment = commentRepository.findById(reCommentDto.getCommentSeq());					  
				  CommentEntity changeCommentState = comment.get();			 
				  changeCommentState.setCommentState("N");
			  }
			  				  
			  result.put("HttpStatus","2.00");		
	   		  result.put("Msg","신고가 완료되었습니다.");
			  
	   		  log.info("게시글 댓글 신고  ------> " + "End");
	   		  
			} catch (Exception e) {										
	    	    result.put("HttpStatus","1.00");		
	   			result.put("Msg","알수없는 이유로 신고에 실패하였습니다.");
	   			log.error("게시글 댓글 신고 ----------> " + Constants.SYSTEM_ERROR ,  e);
	   		 return result ;			
			}               		 
		  return result ;					 	    		   
}	
		
		

	
	}
