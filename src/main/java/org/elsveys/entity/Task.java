package org.elsveys.entity;

import jakarta.persistence.*;
import org.elsveys.enums.StatusTask;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "description", nullable = false, length = 50)
    private String description;
    @Column(name = "status", nullable = false)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING)
    private StatusTask statusTask;
    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public StatusTask getStatusTask() {
        return statusTask;
    }

    public void setStatusTask(StatusTask statusTask) {
        this.statusTask = statusTask;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public LocalDate getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDate deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }
}
