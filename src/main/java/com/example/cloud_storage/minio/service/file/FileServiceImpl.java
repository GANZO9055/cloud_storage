package com.example.cloud_storage.minio.service.file;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.storage.StorageService;
import com.example.cloud_storage.minio.validation.ValidationResource;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {

    private StorageService storageService;
    private ValidationResource validationResource;

    @Override
    public Resource get(String path) {
        validationResource.checkingPath(path);
        return storageService.get(path);
    }

    @Override
    public void delete(String path) {
        validationResource.checkingPath(path);
        storageService.delete(path);
    }

    @Override
    public InputStream download(String path) {
        validationResource.checkingPath(path);
        return storageService.download(path);
    }

    @Override
    public Resource move(String fromPath, String toPath) {
        validationResource.checkingPath(toPath);
        return storageService.move(fromPath, toPath);
    }

    @Override
    public List<Resource> search(String query) {
        validationResource.checkingPath(query);
        return storageService.search(query);
    }

    @Override
    public List<Resource> upload(String path, List<MultipartFile> files) {
        validationResource.checkingPath(path);
        return storageService.upload(path, files);
    }
}
