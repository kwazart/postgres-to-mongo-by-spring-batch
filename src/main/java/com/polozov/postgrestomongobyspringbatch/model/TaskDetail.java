package com.polozov.postgrestomongobyspringbatch.model;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Accessors(chain = true)
@Document(collection = "task")
@Data
public class TaskDetail {

    @Id
    private String taskId;

    private String taskName;

    private Short priority;

    @CreatedDate
    private LocalDateTime createdAt;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private Boolean isComplete;
}
