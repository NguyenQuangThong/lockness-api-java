package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.PointLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PointLogRepository extends MongoRepository<PointLog, String> {
}
