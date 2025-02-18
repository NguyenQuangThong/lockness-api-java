package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.TaskAssignment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskAssignmentRepository extends MongoRepository<TaskAssignment, String> {
}
