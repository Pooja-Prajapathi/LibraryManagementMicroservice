package com.books.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "books")
public class Books {

    @Id
    @Column(name = "ID", unique = true)
    private int id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "Publisher", nullable = false)
    private String publisher;

    @Column(name = "Author", nullable = false)
    private String author;
}
