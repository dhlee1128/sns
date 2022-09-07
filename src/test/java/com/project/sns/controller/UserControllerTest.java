package com.project.sns.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.sns.controller.request.UserJoinRequest;
import com.project.sns.controller.request.UserLoginRequest;
import com.project.sns.exception.ErrorCode;
import com.project.sns.exception.SnsApplicationException;
import com.project.sns.model.User;
import com.project.sns.service.UserService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @DisplayName("회원가입이 성공한다.")
    @Test
    public void givenUserNameAndPassword_when_thenReturnSuccess() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.join(userName, password)).thenReturn(mock(User.class));

        mockMvc.perform(post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
        ).andDo(print())
        .andExpect(status().isOk());
    }

    @DisplayName("회원가입시 이미 회원가입된 userName으로 회원가입을 하는 경우 에러를 반환한다.")
    @Test
    public void givenUserName_whenExistedUserName_thenReturnError() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.join(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

        mockMvc.perform(post("/api/v1/users/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserJoinRequest(userName, password)))
        ).andDo(print())
        .andExpect(status().isConflict());
    }

    @DisplayName("로그인이 성공한다.")
    @Test
    public void givenUserNameAndPassword_when_thenReturnLogin() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenReturn("test_token");

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
        ).andDo(print())
        .andExpect(status().isOk());
    }

    @DisplayName("로그인시 회원가입이 안된 UserName을 입력할 경우 에러 반환")
    @Test
    public void givenUserName_whenNotJoinedUserName_thenReturnError() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.USER_NOT_FOUND, ""));

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
        ).andDo(print())
        .andExpect(status().isNotFound());
    }

    @DisplayName("로그인시 틀린 Password를 입력할 경우 에러 반환")
    @Test
    public void givenPassword_whenNotCorrectPassword_thenReturnError() throws Exception {
        String userName = "userName";
        String password = "password";

        when(userService.login(userName, password)).thenThrow(new SnsApplicationException(ErrorCode.INVALID_PASSWORD, ""));

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(new UserLoginRequest(userName, password)))
        ).andDo(print())
        .andExpect(status().isUnauthorized());
    }

    
}
