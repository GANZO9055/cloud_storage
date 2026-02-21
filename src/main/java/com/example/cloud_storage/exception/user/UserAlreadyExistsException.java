package com.example.cloud_storage.exception.user;

import com.example.cloud_storage.exception.CloudStorageException;

public class UserAlreadyExistsException extends CloudStorageException {
    public UserAlreadyExistsException(String message) {
        super(message);
    }
}
