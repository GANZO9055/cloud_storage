package com.example.cloud_storage.minio.controller;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;
import com.example.cloud_storage.minio.service.directory.FolderService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Validated
@RequestMapping("/api/directory")
public class FolderController {

    private FolderService directoryService;

    @PostMapping
    public ResponseEntity<DirectoryResponseDto> createFolder(
            @RequestParam
            @Size(min = 1, max = 50)
            @NotBlank
            String path) {
        return new ResponseEntity<>(
                directoryService.createFolder(path),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Resource>> getFolderContents(
            @RequestParam
            @Size(min = 1, max = 50)
            @NotBlank
            String path) {
        return new ResponseEntity<>(
                directoryService.getFolderContents(path),
                HttpStatus.OK
        );
    }
}
