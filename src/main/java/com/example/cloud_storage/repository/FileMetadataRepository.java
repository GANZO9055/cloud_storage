package com.example.cloud_storage.repository;

import com.example.cloud_storage.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
    boolean existsByUserIdAndPath(Integer userId, String path);
    List<FileMetadata> findAllByUserIdAndPathStartingWith(Integer userId, String startingPath);
}
