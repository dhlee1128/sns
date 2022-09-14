package com.project.sns.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sns.controller.request.PostCommentRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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
    void givenPost_when_thenSuccess() throws Exception {
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
    void givenPost_whenNotLogined_thenReturnError() throws Exception {
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
    void givenPostForUpdate_when_thenModified() throws Exception {
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
    void givenPostForUpdate_whenNotLogined_thenError() throws Exception {
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
    void givenPostForUpdate_whenUsersPost_thenError() throws Exception {
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
    void givenPostForUpdate_whenNotExistedPost_thenError() throws Exception {
        String title = "title";
        String body = "body";

        // mocking

        mockMvc.perform(post("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
        ).andDo(print())
        .andExpect(status().isNotFound());
    }
    
    @DisplayName("포스트를 삭제하는 경우")
    @Test
    @WithMockUser
    void givenPost_when_thendeleted() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isOk());
    }
    
    @DisplayName("포스트 삭제시 로그인하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenPostForDelete_whenNotLogined_thenError() throws Exception {

        mockMvc.perform(delete("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }
    
    @DisplayName("포스트 삭제시 작성자와 삭제 요청자가 다를 경우")
    @Test
    @WithMockUser
    void givenPostForDelete_whenNotEqualsUser_thenError() throws Exception {
        // mocking
        doThrow(new SnsApplicationException(ErrorCode.INVALID_PERMISSION)).when(postService).delete(any(), any());

        mockMvc.perform(delete("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }
    
    @DisplayName("포스트 삭제시 삭제하려는 포스트가 존재하지 않을 경우")
    @Test
    @WithMockUser
    void givenPostForDelete_whenNotExistedPost_thenError() throws Exception {
        // mocking
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).delete(any(), any());

        mockMvc.perform(delete("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }

    
    @DisplayName("피트 목록을 가져오는 경우")
    @Test
    @WithMockUser
    void givenRequestFeed_when_thenGetFeed() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isOk());
    }
    
    @DisplayName("피드목록 요청시 로그인하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenRequestFeed_whenNotLogined_thenError() throws Exception {
        when(postService.list(any())).thenReturn(Page.empty());

        mockMvc.perform(delete("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }
    
    @DisplayName("내피트 목록을 가져오는 경우")
    @Test
    @WithMockUser
    void givenRequestMyFeed_when_thenGetMyFeed() throws Exception {
        when(postService.my(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/v1/posts")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isOk());
    }
    
    @DisplayName("내피드목록 요청시 로그인하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenRequestMyFeed_whenNotLogined_thenError() throws Exception {
        when(postService.my(any(), any())).thenReturn(Page.empty());

        mockMvc.perform(delete("/api/v1/posts/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }
    
    @DisplayName("좋아요 기능")
    @Test
    @WithMockUser
    void given_when_thenLikes() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isOk());
    }
    
    @DisplayName("좋아요 버튼 클릭시 로그인하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenClickLikeButton_whenNotLogined_thenError() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/likes")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }
    
    @DisplayName("좋아요 버튼 클릭시 게시글이 없는 경우")
    @Test
    @WithMockUser
    void givenClickLikeButton_whenNotExisted_thenError() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).like(any(), any());

        mockMvc.perform(post("/api/v1/posts/1/likes")
                .contentType(MediaType.APPLICATION_JSON)
        ).andDo(print())
        .andExpect(status().isNotFound());
    }
    
    @DisplayName("댓글 기능")
    @Test
    @WithMockUser
    void given_when_thenComments() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PostCommentRequest("comment")))
        ).andDo(print())
        .andExpect(status().isOk());
    }
    
    @DisplayName("댓글 작성시 로그인하지 않은 경우")
    @Test
    @WithAnonymousUser
    void givenComments_whenNotLogined_thenError() throws Exception {
        mockMvc.perform(post("/api/v1/posts/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PostCommentRequest("comment")))
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }
    
    @DisplayName("댓글 작성시 게시글이 없는 경우")
    @Test
    @WithMockUser
    void givenComments_whenNotExisted_thenError() throws Exception {
        doThrow(new SnsApplicationException(ErrorCode.POST_NOT_FOUND)).when(postService).comment(any(), any(), any());

        mockMvc.perform(post("/api/v1/posts/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new PostCommentRequest("comment")))
        ).andDo(print())
        .andExpect(status().isNotFound());
    }

}
