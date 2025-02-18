package com.example.LocknessAPI.services;

import com.example.LocknessAPI.dtos.responses.WorkerResponse;
import com.example.LocknessAPI.models.Worker;
import com.example.LocknessAPI.repositories.WorkerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class WorkerService {
    private final WorkerRepository workerRepository;

    private static final int REQUEST_TIME = 5; // minutes

    public WorkerService(WorkerRepository workerRepository) {
        this.workerRepository = workerRepository;
    }

    @Transactional
    public WorkerResponse requestPoint(String workerId, LocalDateTime lastClaimAt) {
        Optional<Worker> workerOpt = workerRepository.findByIdAndLastClaimAtAndStatusIn(workerId, lastClaimAt, List.of("idle", "busy"));
        if (workerOpt.isEmpty()) {
            throw new RuntimeException("No available workers");
        }

        Worker worker = workerOpt.get();
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS).minusSeconds(1);

        if (worker.getLastClaimAt().plusMinutes(REQUEST_TIME).isAfter(now)) {
            throw new RuntimeException("Invalid time!");
        }

        long diffInMinutes = ChronoUnit.MINUTES.between(worker.getLastClaimAt(), now);
        if (diffInMinutes >= REQUEST_TIME + 0.5) {
            worker.setLastClaimAt(now);
            workerRepository.save(worker);
            return new WorkerResponse(worker.getId(), worker.getPoints(), now);
        }

        int newPoints = worker.getPoints() + worker.getSpeed();
        worker.setPoints(newPoints);
        worker.setLastClaimAt(now);
        workerRepository.save(worker);

        return new WorkerResponse(worker.getId(), newPoints, now);
    }
}
