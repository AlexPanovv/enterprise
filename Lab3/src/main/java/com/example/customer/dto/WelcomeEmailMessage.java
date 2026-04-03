package com.example.customer.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class WelcomeEmailMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long customerId;
    private String email;
    private String firstName;
    private LocalDateTime createdAt;

    public WelcomeEmailMessage() {
    }

    public WelcomeEmailMessage(Long customerId, String email, String firstName) {
        this.customerId = customerId;
        this.email = email;
        this.firstName = firstName;
        this.createdAt = LocalDateTime.now();
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "WelcomeEmailMessage{" +
                "customerId=" + customerId +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
