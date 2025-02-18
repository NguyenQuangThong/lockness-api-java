package com.example.LocknessAPI.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.time.LocalDateTime;

@Document(collection = "files")
@Data
@NoArgsConstructor
public class File {

    @Id
    private String id;

    @DBRef
    private Task task;

    @DBRef
    private Worker worker;

    private String status;
    private String fileName;
    private String thumbnailPath;
    private Integer fileType;
    private Long fileSize;
    private Integer width;
    private Integer height;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
