package com.example.cloud_storage.user.service;

import com.example.cloud_storage.minio.storage.MinioStorageService;
import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.exception.user.UnauthorizedUserException;
import com.example.cloud_storage.exception.user.UserAlreadyExistsException;
import com.example.cloud_storage.exception.user.UsernameNotFoundException;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
class UserServiceImplTest {

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer =
            new PostgreSQLContainer<>("postgres:16")
            .withDatabaseName("cloud_storage")
            .withUsername("postgres")
            .withPassword("password");

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockitoBean
    private AuthenticationManager authenticationManager;
    @MockitoBean
    private HttpServletRequest request;
    @MockitoBean
    private HttpServletResponse response;
    @MockitoBean
    private HttpSession session;
    @MockitoBean
    private MinioStorageService minioStorageService;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        when(request.getSession(true)).thenReturn(session);
    }

    @Test
    void whenCreateUserThenSaveUserSuccess() {
        UserRequestDto userTest = new UserRequestDto("testUser", "testPassword");

        User user = userService.create(userTest, request, response);

        assertThat(user.getId()).isNotNull();
        assertEquals(userTest.getUsername(), user.getUsername());
        assertTrue(passwordEncoder.matches(userTest.getPassword(), user.getPassword()));
    }

    @Test
    void whenCreateCloneUserThenGetException() {
        UserRequestDto userTest = new UserRequestDto("testUser", "testPassword");
        UserRequestDto cloneUserTest = new UserRequestDto("testUser", "testPassword");

        userService.create(userTest, request, response);

        UserAlreadyExistsException exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.create(cloneUserTest, request, response)
        );
        assertTrue(exception.getMessage().contains("User is already busy!"));
    }

    @Test
    void whenCreateUserThenAuthenticationUserSuccess() {
        UserRequestDto userTest = new UserRequestDto("testUser", "testPassword");

        User savedUser = userService.create(userTest, request, response);

        User authUser = userService.authenticate(userTest, request, response);

        assertEquals(savedUser.getId(), authUser.getId());
        assertEquals(savedUser.getUsername(), authUser.getUsername());
        assertTrue(passwordEncoder.matches(userTest.getPassword(), authUser.getPassword()));
    }

    @Test
    void whenAuthenticationUserThenGetExceptionUserNotFound() {
        UserRequestDto user = new UserRequestDto("testUser", "testPassword");
        UserRequestDto userTest = new UserRequestDto("test", "testPassword");

        userService.create(user, request, response);

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userService.authenticate(userTest, request, response)
        );
        assertTrue(exception.getMessage().contains("User not found: " + userTest.getUsername()));
    }

    @Test
    void whenAuthenticationUserThenGetExceptionIncorrectPassword() {
        UserRequestDto user = new UserRequestDto("testUser", "testPassword");
        UserRequestDto userTest = new UserRequestDto("testUser", "falsePassword");

        userService.create(user, request, response);

        UnauthorizedUserException exception = assertThrows(UnauthorizedUserException.class,
                () -> userService.authenticate(userTest, request, response)
        );
        assertTrue(exception.getMessage().contains("Invalid password!"));
    }
}