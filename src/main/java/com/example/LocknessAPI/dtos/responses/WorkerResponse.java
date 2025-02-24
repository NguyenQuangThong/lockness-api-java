package com.example.LocknessAPI.dtos.responses;

import com.example.LocknessAPI.models.Worker;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkerResponse {
    private Worker data;
    private Boolean success;
}
