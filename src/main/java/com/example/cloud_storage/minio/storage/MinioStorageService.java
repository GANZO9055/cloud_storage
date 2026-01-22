package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.exception.minio.*;
import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.FolderResponseDto;
import com.example.cloud_storage.minio.mapper.ResourceMapper;
import com.example.cloud_storage.minio.validation.ValidationResource;
import com.example.cloud_storage.user.security.util.SecurityUtil;
import io.minio.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class MinioStorageService implements StorageService {

    private static final String BUCKET = "user-files";
    private static final String ROOT_FOLDER = "user-%s-files/";

    @Autowired
    private MinioClient minioClient;
    @Autowired
    private ResourceMapper resourceMapper;

    @PostConstruct
    public void initStorage() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(BUCKET)
                            .build()
            );
            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(BUCKET)
                                .build()
                );
            }
            log.info("Bucket {} created successfully", BUCKET);
        } catch (Exception e) {
            throw new StorageException("Cannot create MinIO bucket!");
        }
    }

    @Override
    public Resource get(String path) {
        Resource resource;
        if (!ValidationResource.checkingExistenceResource(BUCKET, path)) {
            log.error("Resource not found (get): {}", path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }
        try {
           StatObjectResponse result =  minioClient.statObject(
                    StatObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(path)
                            .build()
            );

           if (path.endsWith("/")) {
               resource = resourceMapper.toFolder(path, getResourceNameFromPath(path));
           } else {
               resource = resourceMapper.toFile(path, getResourceNameFromPath(path), result.size());
           }
        } catch (Exception e) {
            log.error("Failed to get resource info: {}", path);
            throw new StorageException("Failed to get resource information!");
        }
        return resource;
    }

    @Override
    public void delete(String path) {
        if (!ValidationResource.checkingExistenceResource(BUCKET, path)) {
            log.error("Resource not found (delete): {}", path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }
        try {
            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET)
                            .prefix(path)
                            .recursive(true)
                            .build()
            );
            for (Result<Item> item : items) {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(BUCKET)
                                .object(item.get().objectName())
                                .build()
                );
            }
            log.info("Resource deleted: {}", path);
        } catch (Exception e) {
            log.error("Resource deleted: {}", path);
            throw new StorageException("Failed to delete resource!");
        }
    }

    @Override
    public InputStream download(String path) {
        if (!ValidationResource.checkingExistenceResource(BUCKET, path)) {
            log.error("Resource not found (download): {}", path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }
        if (path.endsWith("/")) {
            return downloadFolder(path);
        }
        return downloadFile(path);
    }

    @Override
    public Resource move(String fromPath, String toPath) {
        if (!ValidationResource.checkingExistenceResource(BUCKET, fromPath)) {
            log.error("Resource not found (move): {}", fromPath);
            throw new ResourceNotFoundException("Resource not found: " + fromPath);
        }
        if (ValidationResource.checkingExistenceResource(BUCKET, toPath)) {
            log.error("Resource already exists (move): {}", toPath);
            throw new ResourceAlreadyExistsException("Resource already exists: " + toPath);
        }
        try {
            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET)
                            .prefix(fromPath)
                            .recursive(true)
                            .build()
            );
            for (Result<Item> item : items) {
                Item result = item.get();

                String newResourceName =
                        toPath + result.objectName().substring(fromPath.length());

                minioClient.copyObject(
                        CopyObjectArgs.builder()
                                .bucket(BUCKET)
                                .object(newResourceName)
                                .source(
                                        CopySource.builder()
                                                .bucket(BUCKET)
                                                .object(result.objectName())
                                                .build()
                                ).build()
                );
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(BUCKET)
                                .object(result.objectName())
                                .build()
                );
            }
            log.info("Resource success move!");
            return get(toPath);
        } catch (Exception e) {
            log.error("Failed to move resource!");
            throw new StorageException("Failed to move resource!");
        }
    }

    @Override
    public List<Resource> search(String query) {
        Iterable<Result<Item>> items = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(BUCKET)
                        .prefix(createRootFolderForUser())
                        .recursive(true)
                        .build()
        );

        List<Resource> resources = new ArrayList<>();

        for (Result<Item> item : items) {
            try {
                Item result = item.get();
                String pathName = result.objectName();
                if (pathName.contains(query)) {
                    resources.add(getResourceFromItem(result));
                }
            } catch (Exception e) {
                log.error("Search failed!");
                throw new StorageException("Search failed!");
            }
        }
        log.info("Search success");
        return resources;
    }

    @Override
    public List<Resource> upload(String path, List<MultipartFile> files) {
        if (!ValidationResource.checkingExistenceResource(BUCKET, path)) {
            log.error("Resource already exists (upload): {}", path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }

        List<Resource> resources = new ArrayList<>();

        for (MultipartFile file : files) {

            String originalName = file.getOriginalFilename();
            String objectName = path + originalName;

            if (ValidationResource.checkingExistenceResource(BUCKET, objectName)) {
                log.error("Resource already exists (upload): {}", objectName);
                throw new ResourceAlreadyExistsException("File already exists: " + objectName);
            }
            try {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(BUCKET)
                                .object(objectName)
                                .stream(
                                        file.getInputStream(),
                                        file.getSize(),
                                        -1
                                )
                                .contentType(file.getContentType())
                                .build()
                );
            } catch (IOException e) {
                log.error("Failed read file!");
                throw new StorageException("Failed read file!");
            } catch (Exception e) {
                log.error("Upload failed!");
                throw new StorageException("Upload failed!");
            }

            Resource resource = resourceMapper.toFile(
                    path,
                    getResourceNameFromPath(objectName),
                    file.getSize()
            );

            resources.add(resource);
        }
        log.info("Resource success upload");
        return resources;
    }

    @Override
    public void createRootFolder() {
        String rootFolder = createRootFolderForUser();
        createFolder(rootFolder);
    }

    @Override
    public FolderResponseDto createFolder(String path) {
        FolderResponseDto dto;
        try {
            if (!(path.contains("user-") && path.contains("-files/"))) {
                if (!ValidationResource.checkingExistenceResource(BUCKET, createRootFolderForUser())) {
                    log.error("Parent folder not found!");
                    throw new ParentFolderNotFoundException("Parent folder not found!");
                }
            }
            if (ValidationResource.checkingExistenceResource(BUCKET, path)) {
                log.error("Folder already exists: {}", path);
                throw new FolderAlreadyExistsException("Folder already exists: " + path);
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(path)
                            .stream(
                                    new ByteArrayInputStream(new byte[0]),
                                    0,
                                    -1
                            )
                            .build()
            );

            dto = resourceMapper.toFolder(path, getResourceNameFromPath(path));
            log.info("Folder success create");
        } catch (Exception e) {
            throw new StorageException("Error when creating folder");
        }
        return dto;
    }

    @Override
    public List<Resource> getFolderContents(String path) {
        if (!ValidationResource.checkingExistenceResource(BUCKET, path)) {
            log.error("Folder not found: {}", path);
            throw new FolderNotFoundException("Folder not found: " + path);
        }
        List<Resource> resources = new ArrayList<>();

        Iterable<Result<Item>> items = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(BUCKET)
                        .prefix(path)
                        .delimiter("/")
                        .recursive(false)
                        .build()
        );

        for (Result<Item> result : items) {
            try {
                resources.add(getResourceFromItem(result.get()));
            } catch (Exception e) {
                log.error("Failed to get resource!");
                throw new StorageException("Failed to get resource!");
            }
        }
        log.info("Contents folder success get");
        return resources;
    }

    private InputStream downloadFolder(String path) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ZipOutputStream zipOutputStream = new ZipOutputStream(baos);

            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET)
                            .prefix(path)
                            .recursive(true)
                            .build()
            );
            for (Result<Item> result : items) {
                Item item = result.get();
                if (item.isDir()) {
                    continue;
                }
                String zipEntryName = item.objectName().substring(path.length());
                zipOutputStream.putNextEntry(new ZipEntry(zipEntryName));

                InputStream inputStream = minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(BUCKET)
                                .object(item.objectName())
                                .build()
                );
                inputStream.transferTo(zipOutputStream);
                zipOutputStream.closeEntry();
            }
            zipOutputStream.close();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception e) {
            log.error("Failed download folder!");
            throw new StorageException("Failed download folder!");
        }
    }

    private InputStream downloadFile(String path) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(path)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed download file!");
            throw new StorageException("Failed download file!");
        }
    }

    private String createRootFolderForUser() {
        String number = SecurityUtil.getCurrentUserId().toString();
        return String.format(ROOT_FOLDER, number);
    }

    private String getResourceNameFromPath(String folderName) {
        if (folderName.endsWith("/")) {
            folderName = folderName.substring(0, folderName.length() - 1);
        }
        int index = folderName.lastIndexOf('/');
        if (index >= 0) {
            return folderName.substring(index + 1);
        }
        return folderName;
    }

    private Resource getResourceFromItem(Item item) {
        Resource resource;
        String path = item.objectName();
        String name = getResourceNameFromPath(path);

        if (item.isDir()) {
             resource = resourceMapper.toFolder(path, name);
        } else {
            resource = resourceMapper.toFile(path, name, item.size());
        }
        return resource;
    }
}
