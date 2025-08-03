package com.library.repository;

import com.library.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LibraryRepository extends JpaRepository<Library, Integer> {

    List<Library> findByUserName(String userName);

    void deleteAllByUserName(String username);

    void deleteAllByBookId(int bookId);

    void deleteByUserNameAndBookId(String username, int bookId);

    long countByUserName(String username);
}
