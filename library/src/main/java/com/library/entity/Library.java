package com.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "library")
public class Library {

    @Id
    @Column(name = "ID", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "userName", nullable = false)
    private String userName;

    @Column(name = "BookId", nullable = false)
    private int bookId;

    @Column(name = "password",nullable = false)
    private String password;
}
