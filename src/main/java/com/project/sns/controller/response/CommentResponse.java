package com.project.sns.controller.response;

import java.sql.Timestamp;

import com.project.sns.model.Comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentResponse {
    
    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static CommentResponse fromComment(Comment comment) {
        return new CommentResponse(
            comment.getId(), 
            comment.getComment(), 
            comment.getUserName(),
            comment.getPostId(),
            comment.getRegisteredAt(),
            comment.getUpdatedAt(),
            comment.getDeletedAt()
        );
    }

}
