package com.project.sns.controller.response;


import java.sql.Timestamp;

import com.project.sns.model.Post;
import com.project.sns.model.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostResponse {

    private Integer id;
    private String title;
    private String body;
    private UserResponse user; 
    private Timestamp registeredAt;
    private Timestamp updatedAt;
    
    public static PostResponse fromPost(Post post) {
        return new PostResponse(
            post.getId(), 
            post.getTitle(), 
            post.getBody(),
            UserResponse.fromUser(post.getUser()),
            post.getRegisteredAt(),
            post.getUpdatedAt()
        );
    }


}
