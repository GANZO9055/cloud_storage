package com.example.cloud_storage.service.minio;

import com.example.cloud_storage.dto.Resource;
import com.example.cloud_storage.dto.response.FolderResponseDto;

import java.util.List;

public interface FolderService {
    void createRootFolder(Integer id);
    FolderResponseDto createFolder(String path);
    List<Resource> getFolderContents(String path);
}
