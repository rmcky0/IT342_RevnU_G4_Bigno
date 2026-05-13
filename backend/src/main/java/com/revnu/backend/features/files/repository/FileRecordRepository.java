package com.revnu.backend.features.files.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.revnu.backend.features.files.model.FileRecord;

@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, UUID> {
}
