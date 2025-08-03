package com.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LibraryResponse {
    private int id;
    private String userName;
    private int bookId;
    private String bookTitle;
    private String userEmail;
}