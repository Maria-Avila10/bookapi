package com.example.bookapi;


import com.example.bookapi.model.Book;
import com.example.bookapi.repository.BookRepository;
import com.example.bookapi.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        restTemplate = mock(RestTemplate.class);
    }

    @Test
    void testGetAllBooks() {
        // Simular datos del repositorio
        List<Book> mockBooks = Arrays.asList(
                new Book(1L, "Book1", "Author1", "ISBN1", 2001, "http://example.com/book1"),
                new Book(2L, "Book2", "Author2", "ISBN2", 2002, "http://example.com/book2")
        );

        when(bookRepository.findAll()).thenReturn(mockBooks);

        // Llamada al servicio
        List<Book> books = bookService.getAllBooks();

        // Validaciones
        assertNotNull(books);
        assertEquals(2, books.size());
        assertEquals("Book1", books.get(0).getTitle());

        // Verificar que se llamó al mock
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookById() {
        Book mockBook = new Book(1L, "Book1", "Author1", "ISBN1", 2001, "http://example.com/book1");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        Optional<Book> book = bookService.getBookById(1L);

        assertTrue(book.isPresent());
        assertEquals("Book1", book.get().getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateBookWithValidISBN() {
        // Configurar libro de prueba
        Book mockBook = new Book(null, "Valid Book", "Author", "1234567890", 2023, null);

        // Simular respuesta de OpenLibrary API
        String apiResponse = "{\"ISBN:1234567890\":{\"title\":\"Valid Book\"}}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);

        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        // Crear libro
        Book book = bookService.createBook(mockBook);

        // Validar
        assertNotNull(book);
        assertEquals("Valid Book", book.getTitle());
        verify(bookRepository, times(1)).save(mockBook);
    }

    @Test
    void testCreateBookWithInvalidISBN() {
        // Configurar libro con ISBN inválido
        Book mockBook = new Book(null, "Invalid Book", "Author", "0000", 2023, null);

        // Simular respuesta fallida de OpenLibrary API
        String apiResponse = "{}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);

        // Validar que arroja excepción
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.createBook(mockBook);
        });

        assertEquals("ISBN inválido", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class));
    }

    @Test
    void testUpdateBook() {
        // Configurar libro existente
        Book existingBook = new Book(1L, "Existing Book", "Author", "1234567890", 2023, null);
        Book updatedDetails = new Book(1L, "Updated Book", "Author Updated", "0987654321", 2024, null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedDetails);

        // Actualizar libro
        Book updatedBook = bookService.updateBook(1L, updatedDetails);

        // Validar
        assertNotNull(updatedBook);
        assertEquals("Updated Book", updatedBook.getTitle());
        assertEquals("Author Updated", updatedBook.getAuthor());
        verify(bookRepository, times(1)).save(existingBook);
    }

    @Test
    void testDeleteBook() {
        // Configurar libro existente
        when(bookRepository.existsById(1L)).thenReturn(true);

        // Eliminar libro
        bookService.deleteBook(1L);

        // Verificar interacción
        verify(bookRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteBookNotFound() {
        // Configurar libro no existente
        when(bookRepository.existsById(99L)).thenReturn(false);

        // Validar que lanza excepción al intentar eliminar
        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookService.deleteBook(99L);
        });

        assertEquals("Libro no encontrado", exception.getMessage());
        verify(bookRepository, never()).deleteById(anyLong());
    }
}