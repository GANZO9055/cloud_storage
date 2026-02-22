package com.example.cloud_storage.storage;

import io.minio.Result;
import io.minio.messages.Item;

public interface FolderStorageService {
    void createEmptyFolder(String path);
    Iterable<Result<Item>> getFolderContents(String path);
}
