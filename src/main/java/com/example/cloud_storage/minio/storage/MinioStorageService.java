package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.Type;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;
import com.example.cloud_storage.minio.dto.file.FileResponseDto;
import com.example.cloud_storage.minio.exception.*;
import com.example.cloud_storage.minio.validation.ValidationResource;
import com.example.cloud_storage.user.security.util.SecurityUtil;
import io.minio.*;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
               resource = new DirectoryResponseDto(
                       path,
                       getResourceNameFromPath(path),
                       Type.DIRECTORY
               );
           } else {
               resource = new FileResponseDto(
                       path,
                       getResourceNameFromPath(path),
                       result.size(),
                       Type.FILE
               );
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
            throw new ResourceNotFoundException("Resource not found: " + fromPath);
        }
        if (ValidationResource.checkingExistenceResource(BUCKET, toPath)) {
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
            return get(toPath);
        } catch (Exception e) {
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
                    if (result.isDir()) {
                        resources.add(new DirectoryResponseDto(
                                pathName,
                                getResourceNameFromPath(pathName),
                                Type.DIRECTORY
                        ));
                    } else {
                        resources.add(new FileResponseDto(
                                pathName,
                                getResourceNameFromPath(pathName),
                                result.size(),
                                Type.FILE
                        ));
                    }
                }
            } catch (Exception e) {
                throw new StorageException("Search failed!");
            }
        }
        return resources;
    }

    @Override
    public List<Resource> upload(String path) {
        return List.of();
    }

    @Override
    public void createRootFolder() {
        String rootFolder = createRootFolderForUser();
        createFolder(rootFolder);
    }

    @Override
    public DirectoryResponseDto createFolder(String path) {
        DirectoryResponseDto dto;
        try {
            if (!(path.contains("user-") && path.contains("-files/"))) {
                if (!ValidationResource.checkingExistenceResource(BUCKET, createRootFolderForUser())) {
                    throw new ParentFolderNotFoundException("Parent folder not found!");
                }
                if (ValidationResource.checkingExistenceResource(BUCKET, path)) {
                    throw new FolderAlreadyExistsException("Folder already exists: " + path);
                }
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

            dto = new DirectoryResponseDto(
                    path,
                    getResourceNameFromPath(path),
                    Type.DIRECTORY
            );
        } catch (Exception e) {
            throw new StorageException("Server error!");
        }
        return dto;
    }

    @Override
    public List<Resource> getFolderContents(String path) {
        if (!ValidationResource.checkingExistenceResource(BUCKET, path)) {
            throw new FolderNotFoundException("Folder not found: " + path);
        }
        Iterable<Result<Item>> items = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(BUCKET)
                        .prefix(path)
                        .delimiter("/")
                        .recursive(false)
                        .build()
        );
        return getResourcesFromItems(items);
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

    private List<Resource> getResourcesFromItems(Iterable<Result<Item>> items) {
        List<Resource> resources = new ArrayList<>();

        for (Result<Item> result : items) {
            try {
                Item item = result.get();
                String path = item.objectName();
                String name = getResourceNameFromPath(path);

                if (item.isDir()) {
                    resources.add(
                            new DirectoryResponseDto(
                                    path,
                                    name,
                                    Type.DIRECTORY
                            )
                    );
                } else {
                    resources.add(
                            new FileResponseDto(
                                    path,
                                    name,
                                    item.size(),
                                    Type.FILE
                            )
                    );
                }
            } catch (Exception e) {
                log.error("Failed to get resources!");
                throw new StorageException("Server error!");
            }
        }
        return resources;
    }
}
