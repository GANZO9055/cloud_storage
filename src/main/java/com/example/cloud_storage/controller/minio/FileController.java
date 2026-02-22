package com.example.cloud_storage.controller.minio;

import com.example.cloud_storage.api.FileControllerApi;
import com.example.cloud_storage.dto.Resource;
import com.example.cloud_storage.service.minio.ResourceService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/resource")
@AllArgsConstructor
public class FileController implements FileControllerApi {

    private ResourceService resourceService;

    @GetMapping
    public ResponseEntity<Resource> getResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        return ResponseEntity.ok(resourceService.get(path));
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        resourceService.delete(path);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/download")
    public ResponseEntity<StreamingResponseBody> downloadResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        InputStream inputStream = resourceService.download(path);

        StreamingResponseBody stream = outputStream -> {
            try (inputStream) {
                inputStream.transferTo(outputStream);
            }
        };
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(stream);
    }

    @PutMapping("/move")
    public ResponseEntity<Resource> moveResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank String from,
            @RequestParam
            @Size(max = 500)
            @NotBlank String to) {
        return ResponseEntity.ok(resourceService.move(from, to));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Resource>> searchResource(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String query) {
        return ResponseEntity.ok(resourceService.search(query));
    }

    @PostMapping
    public ResponseEntity<List<Resource>> uploadResource(
            @RequestParam("path")
            @Size(max = 500)
            @NotBlank String path,
            @RequestParam("object") List<MultipartFile> files) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.upload(path, files));
    }
}
