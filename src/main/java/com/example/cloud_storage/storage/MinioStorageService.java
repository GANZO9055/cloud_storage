package com.example.cloud_storage.storage;

import com.example.cloud_storage.exception.minio.*;
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
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
@AllArgsConstructor
public class MinioStorageService implements StorageService {

    private static final String BUCKET = "user-files";


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
    public Iterable<Result<Item>> get(String path) {
            return minioClient.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(BUCKET)
                            .prefix(path)
                            .recursive(false)
                            .build()
            );
    }

    @Override
    public void delete(String path) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(path)
                            .build()
            );
            log.info("Resource deleted: {}", path);
        } catch (Exception e) {
            log.error("Failed to delete resource: {}", path);
            throw new StorageException("Failed to delete resource!");
        }
    }

    @Override
    public InputStream download(String path) {
        if (path.endsWith("/")) {
            return downloadFolder(path);
        }
        return downloadFile(path);
    }

    @Override
    public void move(String fromPath, String toPath) {
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
        } catch (Exception e) {
            log.error("Failed to move resource!");
            throw new StorageException("Failed to move resource!");
        }
    }

    @Override
    public void upload(String path, MultipartFile file) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(BUCKET)
                            .object(path)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(),
                                    -1
                            )
                            .contentType(file.getContentType())
                            .headers(Map.of("If-None-Match", "*"))
                            .build()
            );
        } catch (IOException e) {
            log.error("Failed read file!");
            throw new StorageException("Failed read file!");
        } catch (Exception e) {
            log.error("Upload failed!");
            throw new StorageException("Upload failed!");
        }
    }

    @Override
    public Iterable<Result<Item>> getFolderContents(String path) {
        return minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(BUCKET)
                        .prefix(path)
                        .delimiter("/")
                        .recursive(false)
                        .build()
        );
    }

    @Override
    public void createEmptyFolder(String path) {
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
}
