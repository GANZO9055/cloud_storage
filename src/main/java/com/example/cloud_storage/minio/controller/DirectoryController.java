package com.example.cloud_storage.minio.controller;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;
import com.example.cloud_storage.minio.service.directory.DirectoryService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/directory")
public class DirectoryController {

    private DirectoryService directoryService;

    @PostMapping
    public ResponseEntity<DirectoryResponseDto> createDirectory(@RequestParam String path) {
        return new ResponseEntity<>(
                directoryService.createDirectory(path),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Resource>> getFolderContents(@RequestParam String path) {
        return new ResponseEntity<>(
                directoryService.getResource(path),
                HttpStatus.OK
        );
    }
}
