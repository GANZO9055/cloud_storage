package com.example.cloud_storage.exception.minio;

import com.example.cloud_storage.exception.CloudStorageException;

public class InvalidPathException extends CloudStorageException {
  public InvalidPathException(String message) {
    super(message);
  }
}
