package com.book.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.book.domain.Book;

public interface BookRepository extends MongoRepository<Book, Long> {
}
