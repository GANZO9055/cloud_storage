package com.example.cloud_storage.minio.service.directory;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;
import com.example.cloud_storage.minio.storage.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FolderServiceImpl implements FolderService {

    private StorageService storageService;

    @Override
    public DirectoryResponseDto createFolder(String folderName) {
        return storageService.createFolder(folderName);
    }

    @Override
    public List<Resource> getFolderContents(String folderName) {
        return storageService.getResourceContents(folderName);
    }
}
