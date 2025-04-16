package com.example.bookapi.service;

import com.example.bookapi.model.Book;
import com.example.bookapi.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public Book createBook(Book book) {
        // Verificar ISBN usando OpenLibrary API
        String apiUrl = "https://openlibrary.org/api/books?bibkeys=ISBN:" + book.getIsbn() + "&format=json&jscmd=data";
        RestTemplate restTemplate = new RestTemplate();
        var response = restTemplate.getForObject(apiUrl, String.class);

        if (response == null || !response.contains("title")) {
            throw new IllegalArgumentException("ISBN inválido");
        }

        // Aquí validamos campos y ajustamos según la respuesta

        return bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(Long id) {
        return bookRepository.findById(id);
    }

    public Book updateBook(Long id, Book bookDetails) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Libro no encontrado"));

        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setIsbn(bookDetails.getIsbn());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setUrl(bookDetails.getUrl());

        return bookRepository.save(book);
    }

    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Libro no encontrado");
        }
        bookRepository.deleteById(id);
    }
}