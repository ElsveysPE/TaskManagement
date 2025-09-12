package org.elsveys.dto;

public class ActiveUserDTO {
    private String username;
    private String email;
    private String role;
    private String specialization;

    public ActiveUserDTO(Object[] row) {
        this.username = (String) row[0];
        this.email = (String) row[1];
        this.role = (String) row[2];
        this.specialization = (String) row[3];
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public String getSpecialization() {
        return specialization;
    }
}
