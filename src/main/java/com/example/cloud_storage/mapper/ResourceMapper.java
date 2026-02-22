package com.example.cloud_storage.mapper;

import com.example.cloud_storage.dto.Type;
import com.example.cloud_storage.dto.response.FolderResponseDto;
import com.example.cloud_storage.dto.response.FileResponseDto;
import com.example.cloud_storage.model.FileMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(target = "type", constant = "FILE")
    FileResponseDto toFile(String path, String name, Long size);

    @Mapping(target = "type", constant = "DIRECTORY")
    FolderResponseDto toFolder(String path, String name);

    FileMetadata toFileMetadata(Integer userId, String path, String name, Long size, Type type);
}
