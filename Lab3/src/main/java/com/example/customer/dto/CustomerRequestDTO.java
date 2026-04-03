package com.example.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CustomerRequestDTO {

    @NotBlank(message = "Имя обязательно для заполнения")
    private String firstName;

    @NotBlank(message = "Фамилия обязательна для заполнения")
    private String lastName;

    @NotBlank(message = "Email обязателен для заполнения")
    @Email(message = "Введите корректный email адрес")
    private String email;

    public CustomerRequestDTO() {
    }

    public CustomerRequestDTO(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}