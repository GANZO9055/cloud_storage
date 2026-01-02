package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.Type;
import com.example.cloud_storage.minio.dto.directory.DirectoryResponseDto;
import com.example.cloud_storage.minio.dto.file.FileResponseDto;
import com.example.cloud_storage.minio.exception.FolderAlreadyExistsException;
import com.example.cloud_storage.minio.exception.FolderNotFoundException;
import com.example.cloud_storage.minio.exception.ParentFolderNotFoundException;
import com.example.cloud_storage.minio.exception.StorageException;
import com.example.cloud_storage.minio.validation.ValidationResource;
import com.example.cloud_storage.user.security.util.SecurityUtil;
import io.minio.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

@Service
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
        } catch (Exception e) {
            throw new StorageException("Cannot create MinIO bucket!");
        }
    }

    @Override
    public void createRootFolder() {
        String rootFolder = createRootFolderForUser();
        createFolder(rootFolder);
    }

    @Override
    public DirectoryResponseDto createFolder(String folderName) {
        DirectoryResponseDto dto;
        try {
            if (!(folderName.contains("user-") && folderName.contains("-files/"))) {
                if (!ValidationResource.checkingExistenceResource(BUCKET, createRootFolderForUser())) {
                    throw new ParentFolderNotFoundException("Parent folder not found!");
                }
                if (ValidationResource.checkingExistenceResource(BUCKET, folderName)) {
                    throw new FolderAlreadyExistsException("Folder already exists: " + folderName);
                }
            }
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(folderName)
                            .stream(
                                    new ByteArrayInputStream(new byte[0]),
                                    0,
                                    -1
                            )
                            .build()
            );

            dto = new DirectoryResponseDto(
                    folderName,
                    getFolderNameFromPath(folderName),
                    Type.DIRECTORY
            );
        } catch (Exception e) {
            throw new StorageException("Server error!");
        }
        return dto;
    }

    @Override
    public List<Resource> getFolderContents(String folderName) {
        if (!ValidationResource.checkingExistenceResource(BUCKET, folderName)) {
            throw new FolderNotFoundException("Folder not found: " + folderName);
        }
        Iterable<Result<Item>> items = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(BUCKET)
                        .prefix(folderName)
                        .delimiter("/")
                        .recursive(false)
                        .build()
        );
        return getResourcesFromItems(items);
    }

    private String createRootFolderForUser() {
        String number = SecurityUtil.getCurrentUserId().toString();
        return String.format(ROOT_FOLDER, number);
    }

    private String getFolderNameFromPath(String folderName) {
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
                String name = getFolderNameFromPath(path);

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
                throw new StorageException("Server error!");
            }
        }
        return resources;
    }
}
