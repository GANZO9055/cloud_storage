package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;

import java.util.List;

public interface StorageService extends ResourceStorageService {
    void createRootFolder();
    DirectoryResponseDto createFolder(String path);
    List<Resource> getFolderContents(String path);
}
