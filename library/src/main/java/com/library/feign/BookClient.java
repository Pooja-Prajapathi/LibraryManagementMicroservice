package com.library.feign;

import com.library.dto.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "book-service")
public interface BookClient {

    @GetMapping("/books")
    List<BookDTO> getAllBooks();

    @GetMapping("/books/{bookId}")
    BookDTO getBookById(@PathVariable("bookId") int bookId);

    @PostMapping("/books")
    String addBook(@RequestBody BookDTO booksDTO);

    @PutMapping("/books/{bookId}")
    BookDTO updateBook(@PathVariable("bookId") int bookId, @RequestBody BookDTO booksDTO);

    @DeleteMapping("/books/{bookId}")
    void deleteBook(@PathVariable("bookId") int bookId);
}

