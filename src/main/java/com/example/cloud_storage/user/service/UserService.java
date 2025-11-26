package com.example.cloud_storage.user.service;

import com.example.cloud_storage.user.model.User;

public interface UserService {
    User create(User user);
    void deleteUser(Long id);
    User getUser(User user);
}
