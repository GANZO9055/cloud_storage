package com.example.cloud_storage.minio.storage;

import com.example.cloud_storage.exception.minio.*;
import com.example.cloud_storage.minio.dto.Resource;
import com.example.cloud_storage.minio.dto.directory.FolderResponseDto;
import com.example.cloud_storage.minio.mapper.ResourceMapper;
import com.example.cloud_storage.minio.validation.ValidationResource;
import io.minio.*;
import io.minio.messages.Item;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@AllArgsConstructor
public class MinioStorageService implements StorageService {

    private static final String BUCKET = "user-files";
    private static final String ROOT_FOLDER = "user-%s-files/";

    private MinioClient minioClient;
    private ResourceMapper resourceMapper;
    private ValidationResource validationResource;

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
        if (!validationResource.checkingExistenceResource(BUCKET, path)) {
            log.error("Resource not found (get): {}", path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }
        try {
            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET)
                            .prefix(path)
                            .recursive(false)
                            .build()
            );
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
        String newPath = deleteCloneFromEndPath(path);
        if (!validationResource.checkingExistenceResource(BUCKET, newPath)) {
            log.error("Resource not found (delete): {}", newPath);
            throw new ResourceNotFoundException("Resource not found: " + newPath);
        }
        try {
            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET)
                            .prefix(newPath)
                            .recursive(true)
                            .build()
            );
            for (Result<Item> item : items) {
                log.info("Info: {}", item.get().objectName());
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(BUCKET)
                                .object(item.get().objectName())
                                .build()
                );
            }
            log.info("Resource deleted: {}", newPath);
        } catch (Exception e) {
            log.error("Resource deleted: {}", newPath);
            throw new StorageException("Failed to delete resource!");
        }
    }

    @Override
    public InputStream download(String path) {
        if (!validationResource.checkingExistenceResource(BUCKET, path)) {
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
        String newFromPath = deleteCloneFromEndPath(fromPath);
        String newToPath = deleteCloneFromHalfPath(toPath);

        if (!validationResource.checkingExistenceResource(BUCKET, newFromPath)) {
            log.error("Resource not found (move): {}", newFromPath);
            throw new ResourceNotFoundException("Resource not found: " + newFromPath);
        }
        if (validationResource.checkingExistenceResource(BUCKET, newToPath)) {
            log.error("Resource already exists (move): {}", newToPath);
            throw new ResourceAlreadyExistsException("Resource already exists: " + newToPath);
        }
        try {
            Iterable<Result<Item>> items = minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET)
                            .prefix(newFromPath)
                            .recursive(true)
                            .build()
            );
            for (Result<Item> item : items) {
                Item result = item.get();

                String newResourceName =
                        newToPath + result.objectName().substring(newFromPath.length());

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
            return get(newToPath);
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
                        .prefix("")
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
        if (!validationResource.checkingExistenceResource(BUCKET, path)) {
            log.error("Resource already exists (upload): {}", path);
            throw new ResourceNotFoundException("Resource not found: " + path);
        }

        List<Resource> resources = new ArrayList<>();

        for (MultipartFile file : files) {

            String originalName = file.getOriginalFilename();
            String objectName = path + originalName;

            if (validationResource.checkingExistenceResource(BUCKET, objectName)) {
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
        log.info("Resource success upload: {}", path);
        return resources;
    }

    @Override
    public void createRootFolder(Integer id) {
        String rootFolder = String.format(ROOT_FOLDER, id);
        createEmptyFolder(rootFolder);
    }

    @Override
    public FolderResponseDto createFolder(String path) {
        String rootFolder = getRootFolderFromPath(path);

        if (!(rootFolder.contains("user-") && rootFolder.contains("-files/"))) {
            log.error("Parent folder not found!");
            throw new ParentFolderNotFoundException("Parent folder not found!");
        }
        if (validationResource.checkingExistenceResource(BUCKET, path)) {
            log.error("Folder already exists: {}", path);
            throw new FolderAlreadyExistsException("Folder already exists: " + path);
        }

        createEmptyFolder(path);

        FolderResponseDto dto = resourceMapper.toFolder(path, getResourceNameFromPath(path));
        log.info("Folder success create");

        return dto;
    }

    @Override
    public List<Resource> getFolderContents(String path) {
        if (!validationResource.checkingExistenceResource(BUCKET, path)) {
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

    private void createEmptyFolder(String path) {
        try {
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
        } catch (Exception e) {
            throw new StorageException("Error when creating folder");
        }
    }

    private String getRootFolderFromPath(String path) {
        int firstSlash = path.indexOf('/');
        return path.substring(0, firstSlash + 1);
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
        Resource resource;
        String path = item.objectName();
        String name = getResourceNameFromPath(path);

        if (item.isDir()) {
             resource = resourceMapper.toFolder(path, name + "/");
        } else {
            resource = resourceMapper.toFile(path, name, item.size());
        }
        return resource;
    }

    private String deleteCloneFromEndPath(String path) {
        log.info("Path before delete clone: {}", path);
        if (path.endsWith("/")) {
            String newPath = path.substring(0, path.length() - 1);
            int index = newPath.lastIndexOf("/");
            String nameResource = newPath.substring(index + 1);
            newPath = newPath.substring(0, index + 1);
            if (newPath.contains(nameResource)) {
                path = newPath;
            }
        } else {
            int slashIndex = path.lastIndexOf("/");
            if (slashIndex >= 0) {
                String parentPath = path.substring(0, slashIndex + 1);
                String fileName = path.substring(slashIndex + 1);

                if (fileName.length() % 2 == 0) {
                    String halfName = fileName.substring(0, fileName.length() / 2);
                    if (halfName.equals(fileName.substring(fileName.length() / 2))) {
                        fileName = halfName;
                    }
                }
                path = parentPath + fileName;
            }
        }
        log.info("Path after delete clone: {}", path);
        return path;
    }

    private String deleteCloneFromHalfPath (String path) {
        String newPath = path;
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        log.info("New path: {}", path);
        int lastSlashIndex = path.lastIndexOf("/");
        String resourceName = path.substring(lastSlashIndex + 1);
        path = path.substring(0, lastSlashIndex + 1);
        log.info("Last new path: {}", path);

        String newToPath = deleteCloneFromEndPath(path);
        newToPath = deleteCloneFromEndPath(newToPath + resourceName);

        if (newPath.endsWith("/")) {
            newToPath = newToPath + "/";
        }
        log.info("New newPath: {}", newToPath);
        return newToPath;
    }
}
