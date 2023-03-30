package org.brainstorm.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.brainstorm.instant.Status;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String theSchema;

    private String tableName;

    @Enumerated(EnumType.STRING)
    private MODE destination;

    private long expectedCount;

    private int columnCount;

    private String directory;

    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<Task> tasks;

    public void setTableName(String tableName) {
        this.tableName = tableName;
        this.directory = tableName;
    }
}
