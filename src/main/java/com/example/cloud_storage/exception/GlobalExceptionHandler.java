package com.example.cloud_storage.exception;

import com.example.cloud_storage.exception.minio.*;
import com.example.cloud_storage.exception.user.UnauthorizedUserException;
import com.example.cloud_storage.exception.user.UserAlreadyExistsException;
import com.example.cloud_storage.exception.user.UsernameNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedUserException.class)
    public ResponseEntity<?> handleNotFound(UnauthorizedUserException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<?> handleNotFound(UsernameNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<?> handleConflict(UserAlreadyExistsException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of("message", exception.getMessage())
                );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        Map.of("message", "internal server error")
                );
    }

    @ExceptionHandler(StorageException.class)
    public ResponseEntity<?> handleException(StorageException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException exception) {
        Map<String, String> errors = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage() != null
                                ? error.getDefaultMessage()
                                : "Validation failed"
                ));
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of("message", "Validation failed", "errors", errors)
                );
    }

    @ExceptionHandler(InvalidPathException.class)
    public ResponseEntity<?> handleInvalidPath(InvalidPathException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(
                        Map.of("message", exception.getMessage())
                );
    }

    @ExceptionHandler(FolderAlreadyExistsException.class)
    public ResponseEntity<?> handleConflictFolder(FolderAlreadyExistsException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of("message", exception.getMessage())
                );
    }

    @ExceptionHandler(FolderNotFoundException.class)
    public ResponseEntity<?> handleNotFoundFolder(FolderNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of("message", exception.getMessage())
                );
    }

    @ExceptionHandler(ParentFolderNotFoundException.class)
    public ResponseEntity<?> handleNotFoundParent(ParentFolderNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of("message", exception.getMessage())
                );
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleNotFoundResource(ResourceNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                        Map.of("message", exception.getMessage())
                );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<?> handleConflictResource(ResourceAlreadyExistsException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(
                        Map.of("message", exception.getMessage())
                );
    }
}
