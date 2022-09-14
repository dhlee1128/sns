package com.project.sns.model;

import java.sql.Timestamp;

import com.project.sns.model.entity.CommentEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Comment {
    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Comment fromEntity(CommentEntity entity) {
        return new Comment(
            entity.getId(),
            entity.getComment(),
            entity.getUser().getUserName(),
            entity.getPost().getId(),
            entity.getRegisteredAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt()
        );

    }

}
