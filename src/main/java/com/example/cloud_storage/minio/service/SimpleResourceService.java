package com.example.cloud_storage.minio.service;

import com.example.cloud_storage.exception.minio.*;
import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.Type;
import com.example.cloud_storage.minio.dto.directory.FolderResponseDto;
import com.example.cloud_storage.minio.mapper.ResourceMapper;
import com.example.cloud_storage.minio.model.FileMetadata;
import com.example.cloud_storage.minio.repository.FileMetadataRepository;
import com.example.cloud_storage.minio.storage.StorageService;
import com.example.cloud_storage.minio.validator.MinioValidatorResource;
import com.example.cloud_storage.user.util.UserUtil;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class SimpleResourceService implements ResourceService {

    private static final String ROOT_FOLDER = "user-%s-files/";

    private StorageService storageService;
    private ResourceMapper resourceMapper;
    private MinioValidatorResource minioValidatorResource;
    private FileMetadataRepository metadataRepository;

    @Override
    public void createRootFolder(Integer id) {
        String rootFolder = String.format(ROOT_FOLDER, id);
        storageService.createEmptyFolder(rootFolder);
    }

    @Override
    public FolderResponseDto createFolder(String path) {
        String newPath = getFullPath(path);

        if (!(newPath.contains("user-") && newPath.contains("-files/"))) {
            log.error("Parent folder not found!");
            throw new ParentFolderNotFoundException("Parent folder not found!");
        }
        if (minioValidatorResource.checkingExistenceResource(path)) {
            log.error("Folder already exists: {}", newPath);
            throw new FolderAlreadyExistsException("Folder already exists: " + path);
        }

        storageService.createEmptyFolder(newPath);
        metadataRepository.save(
                resourceMapper.toFileMetadata(
                        UserUtil.getId(),
                        path,
                        getResourceNameFromPath(newPath),
                        0L,
                        Type.DIRECTORY
                )
        );

        FolderResponseDto dto = resourceMapper.toFolder(path, getResourceNameFromPath(newPath));
        log.info("Folder success create");

        return dto;
    }

    @Override
    public List<Resource> getFolderContents(String path) {
        String newPath = getFullPath(path);
        if (!minioValidatorResource.checkingExistenceResource(path)) {
            log.error("Folder not found: {}", newPath);
            throw new FolderNotFoundException("Folder not found: " + path);
        }
        List<Resource> resources = new ArrayList<>();

        Iterable<Result<Item>> items = storageService.getFolderContents(newPath);

        for (Result<Item> result : items) {
            try {
                Item item = result.get();
                if (item.objectName().equals(newPath)) {
                    continue;
                }
                resources.add(getResourceFromItem(item));
            } catch (Exception e) {
                log.error("Failed to get resource!");
                throw new StorageException("Failed to get resource!");
            }
        }
        log.info("Contents folder success get");
        return resources;
    }

    @Override
    public Resource get(String path) {
        if (!minioValidatorResource.checkingExistenceResource(path)) {
            log.error("Resource not found (get): {}", path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }

        try {
            var items = storageService.get(path);

            for (Result<Item> itemResult : items) {
                Item result = itemResult.get();
                if (!path.endsWith("/")) {
                    return resourceMapper.toFile(path, getResourceNameFromPath(path), result.size());
                }
            }
            return resourceMapper.toFolder(path, getResourceNameFromPath(path));
        } catch (Exception e) {
            log.error("Failed to get resource info: {}", path);
            throw new StorageException("Failed to get resource information!");
        }
    }

    @Override
    public void delete(String path) {
        if (!minioValidatorResource.checkingExistenceResource(path)) {
            log.error("Resource not found (delete): {}", path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }
        var files = metadataRepository.findAllByUserIdAndPathStartingWith(UserUtil.getId(), path);
        for (FileMetadata fileMetadata : files) {
            String newPath = getFullPath(fileMetadata.getPath());
            storageService.delete(newPath);
        }
        metadataRepository.deleteAll(files);
    }

    @Override
    public InputStream download(String path) {
        String newPath = getFullPath(path);
        if (!minioValidatorResource.checkingExistenceResource(path)) {
            log.error("Resource not found (download): {}", newPath);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }
        return storageService.download(newPath);
    }

    @Override
    public Resource move(String fromPath, String toPath) {
        String newFromPath = getFullPath(fromPath);
        String newToPath = getFullPath(toPath);
        if (!minioValidatorResource.checkingExistenceResource(fromPath)) {
            log.error("Resource not found (move): {}", newFromPath);
            throw new ResourceNotFoundException("Resource not found: " + fromPath);
        }
        if (minioValidatorResource.checkingExistenceResource(toPath)) {
            log.error("Resource already exists (move): {}", newToPath);
            throw new ResourceAlreadyExistsException("Resource already exists: " + toPath);
        }
        storageService.move(newFromPath, newToPath);
        return get(newToPath);
    }

    @Override
    public List<Resource> search(String query) {
        String rootFolder = String.format(ROOT_FOLDER, UserUtil.getId());

        var files = metadataRepository.findAllByUserIdAndPathStartingWith(
                UserUtil.getId(),
                rootFolder
        );

        List<Resource> resources = new ArrayList<>();

        for (FileMetadata fileMetadata : files) {
            if (fileMetadata.getName().contains(query)) {
                resources.add(get(fileMetadata.getPath()));
            }
        }
        log.info("Search success");
        return resources;
    }

    @Override
    public List<Resource> upload(String path, List<MultipartFile> files) {
        String newPath = getFullPath(path);
        if (!minioValidatorResource.checkingExistenceResource(path)) {
            log.error("Resource already exists (upload): {}", newPath);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }

        List<Resource> resources = new ArrayList<>();

        for (MultipartFile file : files) {
            String originalName = file.getOriginalFilename();
            String originalPath = newPath + originalName;

            storageService.upload(originalPath, file);
            metadataRepository.save(
                    resourceMapper.toFileMetadata(
                            UserUtil.getId(),
                            path,
                            getResourceNameFromPath(originalPath),
                            file.getSize(),
                            Type.FILE
                    )
            );

            Resource resource = resourceMapper.toFile(
                    path,
                    getResourceNameFromPath(originalPath),
                    file.getSize()
            );
            resources.add(resource);
        }
        log.info("Resource success upload: {}", path);
        return resources;
    }

    private String getFullPath(String path) {
        String rootFolder = String.format(ROOT_FOLDER, UserUtil.getId());
        return rootFolder + path;
    }

    private String getResourceNameFromPath(String path) {
        String newPath = path;
        if (path.endsWith("/")) {
            newPath = path.substring(0, path.length() - 1);
        }
        int index = newPath.lastIndexOf("/");
        if (index >= 0) {
            newPath = newPath.substring(index + 1);
        }
        return newPath;
    }

    private Resource getResourceFromItem(Item item) {
        String path = item.objectName();

        int slashIndex = path.indexOf("/");
        path = path.substring(slashIndex + 1);

        int index = path.lastIndexOf("/");
        String newPath = path.substring(0, index + 1);

        String name = getResourceNameFromPath(path);

        if (path.endsWith("/")) {
            newPath = newPath.substring(0, path.length() -1);
            int number = newPath.lastIndexOf("/");
            newPath = newPath.substring(0, number + 1);
        }

        if (item.isDir()) {
            return resourceMapper.toFolder(newPath, name + "/");
        }
        return resourceMapper.toFile(newPath, name, item.size());
    }
}
