package com.example.cloud_storage.minio.service.file;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.file.FileResponseDto;

import java.util.List;

public interface FileService {
    Resource get(String fileName);
    void delete(String fileName);
    Resource download(String fileName);
    Resource move(String fromFile, String toFile);
    List<Resource> search(String query);
    List<Resource> upload(String fileName);
}
