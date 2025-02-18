package com.example.LocknessAPI.services;

import com.example.LocknessAPI.models.File;
import com.example.LocknessAPI.models.Task;
import com.example.LocknessAPI.repositories.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class FileService {
    private final FileRepository fileRepository;

    public File createFile(Task task, String fileName, Integer fileType, long size) {
        File file = new File();
        file.setTask(task);
        file.setFileName(fileName);
        file.setFileType(fileType);
        file.setFileSize(size);
        return fileRepository.save(file);
    }

    public void saveFile(MultipartFile file, String key) throws Exception {
        Path destination = Paths.get("uploads/" + key);
        Files.createDirectories(destination.getParent());
        file.transferTo(destination);
    }
}
