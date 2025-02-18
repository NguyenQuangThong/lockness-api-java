package com.example.LocknessAPI.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class WorkerResponse {
    private String id;
    private Integer points;
    private LocalDateTime lastClaimAt;
}
