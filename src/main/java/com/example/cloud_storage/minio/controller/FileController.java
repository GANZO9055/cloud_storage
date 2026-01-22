package com.example.cloud_storage.minio.controller;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.service.file.FileService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/resource")
@AllArgsConstructor
public class FileController {

    private FileService fileService;

    @GetMapping
    public ResponseEntity<Resource> getResource(@RequestParam String path) {
        return ResponseEntity.ok(fileService.get(path));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(@RequestParam String path) {
        fileService.delete(path);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download")
    public ResponseEntity<InputStream> downloadResource(@RequestParam String path) {
        return ResponseEntity.ok(fileService.download(path));
    }

    @GetMapping("/move")
    public ResponseEntity<Resource> moveResource(@RequestParam String from, @RequestParam String to) {
        return ResponseEntity.ok(fileService.move(from, to));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Resource>> searchResource(@RequestParam String query) {
        return ResponseEntity.ok(fileService.search(query));
    }

    @PostMapping
    public ResponseEntity<List<Resource>> uploadResource(
            @RequestParam("path") String path,
            @RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fileService.upload(path, files));
    }
}
