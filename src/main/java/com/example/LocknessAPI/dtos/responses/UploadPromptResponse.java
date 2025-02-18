package com.example.LocknessAPI.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UploadPromptResponse {
    private String message;
    private Boolean status;
}
