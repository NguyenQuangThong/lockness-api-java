package com.example.LocknessAPI.dtos.requests;

import lombok.Data;

@Data
public class WorkerRequest {
    private String osName;
    private String osVersion;
    private String machineId;
    private String ipAddress;
    private String cpu;
    private String gpu;
    private String ram;
}
