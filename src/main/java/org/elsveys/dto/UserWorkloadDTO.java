package org.elsveys.dto;

public class UserWorkloadDTO {
    private String username;
    private String specialization;
    private String taskName;
    public UserWorkloadDTO(Object[] row){
        this.username = (String) row[0];
        this.specialization = (String) row[1];
        this.taskName = (String) row[2];
    }

    public String getUsername() {
        return username;
    }

    public String getSpecialization() {
        return specialization;
    }

    public String getTaskName() {
        return taskName;
    }
}
