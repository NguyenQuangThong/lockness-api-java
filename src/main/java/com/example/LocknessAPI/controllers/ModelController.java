package com.example.LocknessAPI.controllers;

import com.example.LocknessAPI.commons.EntityStatus;
import com.example.LocknessAPI.commons.MessageDefine;
import com.example.LocknessAPI.dtos.responses.UploadModelResponse;
import com.example.LocknessAPI.models.Model;
import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.repositories.ModelRepository;
import com.example.LocknessAPI.services.ModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/models")
@RequiredArgsConstructor
public class ModelController {

    private final ModelService modelService;

    private final ModelRepository modelRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping
    public Page<Model> index(@RequestParam(defaultValue = "0") int page,
                             @AuthenticationPrincipal User user) {
        return modelRepository.findByUser(user,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @PostMapping
    public ResponseEntity<Model> create(@RequestParam String prompt,
                                        @AuthenticationPrincipal User user) {
        Model model = modelService.createModel(prompt, user.getId());
        return ResponseEntity.status(201).body(model);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("model_id") String modelId,
                                    @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is required");
            }

            Model model = modelRepository.findById(modelId)
                    .orElseThrow(() -> new RuntimeException(MessageDefine.MODEL_NOT_FOUND.getMessage()));

            String filePath = model.getId() + "." + getFileExtension(file);
            Path destination = Paths.get("uploads/text-to-3d/", filePath);
            Files.createDirectories(destination.getParent());
            file.transferTo(destination);

            model.setModelUrl(filePath);
            model.setStatus(EntityStatus.DONE.getCode());
            Model updatedModel = modelRepository.save(model);

            redisTemplate.convertAndSend("job:send-model",
                    // Using Jackson or similar JSON mapper would be better
                    String.format("{\"id\":%d,\"userId\":%d,\"prompt\":\"%s\",\"status\":\"%s\",\"modelUrl\":\"%s\"}",
                            updatedModel.getId(),
                            updatedModel.getUser().getId(),
                            updatedModel.getPrompt(),
                            updatedModel.getStatus(),
                            updatedModel.getModelUrl()
                    )
            );

            return ResponseEntity.ok()
                    .body(new UploadModelResponse(MessageDefine.FILE_UPLOADED_SUCCESSFULLY.getMessage(), true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/upload-thumbnail")
    public ResponseEntity<?> uploadThumbnail(@RequestParam("model_id") String modelId,
                                             @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(MessageDefine.FILE_IS_REQUIRED.getMessage());
            }

            Model model = modelRepository.findById(modelId)
                    .orElseThrow(() -> new RuntimeException(MessageDefine.MODEL_NOT_FOUND.getMessage()));

            String filePath = model.getId() + "." + getFileExtension(file);
            Path destination = Paths.get("uploads/text-to-3d/thumbnails/", filePath);
            Files.createDirectories(destination.getParent());
            file.transferTo(destination);

            model.setThumbnailUrl(filePath);
            Model updatedModel = modelRepository.save(model);

            redisTemplate.convertAndSend("job:send-model",
                    // Using Jackson or similar JSON mapper would be better
                    String.format("{\"id\":%d,\"userId\":%d,\"prompt\":\"%s\",\"status\":\"%s\",\"modelUrl\":\"%s\",\"thumbnailUrl\":\"%s\"}",
                            updatedModel.getId(),
                            updatedModel.getUser().getId(),
                            updatedModel.getPrompt(),
                            updatedModel.getStatus(),
                            updatedModel.getModelUrl(),
                            updatedModel.getThumbnailUrl()
                    )
            );

            return ResponseEntity.ok()
                    .body(new UploadModelResponse(MessageDefine.THUMBNAIL_UPLOADED_SUCCESSFULLY.getMessage(), true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @GetMapping("/file/{name}")
    public ResponseEntity<byte[]> getFile(@PathVariable String name) throws Exception {
        Path path = Paths.get("uploads/text-to-3d/" + name);
        byte[] content = Files.readAllBytes(path);

        return ResponseEntity.ok()
                .header("Cross-Origin-Resource-Policy", "cross-origin")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    @GetMapping("/thumbnail/{name}")
    public ResponseEntity<byte[]> getThumbnail(@PathVariable String name) throws Exception {
        Path path = Paths.get("uploads/text-to-3d/thumbnails/" + name);
        byte[] content = Files.readAllBytes(path);

        return ResponseEntity.ok()
                .header("Cross-Origin-Resource-Policy", "cross-origin")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
    }
}
