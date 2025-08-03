package com.library.controller;

import com.library.dto.*;
import com.library.service.LibraryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;

    public LibraryController(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    @GetMapping
    public ResponseEntity<List<LibraryResponse>> getAllLibraryData() {
        List<LibraryResponse> libraryData = libraryService.getAllLibraryData();
        return ResponseEntity.ok(libraryData);
    }

    @GetMapping("/books")
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(libraryService.getAllBooks());
    }

    @GetMapping("/books/{bookId}")
    public ResponseEntity<BookDTO> getBook(@PathVariable int bookId) {
        return ResponseEntity.ok(libraryService.getBookById(bookId));
    }

    @PostMapping("/books")
    public ResponseEntity<String> addBook(@RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(libraryService.addBook(bookDTO));
    }

    @PutMapping("/books/{bookId}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable int bookId, @RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(libraryService.updateBook(bookId, bookDTO));
    }

    @DeleteMapping("/books/{bookId}")
    public ResponseEntity<String> deleteBook(@PathVariable int bookId) {
        libraryService.deleteBook(bookId);
        return ResponseEntity.ok("Book and related records deleted.");
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(libraryService.getAllUsers());
    }

    @GetMapping("/users/{username}")
    public ResponseEntity<UserProfileResponse> getUserProfile(@PathVariable String username) {
        return ResponseEntity.ok(libraryService.getUserProfile(username));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginLibrary(@RequestBody LoginDTO loginDTO) {
        LoginResponse response = libraryService.loginLibrary(loginDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/users")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO savedUser = libraryService.addUser(userDTO);
        return ResponseEntity.ok(savedUser);
    }

    @PutMapping("/users/{username}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable String username, @RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(libraryService.updateUser(username, userDTO));
    }

    @DeleteMapping("/users/{username}")
    public ResponseEntity<String> deleteUser(@PathVariable String username) {
        libraryService.deleteUser(username);
        return ResponseEntity.ok("User and their borrowed books deleted.");
    }

    @PostMapping("/users/{username}/books/{bookId}")
    public ResponseEntity<String> issueBook(@PathVariable String username, @PathVariable int bookId, @RequestBody LibraryDTO libraryDTO) {
        return ResponseEntity.ok(libraryService.issueBook(username, bookId,libraryDTO));
    }

    @DeleteMapping("/users/{username}/books/{bookId}")
    public ResponseEntity<String> releaseBook(@PathVariable String username, @PathVariable int bookId) {
        return ResponseEntity.ok(libraryService.releaseBook(username, bookId));
    }
}
