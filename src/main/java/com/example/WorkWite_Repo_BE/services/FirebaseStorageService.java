package com.example.WorkWite_Repo_BE.services;

import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

@Service
@Slf4j
public class FirebaseStorageService {

    // Upload file resume
    public String uploadFile(MultipartFile file) {
        try {
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Bucket bucket = StorageClient.getInstance().bucket();

            Blob blob = bucket.create(filename, file.getBytes(), file.getContentType());
            log.info("Upload resume lên Firebase thành công: {}", filename);

            return String.format(
                    "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucket.getName(),
                    blob.getName().replace("/", "%2F")
            );
        } catch (IOException e) {
            log.error("Lỗi upload Firebase", e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload resume thất bại");
        }
    }
    public String uploadPdf(byte[] pdfBytes, String filename) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.create(filename, pdfBytes, "application/pdf");
            return String.format(
                    "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucket.getName(),
                    blob.getName().replace("/", "%2F")
            );
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Upload PDF thất bại");
        }
    }


    // filename là tên file, không phải URL đầy đủ
    public Resource downloadFile(String filename) {
        try {
            String bucketName = StorageClient.getInstance().bucket().getName();
            String url = String.format(
                    "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                    bucketName,
                    filename.replace("/", "%2F")
            );
            return new UrlResource(url);
        } catch (MalformedURLException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Không thể tải file");
        }
    }
    public byte[] downloadFileAsBytes(String filename) throws IOException {
        // Giả sử bạn đã khởi tạo Firebase Storage
        Storage storage = StorageOptions.getDefaultInstance().getService();
        Blob blob = storage.get(BlobId.of("your-bucket-name", filename));
        return blob.getContent();
    }
    // Xóa resume trong Firebase
    public void deleteFile(String filename) {
        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.get(filename);
        if (blob != null && blob.delete()) {
            log.info("Đã xóa file {} khỏi Firebase", filename);
        } else {
            log.warn("File {} không tồn tại trong Firebase", filename);
        }
    }

}
