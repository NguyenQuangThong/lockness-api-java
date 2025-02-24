package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.Model;
import com.example.LocknessAPI.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModelRepository extends MongoRepository<Model, String> {
    Page<Model> findByUser(User user, PageRequest pageRequest);
}
