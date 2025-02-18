package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.AuthAccessToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthAccessTokenRepository extends MongoRepository<AuthAccessToken, String> {
}
