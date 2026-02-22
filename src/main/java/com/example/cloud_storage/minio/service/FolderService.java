package com.example.cloud_storage.minio.service;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.FolderResponseDto;

import java.util.List;

public interface FolderService {
    void createRootFolder(Integer id);
    FolderResponseDto createFolder(String path);
    List<Resource> getFolderContents(String path);
}
