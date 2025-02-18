package com.example.LocknessAPI.services;

import com.example.LocknessAPI.models.Leaderboard;
import com.example.LocknessAPI.models.PointLog;
import com.example.LocknessAPI.repositories.LeaderboardRepository;
import com.example.LocknessAPI.repositories.PointLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointLogRepository pointLogRepository;
    private final LeaderboardRepository leaderboardRepository;
    private final StringRedisTemplate redisTemplate;
    private final LeaderboardService leaderboardService;

    public double addPoints(String userId, double points, String msg) {
        // Save log to database
        PointLog pointLog = new PointLog(null, userId, msg, points, Instant.now());
        pointLogRepository.save(pointLog);

        // Get current points from Redis
        Double currentPoints = redisTemplate.opsForZSet().score("leaderboard", userId);
        double newPoints = (currentPoints != null ? currentPoints : 0) + points;

        // Update in Redis
        redisTemplate.opsForZSet().add("leaderboard", userId, newPoints);

        // Update in database
        leaderboardService.updateUserPoints(userId, newPoints, Instant.now());

        return newPoints;
    }

    public List<Leaderboard> getTopUsers(int limit) {
        return redisTemplate.opsForZSet().reverseRangeWithScores("leaderboard", 0, limit - 1).stream()
                .map(entry -> new Leaderboard(entry.getValue(), entry.getScore()))
                .collect(Collectors.toList());
    }

    public double getUserPoints(String userId) {
        Double points = redisTemplate.opsForZSet().score("leaderboard", userId);
        if (points != null) {
            return points;
        }
        return leaderboardRepository.findPointsByUserId(userId);
    }

    public String syncLeaderboardRedisFromDB() {
        String leaderboardKey = "leaderboard";
        redisTemplate.delete(leaderboardKey);

        List<Leaderboard> users = leaderboardRepository.findAll();
        if (users.isEmpty()) return "leaderboard empty";

        users.forEach(user -> redisTemplate.opsForZSet().add(leaderboardKey, user.getUserId(), user.getPoints()));

        return "✅ Leaderboard redis has been reset and synchronized with the database!";
    }
}
