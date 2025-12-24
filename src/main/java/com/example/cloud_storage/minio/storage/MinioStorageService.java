package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MinioStorageService implements StorageService {

    @Autowired
    private MinioClient minioClient;
    private String firstFolderName;

    @Override
    public void userRoot(String firstFolderName) {
        this.firstFolderName = firstFolderName;
        init();
    }

    public void init() {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .object(firstFolderName)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DirectoryResponseDto createFolder(String folderName) {
        return null;
    }

    @Override
    public List<Resource> getResourceContents(String folderName) {
        return List.of();
    }
}
