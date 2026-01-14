package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;

import java.io.InputStream;
import java.util.List;

public interface ResourceStorageService {
    Resource get(String path);
    void delete(String path);
    InputStream download(String path);
    Resource move(String fromPath, String toPath);
    List<Resource> search(String query);
    List<Resource> upload(String path);
}
