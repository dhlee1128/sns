package com.project.sns.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.sns.exception.ErrorCode;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.model.Post;
import com.project.sns.model.entity.PostEntity;
import com.project.sns.model.entity.UserEntity;
import com.project.sns.repository.PostEntityRepository;
import com.project.sns.repository.UserEntityRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    
    @Transactional
    public void create(String title, String body, String userName) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> 
                    new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> 
                    new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
        
        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", userName)));

        // post permission
        if (postEntity.getUser() != userEntity ) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));

    }

    @Transactional
    public void delete(String userName, Integer postId) { 
        UserEntity userEntity = userEntityRepository.findByUserName(userName).orElseThrow(() -> 
                    new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
        
        // post exist
        PostEntity postEntity = postEntityRepository.findById(postId).orElseThrow(() ->
                    new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", userName)));
        
        // post permission
        if (postEntity.getUser() != userEntity ) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", postId));
        }

        postEntityRepository.delete(postEntity);
    }

}
