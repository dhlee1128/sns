package com.project.sns.model;

import java.sql.Timestamp;

import com.project.sns.model.entity.PostEntity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Post {
    private Integer id;
    private String title;
    private String body;
    private User user; 
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    private Timestamp deletedAt;

    public static Post fromEntity(PostEntity entity) {
        return new Post(
            entity.getId(),
            entity.getTitle(),
            entity.getBody(),
            User.fromEntity(entity.getUser()),
            entity.getRegisteredAt(),
            entity.getUpdatedAt(),
            entity.getDeletedAt()
        );
    }
}
