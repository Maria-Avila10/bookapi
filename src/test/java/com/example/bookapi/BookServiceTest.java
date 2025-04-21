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
    private BookService bookService; // Inyecta el mock en la instancia real de `BookService`.

    @Mock
    private BookRepository bookRepository; // Mock del repositorio que reemplazará su comportamiento real.

    private RestTemplate restTemplate; // Simula la interacción con servicios externos.

    @BeforeEach
    void setUp() {
        // Inicializa los mocks antes de cada prueba
        MockitoAnnotations.openMocks(this);
        restTemplate = mock(RestTemplate.class); // Mock manual para `RestTemplate`.
    }

    // Prueba para obtener todos los libros.
    @Test
    void testGetAllBooks() {
        // Simula una lista de libros que el repositorio devuelve.
        List<Book> mockBooks = Arrays.asList(
                new Book(1L, "Book1", "Author1", "ISBN1", 2001, "http://example.com/book1"),
                new Book(2L, "Book2", "Author2", "ISBN2", 2002, "http://example.com/book2")
        );

        // Configuración del comportamiento del mock.
        when(bookRepository.findAll()).thenReturn(mockBooks);

        // Llamada al servicio.
        List<Book> books = bookService.getAllBooks();

        // Validaciones del resultado.
        assertNotNull(books); // Verifica que la lista no sea nula.
        assertEquals(2, books.size()); // Verifica que la lista tenga 2 elementos.
        assertEquals("Book1", books.get(0).getTitle()); // Verifica el título del primer libro.

        // Verifica que el methods del repositorio fue llamado exactamente una vez.
        verify(bookRepository, times(1)).findAll();
    }

    // Prueba para obtener un libro por ID.
    @Test
    void testGetBookById() {
        // Simula un libro encontrado en el repositorio.
        Book mockBook = new Book(1L, "Book1", "Author1", "ISBN1", 2001, "http://example.com/book1");
        when(bookRepository.findById(1L)).thenReturn(Optional.of(mockBook));

        // Llamada al servicio.
        Optional<Book> book = bookService.getBookById(1L);

        // Validaciones.
        assertTrue(book.isPresent()); // Confirma que el resultado no está vacío.
        assertEquals("Book1", book.get().getTitle()); // Válida que el título sea correcto.

        // Verifica que el methods findById fue llamado con el ID correcto.
        verify(bookRepository, times(1)).findById(1L);
    }

    // Prueba para crear un libro con un ISBN válido.
    @Test
    void testCreateBookWithValidISBN() {
        // Libro de ejemplo que se desea agregar.
        Book mockBook = new Book(null, "Valid Book", "Author", "1234567890", 0, null);

        // Simula la respuesta de la API de OpenLibrary.
        String apiResponse = "{\"ISBN:1234567890\":{\"title\":\"Valid Book\", \"publish_date\":\"2009\"}}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);

        // Simula la operación de guardar el libro en el repositorio.
        when(bookRepository.save(any(Book.class))).thenReturn(mockBook);

        // Llamada al servicio.
        Book book = bookService.createBook(mockBook);

        // Validaciones.
        assertNotNull(book); // Verifica que el libro creado no sea nulo.
        assertEquals("Valid Book", book.getTitle()); // Valida el título.
        assertEquals(2009, book.getPublicationYear()); // Verifica que el año de publicación es correcto (2009).
        verify(bookRepository, times(1)).save(mockBook); // Verifica que el libro fue guardado.
    }

    // Prueba para crear un libro con un ISBN inválido.
    @Test
    void testCreateBookWithInvalidISBN() {
        // Libro con un ISBN inválido.
        Book mockBook = new Book(null, "Invalid Book", "Author", "0000", 2023, null);

        // Simula una respuesta vacía de la API externa.
        String apiResponse = "{}";
        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);

        // Válida que se lanza la excepción correcta.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            bookService.createBook(mockBook);
        });

        // Valida el mensaje de la excepción.
        assertEquals("ISBN inválido", exception.getMessage());
        verify(bookRepository, never()).save(any(Book.class)); // Verifica que no se intentó guardar el libro.
    }

    // Prueba para actualizar un libro existente.
    @Test
    void testUpdateBook() {
        // Simula un libro existente en el repositorio.
        Book existingBook = new Book(1L, "Existing Book", "Author", "1234567890", 2023, null);
        Book updatedDetails = new Book(1L, "Updated Book", "Author Updated", "0987654321", 2024, null);

        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedDetails);

        // Llamada al servicio.
        Book updatedBook = bookService.updateBook(1L, updatedDetails);

        // Validaciones.
        assertNotNull(updatedBook); // Verifica que el libro actualizado no sea nulo.
        assertEquals("Updated Book", updatedBook.getTitle()); // Valida el nuevo título.
        assertEquals("Author Updated", updatedBook.getAuthor()); // Valida el nuevo autor.
        verify(bookRepository, times(1)).save(existingBook); // Verifica que se guardó el libro.
    }

    // Prueba para eliminar un libro existente.
    @Test
    void testDeleteBook() {
        // Simula que el libro existe.
        when(bookRepository.existsById(1L)).thenReturn(true);

        // Llamada al servicio para eliminar el libro.
        bookService.deleteBook(1L);

        // Verifica que se llamó al methods de eliminación del repositorio.
        verify(bookRepository, times(1)).deleteById(1L);
    }

    // Prueba para eliminar un libro que no existe.
    @Test
    void testDeleteBookNotFound() {
        // Simula que el libro no existe.
        when(bookRepository.existsById(99L)).thenReturn(false);

        // Válida que se lance una excepción al intentar eliminar un libro inexistente.
        Exception exception = assertThrows(RuntimeException.class, () -> {
            bookService.deleteBook(99L);
        });

        // Valida el mensaje de la excepción.
        assertEquals("Libro no encontrado", exception.getMessage());
        verify(bookRepository, never()).deleteById(anyLong()); // Verifica que no se intentó eliminar.
    }
}
