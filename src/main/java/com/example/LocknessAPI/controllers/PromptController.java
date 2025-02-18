package com.example.LocknessAPI.controllers;

import com.example.LocknessAPI.dtos.responses.UploadPromptResponse;
import com.example.LocknessAPI.models.Prompt;
import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.repositories.PromptRepository;
import com.example.LocknessAPI.services.PromptService;
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
@RequestMapping("/prompts")
@RequiredArgsConstructor
public class PromptController {

    private final PromptService promptService;

    private final PromptRepository promptRepository;

    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping
    public Page<Prompt> index(@RequestParam(defaultValue = "0") int page,
                              @AuthenticationPrincipal User user) {
        return promptRepository.findByUser(user,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
    }

    @PostMapping
    public ResponseEntity<Prompt> create(@RequestParam String text,
                                         @AuthenticationPrincipal User user) {
        Prompt prompt = promptService.createPrompt(text, user.getId());
        return ResponseEntity.status(201).body(prompt);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("prompt_id") String promptId,
                                    @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is required");
            }

            Prompt prompt = promptRepository.findById(promptId)
                    .orElseThrow(() -> new RuntimeException("Prompt not found"));

            String filePath = prompt.getId() + "." + getFileExtension(file);
            Path destination = Paths.get("uploads/text-to-3d/", filePath);
            Files.createDirectories(destination.getParent());
            file.transferTo(destination);

            prompt.setModelUrl(filePath);
            prompt.setStatus("done");
            Prompt updatedPrompt = promptRepository.save(prompt);

            redisTemplate.convertAndSend("job:send-prompt",
                    // Using Jackson or similar JSON mapper would be better
                    String.format("{\"id\":%d,\"userId\":%d,\"text\":\"%s\",\"status\":\"%s\",\"modelUrl\":\"%s\"}",
                            updatedPrompt.getId(),
                            updatedPrompt.getUser().getId(),
                            updatedPrompt.getText(),
                            updatedPrompt.getStatus(),
                            updatedPrompt.getModelUrl()
                    )
            );

            return ResponseEntity.ok()
                    .body(new UploadPromptResponse("File uploaded successfully!", true));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PostMapping("/upload-thumbnail")
    public ResponseEntity<?> uploadThumbnail(@RequestParam("prompt_id") String promptId,
                                             @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("File is required");
            }

            Prompt prompt = promptRepository.findById(promptId)
                    .orElseThrow(() -> new RuntimeException("Prompt not found"));

            String filePath = prompt.getId() + "." + getFileExtension(file);
            Path destination = Paths.get("uploads/text-to-3d/thumbnails/", filePath);
            Files.createDirectories(destination.getParent());
            file.transferTo(destination);

            prompt.setThumbnailUrl(filePath);
            Prompt updatedPrompt = promptRepository.save(prompt);

            redisTemplate.convertAndSend("job:send-prompt",
                    // Using Jackson or similar JSON mapper would be better
                    String.format("{\"id\":%d,\"userId\":%d,\"text\":\"%s\",\"status\":\"%s\",\"modelUrl\":\"%s\",\"thumbnailUrl\":\"%s\"}",
                            updatedPrompt.getId(),
                            updatedPrompt.getUser().getId(),
                            updatedPrompt.getText(),
                            updatedPrompt.getStatus(),
                            updatedPrompt.getModelUrl(),
                            updatedPrompt.getThumbnailUrl()
                    )
            );

            return ResponseEntity.ok()
                    .body(new UploadPromptResponse("Thumbnail uploaded successfully!", true));
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
