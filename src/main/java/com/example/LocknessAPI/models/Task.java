package com.example.LocknessAPI.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "tasks")
@Data
@NoArgsConstructor
public class Task {

    @Id
    private String id;

    private Integer userId;
    private Integer type;
    private String title;
    private String status;
    private String description;
    private String resultUrl;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @DBRef
    private User user;

    @DBRef
    private List<File> files;

    @DBRef
    private List<TaskAssignment> taskAssignments;
}
