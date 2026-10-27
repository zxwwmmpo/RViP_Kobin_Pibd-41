package com.example.storage.controller;

import com.example.storage.dto.FileUploadResponse;
import com.example.storage.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final FileStorageService storageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> upload(@RequestPart("file") MultipartFile file) {
        log.info("получен запрос на сохранение CSV: size={}", file.getSize());

        FileUploadResponse response = storageService.save(file);

        return ResponseEntity.created(URI.create("/api/files/" + response.id())).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> download(@PathVariable("id") UUID id) {
        log.info("получен запрос на скачивание CSV: id={}", id);

        Resource resource = storageService.load(id);
        String disposition = ContentDisposition.attachment().filename(id + ".csv").build().toString();

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition)
                .body(resource);
    }
}