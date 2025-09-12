package org.elsveys.dto;

public class ActiveTaskDTO {
    private String taskName;
    private String description;
    private String status;
    private String projectName;

    public ActiveTaskDTO(Object[] row){
        this.taskName = (String) row[0];
        this.description = (String) row[1];
        this.status = (String) row[2];
        this.projectName = (String) row[3];
    }


    public String getTaskName() {
        return taskName;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getProjectName() {
        return projectName;
    }
}
