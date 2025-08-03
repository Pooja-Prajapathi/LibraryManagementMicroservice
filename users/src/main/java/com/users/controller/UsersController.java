package com.users.controller;

import com.users.dto.UsersDTO;
import com.users.exception.FieldNotFoundException;
import com.users.service.UsersService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UsersController {

    private static final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final UsersService usersService;

    @Autowired
    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @PostMapping
    public ResponseEntity<UsersDTO> createUser(@Valid @RequestBody UsersDTO usersDTO) {
        logger.info("Received request to create user: {}", usersDTO);
        return ResponseEntity.ok(usersService.createUser(usersDTO));
    }

    @GetMapping
    public ResponseEntity<List<UsersDTO>> getAllUsers() {
        logger.info("Fetching all users...");
        List<UsersDTO> users = usersService.getAllUsers();
        if (users.isEmpty()) {
            throw new FieldNotFoundException("No users found");
        }
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{userName}")
    public ResponseEntity<UsersDTO> getUserByName(@PathVariable String userName) {
        logger.info("Fetching user by Name: {}", userName);
        return usersService.getUserByName(userName)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new FieldNotFoundException("User not found"));
    }

    @PutMapping("/{userName}")
    public ResponseEntity<UsersDTO> updateUser(@PathVariable String userName, @Valid @RequestBody UsersDTO usersDTO) {
        logger.info("Received request to update user name: {}", userName);
        return usersService.updateUser(userName, usersDTO)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new FieldNotFoundException("Failed: User update unsuccessful"));
    }

    @DeleteMapping("/{userName}")
    public ResponseEntity<String> deleteBook(@PathVariable String userName) {
        logger.warn("Attempting to delete User with name: {}", userName);
        boolean isDeleted = usersService.deleteUser(userName);
        if (isDeleted) {
            return ResponseEntity.ok("Success: User deleted successfully");
        } else {
            return ResponseEntity.badRequest().body("Failed: User deletion unsuccessful");
        }
    }
}
