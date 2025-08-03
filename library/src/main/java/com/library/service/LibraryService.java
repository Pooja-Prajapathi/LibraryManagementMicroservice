package com.library.service;

import com.library.dto.*;
import com.library.entity.Library;
import com.library.feign.BookClient;
import com.library.feign.UserClient;
import com.library.repository.LibraryRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final BookClient bookClient;
    private final UserClient userClient;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;

    public LibraryService(LibraryRepository libraryRepository, BookClient bookClient, UserClient userClient,PasswordEncoder passwordEncoder,JWTService jwtService) {
        this.libraryRepository = libraryRepository;
        this.bookClient = bookClient;
        this.userClient = userClient;
        this.passwordEncoder=passwordEncoder;
        this.jwtService=jwtService;
    }

    public List<BookDTO> getAllBooks() {
        return bookClient.getAllBooks();
    }

    public BookDTO getBookById(int bookId) {
        return bookClient.getBookById(bookId);
    }

    public String addBook(BookDTO bookDTO) {
        return bookClient.addBook(bookDTO);
    }

    public BookDTO updateBook(int bookId, BookDTO bookDTO) {
        return bookClient.updateBook(bookId, bookDTO);
    }

    public void deleteBook(int bookId) {
        libraryRepository.deleteAllByBookId(bookId);
        bookClient.deleteBook(bookId);
    }

    public List<UserDTO> getAllUsers() {
        return userClient.getAllUsers();
    }

    public UserProfileResponse getUserProfile(String username) {
        List<Integer> bookIds = libraryRepository.findByUserName(username)
                .stream()
                .map(Library::getBookId)
                .collect(Collectors.toList());

        List<BookDTO> books = bookIds.stream()
                .map(bookClient::getBookById)
                .collect(Collectors.toList());

        UserDTO userDTO = userClient.getUserByName(username);

        return new UserProfileResponse(userDTO, books);
    }

    public UserDTO addUser(UserDTO userDTO) {
        return userClient.createUser(userDTO);
    }

    public void deleteUser(String username) {
        libraryRepository.deleteAllByUserName(username);
        userClient.deleteUser(username);
    }

    public UserDTO updateUser(String username, UserDTO userDTO) {
        return userClient.updateUser(username, userDTO);
    }

    public String issueBook(String username, int bookId, LibraryDTO libraryDTO) {
        // Check for existing borrowings
        long borrowedCount = libraryRepository.countByUserName(username);
        if (borrowedCount >= 3) {
            return "Failed: User has already borrowed the maximum of 3 books.";
        }

        // Check if book is already issued
        Optional<Library> existing = libraryRepository.findByUserName(username).stream()
                .filter(entry -> entry.getBookId() == bookId)
                .findFirst();

        if (existing.isPresent()) {
            return "Failed: Book already issued to this user.";
        }

        // Create and save new library entry
        Library library = Library.builder()
                .userName(username)
                .bookId(bookId)
                .password(passwordEncoder.encode(libraryDTO.getPassword()))
                .build();

        // Debug logging
        System.out.println("Saving library entry with password: " + library.getPassword());

        Library savedLibrary = libraryRepository.save(library);

        // Verify the save
        System.out.println("Saved library entry with ID: " + savedLibrary.getId() +
                " and password: " + savedLibrary.getPassword());

        return "Success: Book issued to user.";
    }

    public LoginResponse loginLibrary(LoginDTO loginDTO) {
        List<Library> libraryEntries = libraryRepository.findByUserName(loginDTO.getUserName());

        // Debug logging
        System.out.println("Found " + libraryEntries.size() + " entries for username: " + loginDTO.getUserName());

        if (libraryEntries.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        for (Library library : libraryEntries) {
            // Debug logging
            System.out.println("Stored password hash: " + library.getPassword());
            System.out.println("Input password: " + loginDTO.getPassword());

            boolean matches = passwordEncoder.matches(loginDTO.getPassword(), library.getPassword());
            System.out.println("Password matches: " + matches);

            if (matches) {
                String token = jwtService.generateToken(loginDTO.getUserName());
                return LoginResponse.builder()
                        .userName(loginDTO.getUserName())
                        .accessToken(token)
                        .build();
            }
        }

        throw new RuntimeException("Invalid password");
    }

    public List<LibraryResponse> getAllLibraryData() {
        List<Library> libraries = libraryRepository.findAll();

        return libraries.stream()
                .map(library -> {
                    BookDTO book = bookClient.getBookById(library.getBookId());
                    UserDTO user = userClient.getUserByName(library.getUserName());

                    return new LibraryResponse(
                            library.getId(),
                            library.getUserName(),
                            library.getBookId(),
                            book.getName(),
                            user.getEmail()
                    );
                })
                .collect(Collectors.toList());
    }

    public String releaseBook(String username, int bookId) {
        libraryRepository.deleteByUserNameAndBookId(username, bookId);
        return "Success: Book returned.";
    }
}
