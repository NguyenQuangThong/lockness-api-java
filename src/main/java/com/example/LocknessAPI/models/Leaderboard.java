package com.example.LocknessAPI.models;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "leaderboards")
@Data
@NoArgsConstructor
public class Leaderboard {

    @Id
    private String id;

    private String userId;

    @Indexed
    private double points = 0;

    @LastModifiedDate
    private Instant updatedAt;

    public Leaderboard(String userId, double points) {
        this.userId = userId;
        this.points = points;
    }
}
