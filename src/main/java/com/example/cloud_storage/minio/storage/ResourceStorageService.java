package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import io.minio.Result;
import io.minio.messages.Item;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface ResourceStorageService {
    Iterable<Result<Item>> get(String path);
    void delete(String path);
    InputStream download(String path);
    void move(String fromPath, String toPath);
    void upload(String path, MultipartFile file);
}
