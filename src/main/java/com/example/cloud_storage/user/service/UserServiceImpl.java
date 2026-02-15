package com.example.cloud_storage.user.service;

import com.example.cloud_storage.minio.storage.StorageService;
import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.exception.user.UnauthorizedUserException;
import com.example.cloud_storage.exception.user.UserAlreadyExistsException;
import com.example.cloud_storage.exception.user.UsernameNotFoundException;
import com.example.cloud_storage.user.model.Role;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.repository.UserRepository;
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

    private static final String SPRING_SECURITY_ATTRIBUTE = "SPRING_SECURITY_CONTEXT";

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private StorageService storageService;

    @Override
    public User create(UserRequestDto userRequestDto,
                       HttpServletRequest request,
                       HttpServletResponse response) {
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            log.warn("Registration failed: username={} already exists", userRequestDto.getUsername());
            throw new UserAlreadyExistsException("User is already busy!");
        }

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

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequestDto.getUsername(),
                        userRequestDto.getPassword()
                )
        );
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_ATTRIBUTE, securityContext);

        storageService.createRootFolder(savedUser.getId());
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
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequestDto.getUsername(),
                        userRequestDto.getPassword()
                )
        );
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(SPRING_SECURITY_ATTRIBUTE, securityContext);
        return user;
    }
}
