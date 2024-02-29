package com.upside.api.util;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

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
    
    public static Path getImagePath(String imageName) {
        // ClassPathResource를 사용하여 이미지 파일의 경로를 가져옴
        Resource resource = new ClassPathResource("static/" + imageName);
        try {
            return resource.getFile().toPath();
        } catch (IOException e) {
            throw new RuntimeException("이미지를 가져올 수 없습니다.", e);
        }
    }
    
}
