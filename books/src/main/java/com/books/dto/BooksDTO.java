package com.books.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BooksDTO {

    @Id
    @Positive(message = "Book ID must be a positive number")
    @Column(name = "ID", unique = true)
    private Integer id;

    @Column(name = "Name", nullable = false)
    @NotBlank(message = "Book name is required")
    @Size(min = 3, max = 30, message = "Book name must be between 3 and 30 characters")
    private String name;

    @Column(name = "Publisher", nullable = false)
    @NotBlank(message = "Publisher name is required")
    @Size(min = 3, max = 30, message = "Publisher name must be between 3 and 30 characters")
    private String publisher;

    @Column(name = "Author", nullable = false)
    @NotNull(message = "Author name is required")
    @Size(min = 3, max = 30, message = "Author name must be between 3 and 30 characters")
    private String author;
}
