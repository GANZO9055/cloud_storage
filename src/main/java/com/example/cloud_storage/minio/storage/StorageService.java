package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.FolderResponseDto;

import java.util.List;

public interface StorageService extends ResourceStorageService {
    void createRootFolder();
    FolderResponseDto createFolder(String path);
    List<Resource> getFolderContents(String path);
}
