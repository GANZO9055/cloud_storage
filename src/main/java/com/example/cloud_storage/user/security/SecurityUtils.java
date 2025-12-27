package com.example.cloud_storage.user.security;

import com.example.cloud_storage.user.exception.UnauthorizedUserException;
import com.example.cloud_storage.user.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    public static Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedUserException("User not authenticated!");
        }
        User user = (User) authentication.getPrincipal();
        return user.getId();
    }
}
