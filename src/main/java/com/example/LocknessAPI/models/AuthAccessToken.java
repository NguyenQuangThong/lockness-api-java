package com.example.LocknessAPI.models;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "auth_access_tokens")
@Data
@NoArgsConstructor
public class AuthAccessToken {

    @Id
    private String id;

    private String tokenableId;

    private String type;

    private String name;

    private String hash;

    private List<String> abilities;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    private Instant lastUsedAt;

    private Instant expiresAt;
}
