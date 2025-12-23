package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinioStorageService implements StorageService {

    @Autowired
    private MinioClient minioClient;

    @Override
    public DirectoryResponseDto create(String folderName) {
        return null;
    }

    @Override
    public List<Resource> getResource(String folderName) {
        return List.of();
    }
}
