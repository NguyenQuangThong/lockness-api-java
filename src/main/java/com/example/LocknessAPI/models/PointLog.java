package com.example.LocknessAPI.models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "point_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointLog {

    @Id
    private String id;

    private String userId;

    private String msg;

    private double points;

    @CreatedDate
    private Instant createdAt;
}
