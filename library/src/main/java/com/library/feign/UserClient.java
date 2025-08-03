package com.library.feign;

import com.library.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/users")
    List<UserDTO> getAllUsers();

    @GetMapping("/users/{userName}")
    UserDTO getUserByName(@PathVariable("userName") String userName);

    @PostMapping("/users")
    UserDTO createUser(@RequestBody UserDTO userDTO);

    @PutMapping("/users/{userName}")
    UserDTO updateUser(@PathVariable("userName") String userName, @RequestBody UserDTO userDTO);

    @DeleteMapping("/users/{userName}")
    void deleteUser(@PathVariable("userName") String userName);
}
