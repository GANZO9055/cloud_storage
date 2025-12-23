package com.example.cloud_storage.minio.service.directory;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;

import java.util.List;

public interface DirectoryService {
    DirectoryResponseDto createDirectory(String folderName);
    List<Resource> getResource(String folderName);
}
