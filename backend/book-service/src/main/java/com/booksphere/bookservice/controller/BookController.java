package com.booksphere.bookservice.controller;

import com.booksphere.bookservice.dto.BookDTO;
import com.booksphere.bookservice.model.Book;
import com.booksphere.bookservice.service.BookService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/add")
    public String addBook(@Valid @RequestBody BookDTO bookDto) {
        return bookService.addBook(convertToEntity(bookDto));
    }

    @GetMapping("/all")
    public List<BookDTO> getAllBooks() {
        return bookService.getAllBooks().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("")
    public List<BookDTO> getBooks() {
        return getAllBooks();
    }

    @GetMapping("/search/title/{title}")
    public List<BookDTO> searchByTitle(@PathVariable String title) {
        return bookService.searchByTitle(title).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/search/author/{author}")
    public List<BookDTO> searchByAuthor(@PathVariable String author) {
        return bookService.searchByAuthor(author).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/delete/{id}")
    public String deleteBook(@PathVariable String id) {
        return bookService.deleteBook(id);
    }

    @GetMapping("/{id}")
    public BookDTO getBookById(@PathVariable String id) {
        return convertToDTO(bookService.getBookById(id));
    }

    @GetMapping("/search/genre/{genre}")
    public List<BookDTO> searchByGenre(@PathVariable String genre) {
        return bookService.searchByGenre(genre).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/update/{id}")
    public String updateBook(@PathVariable String id, @RequestBody BookDTO bookDto) {
        return bookService.updateBook(id, convertToEntity(bookDto));
    }

    @PostMapping("/sync/{query}")
    public String syncBooks(@PathVariable String query) {
        return bookService.syncBooks(query);
    }

    @DeleteMapping("/clear")
    public String clearBooks() {
        return bookService.clearAllBooks();
    }

    @PutMapping("/reduce-stock/{id}/{quantity}")
    public void reduceStock(@PathVariable String id, @PathVariable int quantity) {
        bookService.reduceStock(id, quantity);
    }

    @PutMapping("/increase-stock/{id}/{quantity}")
    public void increaseStock(@PathVariable String id, @PathVariable int quantity) {
        bookService.increaseStock(id, quantity);
    }

    @PutMapping("/randomize-stocks")
    public String randomizeStocks() {
        return bookService.randomizeAllStocks();
    }

    private BookDTO convertToDTO(Book book) {
        if (book == null) return null;
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .genre(book.getGenre())
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .stock(book.getStock())
                .description(book.getDescription())
                .imageUrl(book.getImageUrl())
                .build();
    }

    private Book convertToEntity(BookDTO bookDto) {
        if (bookDto == null) return null;
        Book book = new Book();
        book.setId(bookDto.getId());
        book.setTitle(bookDto.getTitle());
        book.setAuthor(bookDto.getAuthor());
        book.setGenre(bookDto.getGenre());
        book.setIsbn(bookDto.getIsbn());
        book.setPrice(bookDto.getPrice());
        book.setStock(bookDto.getStock());
        book.setDescription(bookDto.getDescription());
        book.setImageUrl(bookDto.getImageUrl());
        return book;
    }
}

