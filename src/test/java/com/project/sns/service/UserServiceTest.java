package com.project.sns.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.project.sns.exception.SnsApplicationException;
import com.project.sns.fixture.UserEntityFixture;
import com.project.sns.model.entity.UserEntity;
import com.project.sns.repository.UserEntityRepository;

@SpringBootTest
public class UserServiceTest {
    
    @Autowired
    private UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;

    @MockBean
    private BCryptPasswordEncoder encoder;

    @DisplayName("회원가입이 정상적으로 동작하는 경우")
    @Test
    void givenUserNameAndPassword_whenNotExistUserName_thenJoin() {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(UserEntityFixture.get(userName, password));

        assertDoesNotThrow(() -> userService.join(userName, password));
    }

    @DisplayName("회원가입시 UserName으로 회원가입한 유저가 이미 있을 경우")
    @Test
    void givenUserNameAndPassword_whenExistUserName_thenError() {
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));
        when(encoder.encode(password)).thenReturn("encrypt_password");
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));


        assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
    }

    @DisplayName("로그인이 정상적으로 동작하는 경우")
    @Test
    void givenUserNameAndPassword_whenNotExistUserName_thenLogin() {
        String userName = "userName";
        String password = "password";
        UserEntity fixture = UserEntityFixture.get(userName, password);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @DisplayName("로그인시 UserName으로 회원가입한 유저가 없는 경우")
    @Test
    void givenUserNameAndPassword_whenNotExistJoinedUser_thenError() {
        String userName = "userName";
        String password = "password";

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.empty());


        assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
    }

    @DisplayName("로그인시 password가 틀린 경우")
    @Test
    void givenUserNameAndPassword_whenNotCorrectPassword_thenError() {
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        // mocking
        when(userEntityRepository.findByUserName(userName)).thenReturn(Optional.of(fixture));

        assertThrows(SnsApplicationException.class, () -> userService.join(userName, wrongPassword));
    }


}
