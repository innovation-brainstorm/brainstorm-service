package org.brainstorm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brainstorm.instant.Status;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    //    @NotBlank(message = "fileName can't be null.")
    private String fileName;

    private String columnName;

    private int strategy;
    //add shell path
    private String shell_path;

    @Column(name = "generated_by_ai")
    private boolean generatedByAI;

    @JsonIgnore
    @ManyToOne
    private Session session;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    private boolean isPretrained;

    private String modelId;

    public Task(Session session) {
        this.session = session;
    }

    public Task(Session session, String columnName) {
        this.session = session;
        this.columnName = columnName;
        this.fileName = columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
        this.fileName = columnName + ".csv";
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", columnName='" + columnName + '\'' +
                ", strategy=" + strategy +
                ", generatedByAI=" + generatedByAI +
                ", status=" + status +
                '}';
    }
}
