package com.example.cloud_storage.minio.validator;

import com.example.cloud_storage.exception.minio.InvalidPathException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class ValidatorPath {

    private static final Pattern PATH_PATTERN = Pattern.compile("^[a-zA-Z0-9/_\\-.]+/?$");

    public void checkingPath(String path) {
        if (!PATH_PATTERN.matcher(path).matches() || path.contains("..") || path.contains("//")) {
            log.warn("Invalid characters in path");
            throw new InvalidPathException("Invalid characters in path");
        }
    }
}
