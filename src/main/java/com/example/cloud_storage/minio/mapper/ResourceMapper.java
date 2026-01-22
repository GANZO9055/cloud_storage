package com.example.cloud_storage.minio.mapper;

import com.example.cloud_storage.minio.dto.directory.FolderResponseDto;
import com.example.cloud_storage.minio.dto.file.FileResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(target = "type", constant = "FILE")
    FileResponseDto toFile(String path, String name, Long size);

    @Mapping(target = "type", constant = "DIRECTORY")
    FolderResponseDto toFolder(String path, String name);
}
