package com.example.LocknessAPI.dtos.requests;

import lombok.Data;

@Data
public class VerifyRequest {
    private String message;
    private String signature;
}
