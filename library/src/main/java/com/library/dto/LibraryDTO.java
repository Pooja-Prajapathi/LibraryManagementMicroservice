package com.library.dto;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LibraryDTO {

    @Column(name = "password",nullable = false)
    private String password;
}
