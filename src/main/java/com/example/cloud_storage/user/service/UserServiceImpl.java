package com.example.cloud_storage.user.service;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.exception.UnauthorizedUserException;
import com.example.cloud_storage.user.exception.UserAlreadyExistsException;
import com.example.cloud_storage.user.exception.UsernameNotFoundException;
import com.example.cloud_storage.user.model.Role;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;

    @Override
    public User create(UserRequestDto userRequestDto,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            log.warn("Registration failed: username={} already exists", userRequestDto.getUsername());
            throw new UserAlreadyExistsException("User is already busy!");
        }

        createCookie(userRequestDto, request, response);

        User savedUser;
        try {
            User user = new User();
            user.setUsername(userRequestDto.getUsername());
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
            user.setRole(Role.USER);
            savedUser = userRepository.save(user);
        } catch (Exception exception) {
            log.error("Error while registration user username={}", userRequestDto.getUsername());
            throw new RuntimeException(exception);
        }
        return savedUser;
    }

    @Override
    public User authenticate(UserRequestDto userRequestDto,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        User user = userRepository.findByUsername(userRequestDto.getUsername())
                .orElseThrow(() -> {
                        log.warn("Authentication failed: user no found (username={})!", userRequestDto.getUsername());
                        return new UsernameNotFoundException("User not found: " + userRequestDto.getUsername());
                });
        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            log.warn("Authentication failed: invalid password for username={}", userRequestDto.getUsername());
            throw new UnauthorizedUserException("Invalid password!");
        }
        createCookie(userRequestDto, request, response);
        return user;
    }

    private void createCookie(UserRequestDto userRequestDto,
                              HttpServletRequest request,
                              HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequestDto.getUsername(),
                        userRequestDto.getPassword()
                )
        );

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);

        session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        session.setAttribute("username", userRequestDto.getUsername());

        Cookie cookie = new Cookie("JSESSIONID", session.getId());
        cookie.setPath("/api");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(60 * 60);

        response.addCookie(cookie);
    }
}
