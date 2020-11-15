package com.martyx.multiplefilesupload;

import com.martyx.multiplefilesupload.service.FileStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

@SpringBootApplication
public class MultipleFilesUploadApplication implements CommandLineRunner {

	@Resource
	FileStorageService fileStorageService;

	public static void main(String[] args) {
		SpringApplication.run(MultipleFilesUploadApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		fileStorageService.deleteAll();
		fileStorageService.init();
	}
}
