package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.File;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface FileRepository extends MongoRepository<File, String> {
}
