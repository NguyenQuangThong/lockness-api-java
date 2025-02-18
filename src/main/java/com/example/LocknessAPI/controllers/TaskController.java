package com.example.LocknessAPI.controllers;

import com.example.LocknessAPI.dtos.responses.TaskResponse;
import com.example.LocknessAPI.dtos.responses.UploadTaskResponse;
import com.example.LocknessAPI.models.File;
import com.example.LocknessAPI.models.Task;
import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.repositories.FileRepository;
import com.example.LocknessAPI.repositories.TaskRepository;
import com.example.LocknessAPI.services.FileService;
import com.example.LocknessAPI.services.QueueService;
import com.example.LocknessAPI.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;

    private final FileService fileService;

    private final TaskService taskService;

    private final QueueService queueService;

    private final FileRepository fileRepository;

    @GetMapping
    public PagedModel<Task> index(@RequestParam(defaultValue = "0") int page,
                                  @AuthenticationPrincipal User user) {
        return taskRepository.findByUser(user,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> show(@PathVariable String id,
                                  @AuthenticationPrincipal User user) {
        Task task = taskRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        return ResponseEntity.ok(new TaskResponse(task));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> upload(@RequestPart(name = "file", required = false) MultipartFile file,
                                    @RequestPart(name = "files", required = false) MultipartFile[] files,
                                    @AuthenticationPrincipal User user) {
        if ((file == null || file.isEmpty()) && (files == null || files.length == 0)) {
            return ResponseEntity.badRequest().body("File invalid");
        }

        String folderId = UUID.randomUUID().toString();

        try {
            if (file != null && !file.isEmpty()) {
                // Single file upload (image or video)
                String ext = getFileExtension(file);
                boolean isVideo = List.of("mp4", "mov").contains(ext);
                String key = folderId + "/0." + ext;

                fileService.saveFile(file, key);

                Task task = taskService.createTask(user, "", isVideo ? 1 : 0);
                fileService.createFile(task, key, isVideo ? 1 : 0, file.getSize());

                dispatchQueueJob(task.getId());

                return ResponseEntity.ok(new UploadTaskResponse("Video uploaded", task));
            } else if (files != null && files.length > 0) {
                // Multiple images upload
                Task task = taskService.createTask(user, "", 2);
                task.setResultUrl("http://0.0.0.0:3333/3d/banana-3.obj");
                task = taskRepository.save(task);

                List<File> taskFiles = new ArrayList<>();
                for (int i = 0; i < files.length; i++) {
                    String ext = getFileExtension(files[i]);
                    String key = folderId + "/" + i + "." + ext;
                    fileService.saveFile(files[i], key);
                    taskFiles.add(fileService.createFile(task, key, 0, files[i].getSize()));
                }

                fileRepository.saveAll(taskFiles);
                dispatchQueueJob(task.getId());

                return ResponseEntity.ok(new UploadTaskResponse("Images uploaded", task));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.badRequest().body("Invalid upload request");
    }

    @GetMapping("/file/{name}")
    public ResponseEntity<byte[]> getFile(@PathVariable String name) throws Exception {
        Path path = Paths.get("public/3d/" + name);
        byte[] content = Files.readAllBytes(path);

        return ResponseEntity.ok()
                .header("Cross-Origin-Opener-Policy", "same-origin")
                .header("Cross-Origin-Embedder-Policy", "require-corp")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    private void dispatchQueueJob(String taskId) {
        queueService.dispatch(
                taskId,
                "task",
                10,
                "exponential",
                3000
        );
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }
}
