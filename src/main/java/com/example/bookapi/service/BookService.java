package com.example.bookapi.service;

import com.example.bookapi.model.Book;
import com.example.bookapi.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
        String response = restTemplate.getForObject(apiUrl, String.class);

        // Procesamos la respuesta JSON con Jackson
        if (response == null || response.isEmpty()) {
            throw new IllegalArgumentException("ISBN inválido o no encontrado.");
        }

        try {
            // Usamos ObjectMapper para convertir la respuesta en un JsonNode
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response);

            // Extraemos los datos del libro usando el ISBN
            JsonNode bookData = jsonResponse.get("ISBN:" + book.getIsbn());
            if (bookData != null) {
                // Si el título está presente, agregamos la URL de OpenLibrary
                if (bookData.has("title")) {
                    String url = "https://openlibrary.org" + bookData.get("key").asText();
                    book.setUrl(url);
                }

                // Asignamos el año solo si el usuario no lo proporcionó
                if (book.getPublicationYear() == 0 && bookData.has("publish_date")) {
                    // Si no se ha enviado un año, tomamos el primero del campo "publish_date" de OpenLibrary
                    String publishDate = bookData.get("publish_date").asText();
                    if (publishDate != null && !publishDate.isEmpty()) {
                        // Suponemos que el formato del año está al principio de la cadena, por ejemplo, "2009"
                        try {
                            int year = Integer.parseInt(publishDate.split(" ")[0]);
                            book.setPublicationYear(year);
                        } catch (NumberFormatException e) {
                            // Si el formato es incorrecto o no se puede parsear, dejamos el valor por defecto
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Error al procesar la respuesta de OpenLibrary", e);
        }

        // Guardar el libro en la base de datos
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
