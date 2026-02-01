package com.example.cloud_storage.user.util;

import com.example.cloud_storage.exception.user.UsernameNotFoundException;
import com.example.cloud_storage.user.model.User;
import com.example.cloud_storage.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Component
public class UserUtil {

    private UserRepository userRepository;

    public  Integer getId() {
        return getUser().getId();
    }

    public String getUsername() {
        return getUser().getUsername();
    }

    private User getUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> optional = userRepository.findByUsername(authentication.getName());
        if (optional.isEmpty()) {
            log.error("Username not found");
            throw new UsernameNotFoundException("Username not found");
        }
        return optional.get();
    }
}
