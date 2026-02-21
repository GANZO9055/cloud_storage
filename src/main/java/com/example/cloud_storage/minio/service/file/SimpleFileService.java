package com.example.cloud_storage.minio.service.file;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.storage.StorageService;
import com.example.cloud_storage.minio.validator.ValidatorPath;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
@AllArgsConstructor
public class SimpleFileService implements FileService {

    private StorageService storageService;
    private ValidatorPath validatorPath;

    @Override
    public Resource get(String path) {
        validatorPath.checkingPath(path);
        return storageService.get(path);
    }

    @Override
    public void delete(String path) {
        validatorPath.checkingPath(path);
        storageService.delete(path);
    }

    @Override
    public InputStream download(String path) {
        validatorPath.checkingPath(path);
        return storageService.download(path);
    }

    @Override
    public Resource move(String fromPath, String toPath) {
        validatorPath.checkingPath(toPath);
        return storageService.move(fromPath, toPath);
    }

    @Override
    public List<Resource> search(String query) {
        validatorPath.checkingPath(query);
        return storageService.search(query);
    }

    @Override
    public List<Resource> upload(String path, List<MultipartFile> files) {
        validatorPath.checkingPath(path);
        return storageService.upload(path, files);
    }
}
