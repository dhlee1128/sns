package com.project.sns.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sns.controller.request.PostCreateRequest;
import com.project.sns.exception.ErrorCode;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.fixture.PostEntityFixture;
import com.project.sns.model.Post;
import com.project.sns.service.PostService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @DisplayName("포스트 작성")
    @Test
    @WithMockUser
    void given_when_then() throws Exception {
        String title = "title";
        String body = "body";


        mockMvc.perform(post("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
        ).andDo(print())
        .andExpect(status().isOk());
    }

    @DisplayName("포스트 작성시 로그인 하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenTitle_whenNotLogined_thenReturnError() throws Exception {
        String title = "title";
        String body = "body";

        mockMvc.perform(put("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
        ).andDo(print())
        .andExpect(status().isOk());
    }

    @DisplayName("포스트를 수정하는 경우")
    @Test
    @WithMockUser
    void givenTitle_when_thenModified() throws Exception {
        String title = "title";
        String body = "body";

        when(postService.modify(eq(title), eq(body), any(), any()))
                        .thenReturn(Post.fromEntity(PostEntityFixture.get("userName", 1, 1)));

        mockMvc.perform(post("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
        ).andDo(print())
        .andExpect(status().isOk());
    }

    @DisplayName("포스트를 수정시 로그인 하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenTitle_whenNotLogined_thenError() throws Exception {
        String title = "title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }

    @DisplayName("포스트를 수정시 본인이 작성한 글이 아닌 경우")
    @Test
    @WithMockUser
    void givenTitle_whenUsersPost_thenError() throws Exception {
        String title = "title";
        String body = "body";

        // mocking
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).modify(eq(title), eq(body), any(), eq(1));

        mockMvc.perform(post("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }

    @DisplayName("포스트를 수정시 수정하려는 글이 없는 경우")
    @Test
    @WithMockUser
    void givenTitle_whenNotExistedPost_thenError() throws Exception {
        String title = "title";
        String body = "body";

        // mocking

        mockMvc.perform(post("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
        ).andDo(print())
        .andExpect(status().isNotFound());
    }

}
