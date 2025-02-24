package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.User;
import com.example.LocknessAPI.models.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkerRepository extends MongoRepository<Worker, String> {
    Optional<Worker> findFirstByStatus(byte status);

    Optional<Worker> findByIdAndLastClaimAtAndStatusIn(String id, LocalDateTime lastClaimAt, List<String> statuses);

    Page<Worker> findByUser(User user, PageRequest pageRequest);

    Optional<Worker> findByIdAndUser(String id, User user);
}
