package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.Task;
import com.example.LocknessAPI.models.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.web.PagedModel;

import java.util.Optional;

public interface TaskRepository extends MongoRepository<Task, String> {
    PagedModel<Task> findByUser(User user, PageRequest pageRequest);
    Optional<Task> findByIdAndUser(String id, User user);
}
