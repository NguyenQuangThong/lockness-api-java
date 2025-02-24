package com.example.LocknessAPI.services;

import com.example.LocknessAPI.commons.MessageDefine;
import com.example.LocknessAPI.commons.EntityStatus;
import com.example.LocknessAPI.dtos.requests.WorkerRequest;
import com.example.LocknessAPI.dtos.responses.WorkerResponse;
import com.example.LocknessAPI.models.TaskAssignment;
import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.models.Worker;
import com.example.LocknessAPI.repositories.TaskAssignmentRepository;
import com.example.LocknessAPI.repositories.WorkerRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class WorkerService {
    private final WorkerRepository workerRepository;

    private final TaskAssignmentRepository taskAssignmentRepository;

    private static final int REQUEST_TIME = 5;

    @Transactional
    public WorkerResponse requestPoint(String workerId, LocalDateTime lastClaimAt) {
        Optional<Worker> workerOpt = workerRepository.findByIdAndLastClaimAtAndStatusIn(workerId, lastClaimAt, List.of("idle", "busy"));
        if (workerOpt.isEmpty()) {
            throw new RuntimeException(MessageDefine.NO_AVAILALBE_WORKER.getMessage());
        }

        Worker worker = workerOpt.get();
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusSeconds(1);

        if (worker.getLastClaimAt().plusMinutes(REQUEST_TIME).isAfter(now)) {
            throw new RuntimeException(MessageDefine.INVALID_TIME.getMessage());
        }

        long diffInMinutes = ChronoUnit.MINUTES.between(worker.getLastClaimAt(), now);
        if (diffInMinutes >= REQUEST_TIME + 0.5) {
            worker.setLastClaimAt(now);
            workerRepository.save(worker);
            return WorkerResponse.builder().data(worker).build();
        }

        int newPoints = worker.getPoints() + worker.getSpeed();
        worker.setPoints(newPoints);
        worker.setLastClaimAt(now);
        workerRepository.save(worker);

        return WorkerResponse.builder().data(worker).build();
    }

    public Worker create(WorkerRequest workerRequest, User user) {
        Worker worker = new Worker();
        worker.setId(UUID.randomUUID().toString());
        worker.setToken(generateToken());
        worker.setOsName(workerRequest.getOsName());
        worker.setOsVersion(workerRequest.getOsVersion());
        worker.setMachineId(workerRequest.getMachineId());
        worker.setIpAddress(workerRequest.getIpAddress());
        worker.setCpu(workerRequest.getCpu());
        worker.setGpu(workerRequest.getGpu());
        worker.setRam(workerRequest.getRam());
        worker.setUptime(0);
        worker.setStatus(EntityStatus.IDLE.getCode());
        worker.setSpeed(2);
        worker.setLastClaimAt(LocalDateTime.now());
        worker.setUser(user);
        worker.setCreatedAt(LocalDateTime.now());

        workerRepository.save(worker);

        return worker;
    }

    public Worker modifyWorkerByStatus(String id, byte status, User user) {
        Worker worker = workerRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException(MessageDefine.WORKER_NOT_FOUND.getMessage()));

        worker.setStatus(status);
        LocalDateTime now = LocalDateTime.now();

        if (status == EntityStatus.IDLE.getCode()) {
            if (worker.getLastClaimAt() == null) {
                worker.setLastClaimAt(now);
            }
        }

        if (status == EntityStatus.IDLE.getCode() && worker.getStatus() == EntityStatus.OFF.getCode()) {
            worker.setLastClaimAt(now);
        }

        if (status == EntityStatus.OFF.getCode()) {
            worker.setLastClaimAt(now);
        }

        workerRepository.save(worker);
        return worker;
    }

    public short uploadFile(MultipartFile file, String taskAssignmentId, User user) {
        try {
            if (file == null || file.isEmpty()) {
                return MessageDefine.FAIL_TO_UPLOAD_FILE.getCode();
            }

            String ext = getFileExtension(file);
            if (!"splat".equalsIgnoreCase(ext)) {
                return MessageDefine.INVALID_FILE_TYPE.getCode();
            }

            TaskAssignment taskAssignment = taskAssignmentRepository.findById(taskAssignmentId)
                    .orElseThrow(() -> new RuntimeException(MessageDefine.TASK_ASSIGNMENT_NOT_FOUND.getMessage()));

            Worker worker = workerRepository.findById(taskAssignment.getWorkerId())
                    .orElseThrow(() -> new RuntimeException(MessageDefine.WORKER_INVALID.getMessage()));

            if (worker.getStatus() != EntityStatus.BUSY.getCode()) {
                return MessageDefine.WORKER_INVALID.getCode();
            }

            if (!worker.getUser().getId().equals(user.getId())) {
                return MessageDefine.WORKER_NOT_FOUND.getCode();
            }

            String folderId = UUID.randomUUID().toString();
            String key = folderId + "/0." + ext;
            Path destination = Paths.get("uploads/" + key);
            Files.createDirectories(destination.getParent());
            file.transferTo(destination);

            taskAssignment.setFileName(key);
            taskAssignment.setFileUploadTime(LocalDateTime.now());
            taskAssignmentRepository.save(taskAssignment);

            worker.setStatus(EntityStatus.IDLE.getCode());
            workerRepository.save(worker);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return MessageDefine.SUCCESS.getCode();
    }

    public byte[] getFile(String name) throws IOException {
        Path path = Paths.get("storage/uploads/" + name);
        byte[] content = Files.readAllBytes(path);
        return content;
    }

    public void revoke(String id, User user) {
        Worker worker = workerRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException(MessageDefine.WORKER_NOT_FOUND.getMessage()));
        workerRepository.delete(worker);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }

    public String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        return originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase() : "";
    }
}
