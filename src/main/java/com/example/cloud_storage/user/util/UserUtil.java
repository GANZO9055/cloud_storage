package com.example.cloud_storage.user.util;

import com.example.cloud_storage.exception.user.UsernameNotFoundException;
import com.example.cloud_storage.user.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Slf4j
public class UserUtil {

    public static Integer getId() {
        return getUser().getId();
    }

    public static String getUsername() {
        return getUser().getUsername();
    }

    private static CustomUserDetails getUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        throw new UsernameNotFoundException("Username not found");
    }
}
