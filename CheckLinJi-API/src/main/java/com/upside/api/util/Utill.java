package com.upside.api.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.upside.api.entity.ChallengeSubmissionEntity;
import com.upside.api.entity.CommentEntity;

public class Utill {
	
	

    
    // 게시글 엔터티 리스트에서 게시글 기본 키(challengeSubmissionId) 추출
    public static List<Long> extractMissionSeq(List<ChallengeSubmissionEntity> missions) {
        return missions.stream()
                .map(ChallengeSubmissionEntity::getChallengeSubmissionId)
                .collect(Collectors.toList());
    }
    
    // 댓글 엔터티 리스트에서 댓글 기본 키(commentSeq) 추출
    public static List<Long> extractCommentSeqs(List<CommentEntity> comments) {
        return comments.stream()
                .map(CommentEntity::getCommentSeq)
                .collect(Collectors.toList());
    }
    
    public static HttpHeaders getFileExtension(Path imagePath) throws UnsupportedEncodingException {
    	
    	HttpHeaders headers = new HttpHeaders();
    	
        // 파일명과 확장자를 구분
        String fileName = imagePath.getFileName().toString();
        String fileExtension = "";

        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1) {
            // 파일명에 확장자가 있는 경우
            fileExtension = fileName.substring(dotIndex + 1);
            fileName = fileName.substring(0, dotIndex);
        }
        
        // MIME 타입 설정
        MediaType mediaType = getMediaType(fileExtension);

        // Content-Type 헤더 설정
        if (mediaType != null) {
            headers.setContentType(mediaType);
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()));
        }
        
		return headers;
    }
    
    
    public static MediaType getMediaType(String fileExtension) {
        switch (fileExtension) {
            case "jpg":
            case "jpeg":
                return MediaType.IMAGE_JPEG;
            case "png":
                return MediaType.IMAGE_PNG;
            case "gif":
                return MediaType.IMAGE_GIF;
            // 추가적인 확장자에 대한 처리를 원하는 대로 추가할 수 있습니다.
            default:
                return null; // 기본적으로는 Content-Type을 설정하지 않음
        }
    }
    
}
