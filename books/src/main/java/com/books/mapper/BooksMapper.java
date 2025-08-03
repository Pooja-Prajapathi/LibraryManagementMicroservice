package com.books.mapper;

import com.books.dto.BooksDTO;
import com.books.entity.Books;
import org.springframework.stereotype.Component;

@Component
public class BooksMapper {

    private BooksMapper() {}

    public BooksDTO toBooksDTO(Books books) {
        if (books == null) {
            return null;
        }
        return new BooksDTO(
                books.getId(),
                books.getName(),
                books.getPublisher(),
                books.getAuthor()
        );
    }

    public Books toBooksEntity(BooksDTO booksDTO) {
        if (booksDTO == null) {
            return null;
        }
        Books books = new Books();
        books.setId(booksDTO.getId());
        books.setName(booksDTO.getName());
        books.setPublisher(booksDTO.getPublisher());
        books.setAuthor(booksDTO.getAuthor());
        return books;
    }
}


