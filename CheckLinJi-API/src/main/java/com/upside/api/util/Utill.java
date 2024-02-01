package com.upside.api.util;

import java.util.List;
import java.util.stream.Collectors;

import com.upside.api.entity.CommentEntity;

public class Utill {
	
	
    // 댓글 엔터티 리스트에서 댓글 기본 키(commentSeq) 추출
    public static List<Long> extractCommentSeqs(List<CommentEntity> comments) {
        return comments.stream()
                .map(CommentEntity::getCommentSeq)
                .collect(Collectors.toList());
    }

}
