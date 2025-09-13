package org.elsveys.dto;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.SqlResultSetMapping;

public class OverdueTaskDTO {
    private String taskName;
    private String username;
    private Integer daysOverdue;

    public OverdueTaskDTO(String taskName, String username, Integer daysOverdue){
        this.taskName = taskName;
        this.username = username;
        this.daysOverdue = daysOverdue;
    }

    public String getTaskName() {
        return taskName;
    }

    public String getUsername() {
        return username;
    }

    public Integer getDaysOverdue() {
        return daysOverdue;
    }
}
