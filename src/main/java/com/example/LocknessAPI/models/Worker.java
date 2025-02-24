package com.example.LocknessAPI.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

@Document(collection = "workers")
@Data
@NoArgsConstructor
public class Worker {

    @Id
    private String id = UUID.randomUUID().toString();

    private Byte status;
    private String token;
    private String osName;
    private String osVersion;
    private String machineId;
    private String ipAddress;
    private String cpu;
    private String gpu;
    private String ram;
    private Integer uptime;
    private Integer speed;
    private Integer points;
    private LocalDateTime lastActiveAt;
    private LocalDateTime lastClaimAt;
    private String userId;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @DBRef
    private User user;

    @DBRef
    private List<File> files;
}
