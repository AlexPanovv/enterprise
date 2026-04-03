package com.example.lab2.Model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name; // Например: "ROLE_USER", "ROLE_ADMIN"

    public String getName() {
        return name;
    }
}
