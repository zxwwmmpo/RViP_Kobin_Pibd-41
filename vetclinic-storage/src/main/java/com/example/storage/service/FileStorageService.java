package com.example.storage.service;

import com.example.storage.dto.FileUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import java.nio.file.StandardCopyOption;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.UUID;

@Slf4j
@Service
public class FileStorageService {

    private final Path directory;

    public FileStorageService(@Value("${storage.directory}") String directory) throws IOException {
        this.directory = Path.of(directory).toAbsolutePath().normalize();
        Files.createDirectories(this.directory);
    }

    public FileUploadResponse save(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "файл пустой");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || !originalName.toLowerCase(Locale.ROOT).endsWith(".csv")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ожидается файл с расширением .csv");
        }

        UUID id = UUID.randomUUID();
        String fileName = id + ".csv";
        Path destination = directory.resolve(fileName);
        boolean created = false;

        try {
            try (InputStream input = file.getInputStream(); OutputStream output = Files.newOutputStream(destination, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)) {
                created = true;
                input.transferTo(output);
            }

            long size = Files.size(destination);
            log.info("сохранен CSV: id={}, size={}", id, size);

            return new FileUploadResponse(id.toString(), fileName, size);
        } catch (IOException exception) {
            if (created) {
                try {
                    Files.deleteIfExists(destination);
                } catch (IOException cleanupException) {
                    exception.addSuppressed(cleanupException);
                }
            }

            log.error("ошибка сохранения CSV: id={}", id, exception);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "не удалось сохранить файл", exception);
        }
    }

    public Resource load(UUID id) {
        Path file = directory.resolve(id + ".csv");

        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "файл не найден");
        }

        log.info("выдача CSV: id={}", id);
        return new FileSystemResource(file);
    }


    public synchronized FileUploadResponse saveReport(UUID reportId, byte[] content) throws IOException {
        if (reportId == null || content == null || content.length == 0) {
            throw new IllegalArgumentException("отсутствует идентификатор или содержимое отчета");
        }
        if (content.length > 20 * 1024 * 1024) {
            throw new IllegalArgumentException("размер CSV превышает 20 МБ");
        }

        String fileName = reportId + ".csv";
        Path destination = directory.resolve(fileName);

        if (Files.isRegularFile(destination, LinkOption.NOFOLLOW_LINKS)) {
            log.info("отчет уже сохранен: reportId={}", reportId);
            return new FileUploadResponse(reportId.toString(), fileName, Files.size(destination));
        }

        Path temporary = Files.createTempFile(directory, reportId + "-", ".tmp");

        try {
            Files.write(temporary, content);
            Files.move(temporary, destination, StandardCopyOption.ATOMIC_MOVE);
            log.info("отчет сохранен: reportId={}, size={}", reportId, content.length);
            return new FileUploadResponse(reportId.toString(), fileName, content.length);
        } finally {
            try {
                Files.deleteIfExists(temporary);
            } catch (IOException exception) {
                log.warn("не удалось удалить временный файл: {}", temporary, exception);
            }
        }
    }
}