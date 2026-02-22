package com.example.cloud_storage.controller.minio;

import com.example.cloud_storage.api.FolderControllerApi;
import com.example.cloud_storage.dto.Resource;
import com.example.cloud_storage.dto.response.FolderResponseDto;
import com.example.cloud_storage.service.minio.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class FolderController implements FolderControllerApi {

    private ResourceService resourceService;

    @PostMapping
    public ResponseEntity<FolderResponseDto> createFolder(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        return new ResponseEntity<>(
                resourceService.createFolder(path),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<Resource>> getFolderContents(
            @RequestParam
            @Size(max = 500)
            @NotBlank
            String path) {
        return new ResponseEntity<>(
                resourceService.getFolderContents(path),
                HttpStatus.OK
        );
    }
}
