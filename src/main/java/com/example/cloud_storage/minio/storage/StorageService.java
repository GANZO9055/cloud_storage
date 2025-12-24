package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;

import java.util.List;

public interface StorageService {
    void userRoot(String firstFolderName);
    DirectoryResponseDto createFolder(String folderName);
    List<Resource> getResourceContents(String folderName);
}
