package org.brainstorm.model;

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

    @Column(name = "generated_by_ai")
    private boolean generatedByAI;

    @ManyToOne
    private Session session;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    public Task(Session session) {
        this.session = session;
    }

    public Task(Session session, String columnName) {
        this.session = session;
        this.columnName = columnName;
        this.fileName = columnName;
    }
}
