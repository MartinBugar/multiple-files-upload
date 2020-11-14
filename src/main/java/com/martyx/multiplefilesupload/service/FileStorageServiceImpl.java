package com.martyx.multiplefilesupload.service;


import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final Path root = Paths.get("uploads"); // URL adress to uploaded files


    @Override
    public void init() {
        try {
            Files.createDirectory(root); // vytvori adresa uploads
        }catch (IOException ex){
            throw new RuntimeException("Could not initialize folder for upload");
        }
    }

    @Override
    public void save(MultipartFile multipartFile) {
        try{
            Files.copy(multipartFile.getInputStream(),this.root.resolve(multipartFile.getOriginalFilename())); // skopiruje subor z input streamu do rootu
        } catch (Exception ex){
            throw new RuntimeException("Could not store the file. Error : " + ex.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try{
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file");
            }
        } catch (MalformedURLException ex){
            throw new RuntimeException("Error : " + ex.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try{
            return Files.walk(this.root,1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e){
            throw new RuntimeException("Could not load the files");
        }
    }
}
