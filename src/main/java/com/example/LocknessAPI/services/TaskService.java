package com.example.LocknessAPI.services;

import com.example.LocknessAPI.commons.EntityStatus;
import com.example.LocknessAPI.models.Task;
import com.example.LocknessAPI.models.TaskAssignment;
import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.models.Worker;
import com.example.LocknessAPI.repositories.TaskAssignmentRepository;
import com.example.LocknessAPI.repositories.TaskRepository;
import com.example.LocknessAPI.repositories.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final WorkerRepository workerRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;
    private final StringRedisTemplate redisTemplate;

    @Transactional
    public String assignTaskToWorker(String taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Optional<Worker> optionalWorker = workerRepository.findFirstByStatus(EntityStatus.IDLE.getCode());
        if (optionalWorker.isEmpty()) {
            throw new RuntimeException("No available workers");
        }
        Worker worker = optionalWorker.get();

        TaskAssignment taskAssignment = TaskAssignment.builder()
                .workerId(worker.getId())
                .taskId(taskId)
                .assignedAt(LocalDateTime.now())
                .build();
        taskAssignmentRepository.save(taskAssignment);

        worker.setStatus(EntityStatus.BUSY.getCode());
        worker.setUpdatedAt(LocalDateTime.now());
        workerRepository.save(worker);

        String message = String.format(
                "{\"workerId\": \"%s\", \"userId\": \"%s\", \"taskId\": \"%s\", \"type\": null, \"files\": [%s], \"taskAssignmentId\": \"%s\"}",
                worker.getId(), worker.getUserId(), task.getId(),
                task.getFiles().stream().map(file -> String.format(
                        "{\"id\": \"%s\", \"path\": \"%s\", \"size\": %d, \"type\": \"%s\"}",
                        file.getId(), file.getFileName(), file.getFileSize(), file.getFileType()
                )).collect(Collectors.joining(",")),
                taskAssignment.getId()
        );

        redisTemplate.convertAndSend("job:send-to-worker", message);

        return worker.getId();
    }

    public Optional<Worker> getIdleWorker() {
        return workerRepository.findFirstByStatus(EntityStatus.IDLE.getCode());
    }

    public Optional<Worker> getIdleWorkerWithLock() {
        String redisLockKey = "worker_lock";
        Boolean isLocked = redisTemplate.hasKey(redisLockKey);
        if (Boolean.TRUE.equals(isLocked)) {
            throw new RuntimeException("Worker is locked");
        }

        redisTemplate.opsForValue().set(redisLockKey, "locked", 5, TimeUnit.SECONDS);

        Optional<Worker> worker = getIdleWorker();

        redisTemplate.delete(redisLockKey);
        return worker;
    }

    public Task createTask(User user, String title, Integer type) {
        Task task = new Task();
        task.setTitle(title);
        task.setType(type);
        task.setUser(user);
        task.setCreatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
}
