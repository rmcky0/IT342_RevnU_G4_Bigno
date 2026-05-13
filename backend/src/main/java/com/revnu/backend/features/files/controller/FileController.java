package com.revnu.backend.features.files.controller;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.files.repository.FileRecordRepository;

@RestController
@RequestMapping("/revnu/files")
public class FileController {

    private final FileRecordRepository fileRecordRepository;

    public FileController(FileRecordRepository fileRecordRepository) {
        this.fileRecordRepository = fileRecordRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Void> serveFile(@PathVariable UUID id) {
        return fileRecordRepository.findById(id)
                .map(record -> ResponseEntity
                        .status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, record.getFilepath())
                        .<Void>build())
                .orElse(ResponseEntity.notFound().<Void>build());
    }
}
