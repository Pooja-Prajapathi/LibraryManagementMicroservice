package com.books.service;

import com.books.dto.BooksDTO;
import com.books.entity.Books;
import com.books.exception.ResourceExistsException;
import com.books.mapper.BooksMapper;
import com.books.repository.BooksRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class BooksService {
    private static final Logger logger = LoggerFactory.getLogger(BooksService.class);
    private final BooksRepository booksRepository;
    private final BooksMapper booksMapper;

    @Autowired
    public BooksService(BooksRepository booksRepository, BooksMapper booksMapper) {
        this.booksRepository = booksRepository;
        this.booksMapper=booksMapper;
    }

    public BooksDTO createBook(BooksDTO booksDTO){
        logger.info("Received POST request to create employee: {}", booksDTO);
        if (booksRepository.existsById(booksDTO.getId())) {
            throw new ResourceExistsException("Book with ID " + booksDTO.getId() + " already exists!");
        }
        Books books = booksMapper.toBooksEntity(booksDTO);
        Books savedBooks = booksRepository.save(books);
        return booksMapper.toBooksDTO(savedBooks);
    }

    public List<BooksDTO> getAllBooks() {
        logger.info("Received GET request to fetch all employees");
        return booksRepository.findAll()
                .stream()
                .map(booksMapper::toBooksDTO)
                .toList();
    }

    public Optional<BooksDTO> getBookById(int id) {
        return booksRepository.findById(id).map(booksMapper::toBooksDTO);
    }

    @Transactional
    public Optional<BooksDTO> updateBook(int id, BooksDTO updatedBooksDTO) {
        logger.info("Received PUT request to update employee ID: {}", id);
        return booksRepository.findById(id).map(existingBooks -> {
            existingBooks.setName(
                    updatedBooksDTO.getName() != null ? updatedBooksDTO.getName() : existingBooks.getName());
            existingBooks.setPublisher(
                    updatedBooksDTO.getPublisher() != null ? updatedBooksDTO.getPublisher() : existingBooks.getPublisher());
            existingBooks.setAuthor(
                    updatedBooksDTO.getAuthor() != null ? updatedBooksDTO.getAuthor() : existingBooks.getAuthor());
            Books savedBook = booksRepository.save(existingBooks);
            return booksMapper.toBooksDTO(savedBook);
        });
    }

    public boolean deleteBook(int id) {
        logger.warn("Received DELETE request for employee ID: {}", id);
        if (booksRepository.existsById(id)) {
            booksRepository.deleteById(id);
        }
        return !booksRepository.existsById(id);
    }
}
