package tech.codecraftsman.todolist.task;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Entity(name = "tasks")
public class TaskModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID taskId;
    private String description;
    @Column(length = 50)
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String priority;
    @CreationTimestamp
    private LocalDateTime createdAt;
    private UUID userId;

    public void setTitle(String title) throws Exception {
        if (title.length() > 50) {

            throw new Exception("O campo de título deve conter no máximo 50 caracteres");

        }
        this.title = title;

    }
}
