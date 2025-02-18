package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.Worker;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends MongoRepository<Worker, String> {
    Optional<Worker> findFirstByStatus(String status);

    Optional<Worker> findByIdAndLastClaimAtAndStatusIn(String id, LocalDateTime lastClaimAt, List<String> statuses);
}
