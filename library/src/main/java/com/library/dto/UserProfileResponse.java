package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserProfileResponse {
    private UserDTO user;
    private List<BookDTO> borrowedBooks;
}
