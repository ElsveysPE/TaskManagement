package org.elsveys.dto;

import org.elsveys.entity.Assignment;
import org.elsveys.enums.StatusTask;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public class TaskDTO {
    private Integer id;
    private String name;
    private String description;
    private StatusTask status;
    private Integer projectId;
    private boolean deleted;
    private List<Assignment> assignments;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public StatusTask getStatus() {
        return status;
    }

    public void setStatus(StatusTask status) {
        this.status = status;
    }

    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }

    public List<Assignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<Assignment> assignments) {
        this.assignments = assignments;
    }

    public void deleteAssignment(Assignment assignment) {
        assignments.remove(assignment);
    }

    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
