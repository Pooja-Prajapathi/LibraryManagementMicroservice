package com.users.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UsersDTO {

    @Id
    @Column(name = "UserName", nullable = false,unique = true)
    @NotBlank(message = "User name is required")
    @Size(min = 3, max = 30, message = "User name must be between 3 and 30 characters")
    private String userName;

    @Column(name = "Email", nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
}
