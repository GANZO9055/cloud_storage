package com.example.cloud_storage.minio.validator;

import com.example.cloud_storage.minio.repository.FileMetadataRepository;
import com.example.cloud_storage.user.util.UserUtil;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MinioValidatorResource {

    private FileMetadataRepository metadataRepository;

    public boolean checkingExistenceResource(String path) {
        return metadataRepository.existsByUserIdAndPath(UserUtil.getId(), path);
    }
}
