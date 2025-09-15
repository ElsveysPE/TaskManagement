package org.elsveys.dto;

import org.elsveys.enums.UserRole;

public class ProfileDTO {
    private String alias;
    private String username;
    private String email;
    private String specialization;
    private UserRole role;
    public ProfileDTO(String alias, String username, String email, String specialization, UserRole role){
        this.alias = alias;
        this.username = username;
        this.email = email;
        this.specialization = specialization;
        this.role = role;
    }


    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
}
