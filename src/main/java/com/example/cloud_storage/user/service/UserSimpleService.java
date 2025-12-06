package com.example.cloud_storage.user.service;

import com.example.cloud_storage.user.dto.UserRequestDto;
import com.example.cloud_storage.user.exception.UserAlreadyExistsException;
import com.example.cloud_storage.user.mapper.UserMapper;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserSimpleService implements UserService {

    private UserRepository userRepository;
    private UserMapper userMapper;

    @Override
    public User create(UserRequestDto userRequestDto) {
        if (userRepository.findByUsername(userRequestDto.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException("User is already busy!");
        }
        User savedUser;
        try {
            User user = userMapper.toEntity(userRequestDto);
            savedUser = userRepository.save(user);
        } catch (Exception exception) {
            throw exception;
        }
        return savedUser;
    }

    @Override
    public User getUser(UserRequestDto userRequestDto) {
        User user = userMapper.toEntity(userRequestDto);
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        if (optionalUser.isEmpty()) {
            throw new RuntimeException();
        }
        return optionalUser.get();
    }
}
