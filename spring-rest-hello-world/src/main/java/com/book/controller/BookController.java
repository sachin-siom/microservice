package com.book.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.book.domain.Book;
import com.book.exception.BookNotFoundException;
import com.book.exception.BookUnSupportedFieldPatchException;
import com.book.repository.BookRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@RestController
public class BookController {

	private BookRepository repository;
	
	private MeterRegistry metricRegistry;
	
	private Counter pathParamCnt;
	
	private MongoTemplate mongoTemplate;
	
	public BookController(MeterRegistry metricRegistry, BookRepository repository, MongoTemplate mongoTemplate) {
		this.metricRegistry = metricRegistry;
		this.repository = repository;
		this.mongoTemplate = mongoTemplate;
		this.pathParamCnt = metricRegistry.counter("received.rest.call.sachin");
	}

	@GetMapping("/books")
	List<Book> findAll() {
		return repository.findAll();
	}

	@PostMapping("/books")
	@ResponseStatus(HttpStatus.CREATED)
	Book newBook(@RequestBody Book newBook) {
		return repository.save(newBook);
	}

	@GetMapping("/books/{id}")
	Book findOne(@PathVariable Long id) {
		pathParamCnt.increment();
		return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
	}

	@PutMapping("/books/{id}")
	Book saveOrUpdate(@RequestBody Book newBook, @PathVariable Long id) {

		return repository.findById(id).map(x -> {
			x.setName(newBook.getName());
			x.setAuthor(newBook.getAuthor());
			x.setPrice(newBook.getPrice());
			return mongoTemplate.save(x);
		}).orElseGet(() -> {
			newBook.setId(id);
			return repository.save(newBook);
		});
	}

	@PatchMapping("/books/{id}")
	Book patch(@RequestBody Map<String, String> update, @PathVariable Long id) {

		return repository.findById(id).map(x -> {

			String author = update.get("author");
			if (!StringUtils.isEmpty(author)) {
				x.setAuthor(author);

				// better create a custom method to update a value = :newValue where id = :id
				return repository.save(x);
			} else {
				throw new BookUnSupportedFieldPatchException(update.keySet());
			}

		}).orElseGet(() -> {
			throw new BookNotFoundException(id);
		});

	}

	@DeleteMapping("/books/{id}")
	void deleteBook(@PathVariable Long id) {
		repository.deleteById(id);
	}

}
