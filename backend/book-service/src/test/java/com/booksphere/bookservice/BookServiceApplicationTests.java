package com.booksphere.bookservice;

import com.booksphere.bookservice.controller.BookController;
import com.booksphere.bookservice.dto.BookDTO;
import com.booksphere.bookservice.model.Book;
import com.booksphere.bookservice.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(properties = "springdoc.api-docs.enabled=false")
class BookServiceApplicationTests {

    @Autowired
    private BookController bookController;

    @Autowired
    private BookService bookService;

    @MockBean
    private com.booksphere.bookservice.repository.BookRepository bookRepository;

    @Test
    void contextLoads() {
        assertThat(bookController).isNotNull();
        assertThat(bookService).isNotNull();
    }

    @Test
    void testBookModelCreation() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Author");
        book.setPrice(100.0);
        assertThat(book.getTitle()).isEqualTo("Test Book");
    }

    @Test
    void testBookServiceBeanExists() {
        assertThat(bookService).isNotNull();
    }

    @Test
    void testBookControllerBeanExists() {
        assertThat(bookController).isNotNull();
    }

    @Test
    void testAddBookIntegration() {
        BookDTO bookDto = BookDTO.builder()
                .title("New Book")
                .author("Author")
                .genre("Genre")
                .isbn("123456")
                .price(100.0)
                .stock(10)
                .build();
        
        Book book = new Book();
        book.setTitle("New Book");
        
        when(bookRepository.save(any(Book.class))).thenReturn(book);
        
        String response = bookController.addBook(bookDto);
        assertThat(response).isEqualTo("Book added successfully");
    }

    @Test
    void testGetAllBooksIntegration() {
        List<Book> books = new ArrayList<>();
        books.add(new Book());
        
        when(bookRepository.findAll()).thenReturn(books);
        
        List<BookDTO> result = bookController.getAllBooks();
        assertThat(result).hasSize(1);
    }

    @Test
    void testBookPriceValidation() {
        Book book = new Book();
        book.setPrice(50.0);
        assertThat(book.getPrice()).isPositive();
    }

    @Test
    void testBookStockValidation() {
        Book book = new Book();
        book.setStock(10);
        assertThat(book.getStock()).isNotNegative();
    }

    @Test
    void testBookGenreField() {
        Book book = new Book();
        book.setGenre("Fiction");
        assertThat(book.getGenre()).isEqualTo("Fiction");
    }
}
