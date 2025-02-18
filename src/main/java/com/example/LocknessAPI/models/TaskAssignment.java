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

@Document(collection = "task_assignments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskAssignment {

    @Id
    private String id;

    private String taskId;
    private String workerId;
    private String status;
    private LocalDateTime fileUploadTime;
    private String fileName;
    private Integer rating;

    @CreatedDate
    private LocalDateTime assignedAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @DBRef
    private Task task;

    @DBRef
    private Worker worker;
}
