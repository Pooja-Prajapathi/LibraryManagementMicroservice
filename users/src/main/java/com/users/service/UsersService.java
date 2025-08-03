package com.users.service;

import com.users.dto.UsersDTO;
import com.users.entity.Users;
import com.users.exception.ResourceExistsException;
import com.users.mapper.UsersMapper;
import com.users.repository.UsersRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService {
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class);
    private final UsersRepository usersRepository;
    private final UsersMapper usersMapper;

    @Autowired
    public UsersService(UsersRepository usersRepository, UsersMapper usersMapper) {
        this.usersRepository = usersRepository;
        this.usersMapper=usersMapper;
    }

    public UsersDTO createUser(UsersDTO usersDTO){
        logger.info("Received POST request to create employee: {}", usersDTO);
        if (usersRepository.existsById(usersDTO.getUserName())) {
            logger.error("Error creating employee: {}", usersDTO.getUserName());
            throw new ResourceExistsException("Book with ID " + usersDTO.getUserName() + " already exists!");
        }
        if (usersRepository.existsById(usersDTO.getUserName())) {
            logger.error("Error creating books: {}", usersDTO.getUserName());
            throw new ResourceExistsException("Book " + usersDTO.getUserName() + " : Already exists");
        }
        Users users = usersMapper.toUsersEntity(usersDTO);
        Users savedUsers = usersRepository.save(users);
        logger.info("Employee created successfully with ID: {}", savedUsers.getUserName());
        return usersMapper.toUsersDTO(savedUsers);
    }

    public List<UsersDTO> getAllUsers() {
        logger.info("Received GET request to fetch all employees");
        return usersRepository.findAll()
                .stream()
                .map(usersMapper::toUsersDTO)
                .toList();
    }

    public Optional<UsersDTO> getUserByName(String userName) {
        return usersRepository.findById(userName).map(usersMapper::toUsersDTO);
    }

    @Transactional
    public Optional<UsersDTO> updateUser(String userName, UsersDTO updatedUsersDTO) {
        logger.info("Received PUT request to update employee ID: {}", userName);
        return usersRepository.findById(userName).map(existingUsers -> {
            existingUsers.setUserName(
                    updatedUsersDTO.getUserName() != null ? updatedUsersDTO.getUserName() : existingUsers.getUserName());
            existingUsers.setEmail(
                    updatedUsersDTO.getEmail() != null ? updatedUsersDTO.getEmail() : existingUsers.getEmail());
            Users savedUser = usersRepository.save(existingUsers);
            return usersMapper.toUsersDTO(savedUser);
        });
    }

    public boolean deleteUser(String userName){
        logger.warn("Received DELETE request for employee ID: {}", userName);
        if (usersRepository.existsById(userName)) {
            usersRepository.deleteById(userName);
        }
        return !usersRepository.existsById(userName);
    }
}
