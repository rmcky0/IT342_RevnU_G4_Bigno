package com.revnu.backend.features.tags.controller;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.revnu.backend.features.tags.dto.TagDto;
import com.revnu.backend.features.tags.service.TagService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/revnu/tags")
@PreAuthorize("hasRole('TENANT')")
public class TagController {

    private final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ResponseEntity<List<TagDto>> getTags(Principal principal) {
        return ResponseEntity.ok(tagService.getAllTags(principal.getName()));
    }

    @PostMapping
    public ResponseEntity<TagDto> addTag(Principal principal, @Valid @RequestBody TagDto request) {
        return ResponseEntity.ok(tagService.addTag(principal.getName(), request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(Principal principal, @PathVariable UUID id) {
        tagService.deleteTag(principal.getName(), id);
        return ResponseEntity.noContent().build();
    }
}
