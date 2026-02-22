package com.example.cloud_storage.validator;

import com.example.cloud_storage.repository.FileMetadataRepository;
import com.example.cloud_storage.util.UserUtil;
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
