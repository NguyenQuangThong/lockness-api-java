package com.example.LocknessAPI.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "models")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Model {

    @Id
    private String id;

    private String prompt;
    private byte status;
    private String modelUrl;
    private String thumbnailUrl;
    private String userId;

    @DBRef
    private User user;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
