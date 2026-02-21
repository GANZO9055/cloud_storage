package com.example.cloud_storage.exception.user;

import com.example.cloud_storage.exception.CloudStorageException;

public class UsernameNotFoundException extends CloudStorageException {
    public UsernameNotFoundException(String message) {
        super(message);
    }
}
