# Book API

API para la gestión de libros, integrada con **Open Library** para verificar ISBN y manejar la información relacionada con los libros.

## Funcionalidades
- Listar todos los libros
- Buscar libros por /id
- Agregar nuevos libros
- Actualizar un libro existente
- Eliminar un libro


## Tecnologías utilizadas
- **Java 17**
- **Spring Boot 3.x**
- **Spring Data JPA**
- **H2** (base de datos en memoria para desarrollo y pruebas)
- **Mockito** / **JUnit** para pruebas unitarias

## Cómo ejecutar
1. Clonar el repositorio:
   ```bash
   git clone https://github.com/Maria-Avila10/bookapi.git
   ```
2. Navegar al directorio del proyecto:
   ```bash
   cd bookapi
   ```
3. Ejecutar la aplicación con **Maven**:
   ```bash
   mvn spring-boot:run
   ```
4. La API estará disponible en:
   ```
   http://localhost:8080
   ```

## Documentación de la API
| Método | Endpoint       | Descripción                         |
|--------|----------------|-------------------------------------|
| GET    | `/books`       | Devuelve todos los libros           |
| POST   | `/books`       | Agregar un nuevo libro              |
| GET    | `/books/{id}`  | Devuelve un libro por ID            |
| PUT    | `/books/{id}`  | Actualiza un libro existente por ID |
| DELETE | `/books/{id}`  | Elimina un libro por ID             |

## **Ejemplo JSON para solicitudes POST o PUT**
```json
{
   "title": "El Hobbit",
   "author": "J.R.R. Tolkien",
   "isbn": "978-0261102217",
   "publication_year": 1937
}

```


## Open Library
Esta API se integra con **Open Library** para verificar y complementar la información de los libros, como ISBN, autores, y metadatos adicionales. Puedes visitar su sitio oficial aquí:
[https://openlibrary.org](https://openlibrary.org)

## Licencia

Este proyecto está bajo la **Licencia MIT**.

