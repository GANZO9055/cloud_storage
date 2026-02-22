package com.example.cloud_storage.service.minio;

import com.example.cloud_storage.dto.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface FileService {
    Resource get(String path);
    void delete(String path);
    InputStream download(String path);
    Resource move(String fromPath, String toPath);
    List<Resource> search(String query);
    List<Resource> upload(String path, List<MultipartFile> files);
}
