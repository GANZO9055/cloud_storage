package com.example.cloud_storage.exception.minio;

public class InvalidPathException extends RuntimeException {
  public InvalidPathException(String message) {
    super(message);
  }
}
