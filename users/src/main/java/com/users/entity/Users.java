package com.users.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "users")
public class Users {

    @Id
    @Column(name = "UserName", nullable = false,unique = true)
    private String userName;

    @Column(name = "Email", nullable = false)
    private String email;
}
