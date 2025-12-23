package com.example.cloud_storage.minio.service.directory;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;
import com.example.cloud_storage.minio.storage.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DirectoryServiceImpl implements DirectoryService {

    private StorageService storageService;

    @Override
    public DirectoryResponseDto createDirectory(String folderName) {
        return storageService.create(folderName);
    }

    @Override
    public List<Resource> getResource(String folderName) {
        return storageService.getResource(folderName);
    }
}
