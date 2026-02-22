package com.example.cloud_storage.minio.repository;

import com.example.cloud_storage.minio.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    boolean existsByUserIdAndPath(Integer userId, String path);
    List<FileMetadata> findAllByUserIdAndPathStartingWith(Integer userId, String startingPath);
}
