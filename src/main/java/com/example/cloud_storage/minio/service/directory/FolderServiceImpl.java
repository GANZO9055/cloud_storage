package com.example.cloud_storage.minio.service.directory;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.FolderResponseDto;
import com.example.cloud_storage.minio.storage.StorageService;
import com.example.cloud_storage.minio.validation.ValidationResource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FolderServiceImpl implements FolderService {

    private StorageService storageService;
    private ValidationResource validationResource;

    @Override
    public FolderResponseDto createFolder(String folderName) {
        validationResource.checkingPath(folderName);
        return storageService.createFolder(folderName);
    }

    @Override
    public List<Resource> getFolderContents(String folderName) {
        return storageService.getFolderContents(folderName);
    }
}
