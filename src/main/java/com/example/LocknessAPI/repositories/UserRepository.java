package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    boolean existsByReferralCode(String referralCode);
}
