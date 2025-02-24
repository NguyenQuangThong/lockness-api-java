package com.example.LocknessAPI.controllers;

import com.example.LocknessAPI.commons.MessageDefine;
import com.example.LocknessAPI.dtos.requests.WorkerRequest;
import com.example.LocknessAPI.dtos.responses.ErrorResponse;
import com.example.LocknessAPI.dtos.responses.UploadWorkerResponse;
import com.example.LocknessAPI.dtos.responses.WorkerResponse;
import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.models.Worker;
import com.example.LocknessAPI.repositories.WorkerRepository;
import com.example.LocknessAPI.services.WorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Objects;

@RestController
@RequestMapping("/workers")
@RequiredArgsConstructor
public class WorkerController {

    private final WorkerRepository workerRepository;

    private final WorkerService workerService;

    @GetMapping
    public Page<Worker> index(@RequestParam(defaultValue = "0") int page,
                              @AuthenticationPrincipal User user) {
        return workerRepository.findByUser(user,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody WorkerRequest input,
                                    @AuthenticationPrincipal User user) {
        Worker worker = workerService.create(input, user);
        return ResponseEntity.ok(WorkerResponse.builder().data(worker).build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> revoke(@PathVariable String id,
                                    @AuthenticationPrincipal User user) {
        workerService.revoke(id, user);
        return ResponseEntity.ok(WorkerResponse.builder().success(true).build());
    }

    @PostMapping("/status")
    public ResponseEntity<?> status(@RequestParam String id,
                                    @RequestParam byte status,
                                    @AuthenticationPrincipal User user) {
        Worker worker = workerService.modifyWorkerByStatus(id, status, user);
        return ResponseEntity.ok(WorkerResponse.builder().success(true).data(worker).build());
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart(name = "file", required = false) MultipartFile file,
                                    @RequestParam("task_assignment_id") String taskAssignmentId,
                                    @AuthenticationPrincipal User user) {
        try {
            short code = workerService.uploadFile(file, taskAssignmentId, user);
            if (Objects.equals(code, MessageDefine.FAIL_TO_UPLOAD_FILE.getCode())) {
                return ResponseEntity.ok(new UploadWorkerResponse(MessageDefine.FAIL_TO_UPLOAD_FILE.getMessage()));
            }

            if (Objects.equals(code, MessageDefine.INVALID_FILE_TYPE.getCode())) {
                return ResponseEntity.badRequest().body(MessageDefine.INVALID_FILE_TYPE.getMessage());
            }

            if (Objects.equals(code, MessageDefine.WORKER_INVALID.getCode())) {
                return ResponseEntity.badRequest().body(new ErrorResponse(MessageDefine.WORKER_INVALID.getMessage()));
            }

            if (Objects.equals(code, MessageDefine.WORKER_NOT_FOUND.getCode())) {
                return ResponseEntity.badRequest().body(new ErrorResponse(MessageDefine.WORKER_NOT_FOUND.getMessage()));
            }

            return ResponseEntity.ok(new UploadWorkerResponse(MessageDefine.SUCCESS.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/file/{name}")
    public ResponseEntity<byte[]> getFile(@PathVariable String name) throws IOException {
        byte[] content = workerService.getFile(name);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }
}
