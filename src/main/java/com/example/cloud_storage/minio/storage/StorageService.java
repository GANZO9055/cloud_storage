package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;

import java.util.List;

public interface StorageService {
    void createRootFolder();
    DirectoryResponseDto createFolder(String folderName);
    List<Resource> getFolderContents(String folderName);
}
