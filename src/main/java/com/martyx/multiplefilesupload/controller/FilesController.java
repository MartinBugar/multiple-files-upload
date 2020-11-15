package com.martyx.multiplefilesupload.controller;

import com.martyx.multiplefilesupload.message.ResponseMessage;
import com.martyx.multiplefilesupload.model.FileInfo;
import com.martyx.multiplefilesupload.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@CrossOrigin("http://localhost:8081")
public class FilesController {

    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("/upload")
    public ResponseEntity <ResponseMessage> uploadFiles (@RequestParam("file") MultipartFile[] files){
        String message = "";

        try {
            List <String> fileNames = new ArrayList<>(); //vytvorim si List kde budem uchovavam mena suborov

            Arrays.asList(files).stream().forEach(file -> { // nahrane subory prezeniem streamom cez metodu ktora mi ich ulozi
                fileStorageService.save(file);
                fileNames.add(file.getOriginalFilename()); // ulozim mena po jednom zo streamu do mojho Listu
            });

            message = "Uploaded the files successfuly : " + fileNames;
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseMessage(message));
        } catch (Exception ex){
            message = "Fail to upload files";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(new ResponseMessage(message));
        }
    }

    @GetMapping("/files")
    public ResponseEntity<List<FileInfo>> getListFiles (){
        List <FileInfo> fileInfos = fileStorageService.loadAll().map(path -> { // prechadza elementy po jednom
            String filename = path.getFileName().toString(); // vrati mi meno elementu
            String url = MvcUriComponentsBuilder // vrati mi url elementu
                    .fromMethodName(FilesController.class, "getFile", path.getFileName().toString()).build().toString();

            return new FileInfo(filename, url); // vytvori mi novy objekt, ktory bude ulozeny do Listu
        }).collect(Collectors.toList()); // zbali to cele do Listu

        return  ResponseEntity.status(HttpStatus.OK).body(fileInfos); // v tele mi vracia List fileInfos kde su zbalene informacie o vsetkych elementoch
    }


    @GetMapping("/files/{filename:.+}")
    public ResponseEntity<Resource> getFile (@PathVariable String filename) {
        Resource file = fileStorageService.load(filename);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"").body(file);
    }

}
