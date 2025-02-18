package com.example.LocknessAPI.dtos.responses;

import com.example.LocknessAPI.models.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskResponse {
    private Task data;
}
