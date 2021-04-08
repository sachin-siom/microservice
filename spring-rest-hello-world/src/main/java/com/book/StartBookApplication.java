package com.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StartBookApplication {

    // start everything
    public static void main(String[] args) {
        SpringApplication.run(StartBookApplication.class, args);
    }

    // run this only on profile 'demo', avoid run this in test
   /*@Profile("demo")
    @Bean
    CommandLineRunner initDatabase(BookRepository repository) {
        return args -> {
            repository.save(new Book(1l,"A Guide to the Bodhisattva Way of Life", "Santideva", new BigDecimal("15.41")));
            repository.save(new Book(2l,"The Life-Changing Magic of Tidying Up", "Marie Kondo", new BigDecimal("9.69")));
            repository.save(new Book(3l,"Refactoring: Improving the Design of Existing Code", "Martin Fowler", new BigDecimal("47.99")));
        };
    }*/
    
}