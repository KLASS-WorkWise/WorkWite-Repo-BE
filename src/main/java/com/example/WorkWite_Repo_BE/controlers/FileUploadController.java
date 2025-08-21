package com.example.WorkWite_Repo_BE.controlers;

import com.example.WorkWite_Repo_BE.entities.FileInfo;
import com.example.WorkWite_Repo_BE.repositories.FileInfoRepository;
import com.example.WorkWite_Repo_BE.repositories.UserJpaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import java.util.Map;
import java.util.UUID;

@RestController

@RequestMapping("/api/upload")
public class FileUploadController {

    @Autowired
    private FileInfoRepository fileInfoRepository;
    private UserJpaRepository userJpaRepository;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(file.getOriginalFilename());
            Files.write(filePath, file.getBytes());

            FileInfo info = new FileInfo();
            info.setFilename(file.getOriginalFilename());
            info.setFilepath(filePath.toString());
            info.setFileType(file.getContentType());
            info.setUploadTime(LocalDateTime.now());
            fileInfoRepository.save(info);

            return ResponseEntity.ok("Upload thành công: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Lỗi upload file: " + e.getMessage());
        }
    }

    @PostMapping("/multiple")
    public ResponseEntity<String> uploadMultiple(@RequestParam("files") MultipartFile[] files) {
        StringBuilder result = new StringBuilder();
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                return ResponseEntity.status(500).body("Lỗi tạo thư mục: " + e.getMessage());
            }
        }
        for (MultipartFile file : files) {
            try {
                Path filePath = uploadPath.resolve(file.getOriginalFilename());
                Files.write(filePath, file.getBytes());

                FileInfo info = new FileInfo();
                info.setFilename(file.getOriginalFilename());
                info.setFilepath(filePath.toString());
                info.setFileType(file.getContentType());
                info.setUploadTime(LocalDateTime.now());
                fileInfoRepository.save(info);

                result.append("Uploaded: ").append(file.getOriginalFilename()).append("\n");
            } catch (IOException e) {
                result.append("Lỗi: ").append(file.getOriginalFilename()).append(" - ").append(e.getMessage())
                        .append("\n");
            }
        }
        return ResponseEntity.ok(result.toString());
    }

}