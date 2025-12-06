package com.example.cloud_storage.user.service;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.exception.UserAlreadyExistsException;
import com.example.cloud_storage.user.model.Role;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    @Override
    public User create(UserRequestDto userRequestDto) {
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User is already busy!");
        }
        User savedUser;
        try {
            User user = new User();
            user.setUsername(userRequestDto.getUsername());
            user.setPassword(passwordEncoder.encode(userRequestDto.getPassword()));
            user.setRole(Role.USER.getAuthority());
            savedUser = userRepository.save(user);
        } catch (Exception exception) {
            throw exception;
        }
        return savedUser;
    }

    @Override
    public User getUser(UserRequestDto userRequestDto) {
        Optional<User> optionalUser = userRepository.findByUsername(userRequestDto.getUsername());
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found: " + userRequestDto.getUsername());
        }
        return optionalUser.get();
    }
}
