package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.Prompt;
import com.example.LocknessAPI.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PromptRepository extends MongoRepository<Prompt, String> {
    Page<Prompt> findByUser(User user, PageRequest pageRequest);
}
