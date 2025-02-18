package com.example.LocknessAPI.repositories;

import com.example.LocknessAPI.models.Leaderboard;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LeaderboardRepository extends MongoRepository<Leaderboard, String> {
    double findPointsByUserId(String userId);
}
