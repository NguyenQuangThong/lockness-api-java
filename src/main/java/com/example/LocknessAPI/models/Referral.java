package com.example.LocknessAPI.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "referrals")
@Data
@NoArgsConstructor
public class Referral {

    @Id
    private String id;

    private String referrerId;
    private String referredId;

    @CreatedDate
    private LocalDateTime createdAt;
}
