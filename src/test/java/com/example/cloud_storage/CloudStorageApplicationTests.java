package com.example.cloud_storage;

import com.example.cloud_storage.storage.MinioStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ActiveProfiles("test")
class CloudStorageApplicationTests {

	@MockitoBean
	private MinioStorageService minioStorageService;

	@Test
	void contextLoads() {
	}

}
