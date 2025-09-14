package org.elsveys.entity;

import jakarta.persistence.*;
import org.elsveys.enums.StatusProject;

import java.time.LocalDate;
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Column(name = "name", nullable = false, length = 50)
    private String name;
    @Column(name = "description", nullable = false, length = 50)
    private String description;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusProject statusProject;
    @Column(name = "deadline", nullable = false)
    private LocalDate deadline;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
    @Column(name = "deleted_at")
    private LocalDate deletedAt;
    @ManyToOne
    @JoinColumn(name = "teamlead_id", nullable = false)
    private User teamlead;
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

    public StatusProject getStatusProject() {
        return statusProject;
    }

    public void setStatusProject(StatusProject statusProject) {
        this.statusProject = statusProject;
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

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public User getTeamlead() {
        return teamlead;
    }

    public void setTeamlead(User teamlead) {
        this.teamlead = teamlead;
    }
}
