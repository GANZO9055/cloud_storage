package com.example.cloud_storage.user.service;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.exception.UnauthorizedUserException;
import com.example.cloud_storage.user.exception.UserAlreadyExistsException;
import com.example.cloud_storage.user.model.Role;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public User create(UserRequestDto userRequestDto, HttpServletRequest request) {
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
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
                throw new RuntimeException(exception);
        }
        return savedUser;
    }

    @Override
    public User authenticate(UserRequestDto userRequestDto) {
        User user = userRepository.findByUsername(userRequestDto.getUsername())
                .orElseThrow(
                        () -> new UnauthorizedUserException("User not found: " + userRequestDto.getUsername())
                );
        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            throw new UnauthorizedUserException("Invalid password!");
        }

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(
                        () -> new UsernameNotFoundException("User not found: " + username)
                );
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole().getAuthority())
                .build();
    }
}
