package com.example.LocknessAPI.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@Document(collection = "users")
@EnableMongoAuditing
public class User {

    @Id
    private String id = UUID.randomUUID().toString();
    private String fullName;
    private String email;
    private int points;
    private String password;
    private String wallet;
    private String referralCode;
    private String referredBy;

    @DBRef
    private List<Referral> referrals;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @DBRef
    private List<Task> tasks;

    @DBRef
    private List<Worker> workers;

    @DBRef
    private List<Model> models;
}
