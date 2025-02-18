package com.example.LocknessAPI.services;

import com.example.LocknessAPI.models.Leaderboard;
import com.example.LocknessAPI.repositories.LeaderboardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LeaderboardService {
    private final LeaderboardRepository leaderboardRepository;

    public void updateUserPoints(String userId, double points, Instant timestamp) {
        Leaderboard leaderboard = leaderboardRepository.findById(userId).orElse(new Leaderboard());
        leaderboard.setPoints(points);
        leaderboard.setUpdatedAt(timestamp);
        leaderboardRepository.save(leaderboard);
    }
}
