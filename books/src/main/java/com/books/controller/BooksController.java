package com.books.controller;

import com.books.dto.BooksDTO;
import com.books.exception.FieldNotFoundException;
import com.books.service.BooksService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/books")
public class BooksController {

    private static final Logger logger = LoggerFactory.getLogger(BooksController.class);
    private final BooksService booksService;

    @Autowired
    public BooksController(BooksService booksService) {
        this.booksService = booksService;
    }

    @PostMapping
    public ResponseEntity<BooksDTO> createBook(@Valid @RequestBody BooksDTO booksDTO) {
        BooksDTO createdBook = booksService.createBook(booksDTO);
        logger.info("Book created successfully with ID: {}", createdBook.getId());
        return ResponseEntity.ok(createdBook);
    }

    @GetMapping
    public ResponseEntity<List<BooksDTO>> getAllBooks() {
        logger.info("Fetching all employees...");
        List<BooksDTO> books = booksService.getAllBooks();
        if (books.isEmpty()) {
            throw new FieldNotFoundException("No books found");
        }
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BooksDTO> getBookById(@PathVariable int id) {
        logger.info("Fetching book by ID: {}", id);
        return booksService.getBookById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new FieldNotFoundException("Book not found"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BooksDTO> updateBook(@PathVariable int id, @Valid @RequestBody BooksDTO booksDTO) {
        logger.info("Received request to update book ID: {}", id);
        return booksService.updateBook(id, booksDTO)
                .map(emp -> ResponseEntity.ok(booksDTO))
                .orElseThrow(() -> new FieldNotFoundException("Failed: Book update unsuccessful"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable int id) {
        logger.warn("Attempting to delete employee with ID: {}", id);
        boolean isDeleted = booksService.deleteBook(id);
        if (isDeleted) {
            return ResponseEntity.ok("Success: Book deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed: Book deletion unsuccessful");
        }
    }
}
