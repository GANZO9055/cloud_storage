package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;

import java.util.List;

public interface StorageService {
    DirectoryResponseDto create(String folderName);
    List<Resource> getResource(String folderName);
}
