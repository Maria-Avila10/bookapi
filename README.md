# Book API

API para la gestión de libros, integrada con OpenLibrary para verificar ISBN y manejar la información de libros.

## Funcionalidades
- Listar todos los libros
- Buscar libros por título
- Agregar nuevos libros
- Verificar ISBN con OpenLibrary

## Tecnologías utilizadas
- **Java 17**
- **Spring Boot 2.x**
- **Spring Data JPA**
- **H2 (base de datos en memoria para desarrollo y pruebas)**
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

## Documentación de la API
| Método | Endpoint       | Descripción                |
   |--------|----------------|----------------------------|
| GET    | `/books`       | Devuelve todos los libros  |
| POST   | `/books`       | Agregar un nuevo libro     |
| GET    | `/books/{id}`  | Devuelve un libro por ID   |

## Licencia
Este proyecto está bajo la Licencia MIT.