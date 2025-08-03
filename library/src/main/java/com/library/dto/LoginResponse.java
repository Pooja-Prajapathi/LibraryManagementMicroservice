package com.library.dto;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    @Column(nullable = false)
    private String userName;

    @Column(name = "accessToken",nullable = false)
    private String accessToken;
}
