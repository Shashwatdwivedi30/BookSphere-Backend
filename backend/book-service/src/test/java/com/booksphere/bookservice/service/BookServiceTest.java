package com.booksphere.bookservice.service;

import com.booksphere.bookservice.model.Book;
import com.booksphere.bookservice.repository.BookRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BookService bookService;

    @Test
    void testAddBook_Exists() {
        Book book = new Book();
        book.setIsbn("123");
        when(bookRepository.existsByIsbn("123")).thenReturn(true);
        String result = bookService.addBook(book);
        assertTrue(result.contains("already exists"));
    }

    @Test
    void testAddBook_Success() {
        Book book = new Book();
        book.setIsbn("123");
        when(bookRepository.existsByIsbn("123")).thenReturn(false);
        String result = bookService.addBook(book);
        assertTrue(result.contains("successfully"));
        verify(bookRepository).save(book);
    }

    @Test
    void testGetAllBooks() {
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(new Book()));
        assertEquals(1, bookService.getAllBooks().size());
    }

    @Test
    void testSearchMethods() {
        when(bookRepository.findByTitleContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());
        when(bookRepository.findByAuthorContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());
        when(bookRepository.findByGenreIgnoreCase(anyString())).thenReturn(Collections.emptyList());
        
        bookService.searchByTitle("t");
        bookService.searchByAuthor("a");
        bookService.searchByGenre("g");
        
        verify(bookRepository).findByTitleContainingIgnoreCase("t");
        verify(bookRepository).findByAuthorContainingIgnoreCase("a");
        verify(bookRepository).findByGenreIgnoreCase("g");
    }

    @Test
    void testDeleteBook() {
        when(bookRepository.existsById("1")).thenReturn(true);
        bookService.deleteBook("1");
        verify(bookRepository).deleteById("1");
        
        when(bookRepository.existsById("2")).thenReturn(false);
        String res = bookService.deleteBook("2");
        assertTrue(res.contains("not found"));
    }

    @Test
    void testUpdateBook() {
        Book existing = new Book();
        when(bookRepository.findById("1")).thenReturn(Optional.of(existing));
        
        Book update = new Book();
        update.setTitle("New");
        String res = bookService.updateBook("1", update);
        assertTrue(res.contains("successfully"));
        assertEquals("New", existing.getTitle());
    }

    @Test
    void testStockMethods() {
        Book book = new Book();
        book.setStock(10);
        when(bookRepository.findById("1")).thenReturn(Optional.of(book));
        
        bookService.reduceStock("1", 5);
        assertEquals(5, book.getStock());
        
        bookService.increaseStock("1", 10);
        assertEquals(15, book.getStock());
    }

    @Test
    void testClearAndRandomize() {
        bookService.clearAllBooks();
        verify(bookRepository).deleteAll();
        
        when(bookRepository.findAll()).thenReturn(Collections.singletonList(new Book()));
        bookService.randomizeAllStocks();
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testGetBookById() {
        Book book = new Book();
        when(bookRepository.findById("1")).thenReturn(Optional.of(book));
        assertEquals(book, bookService.getBookById("1"));
        
        when(bookRepository.findById("2")).thenReturn(Optional.empty());
        assertNull(bookService.getBookById("2"));
    }

    @Test
    void testUpdateBook_NotFound() {
        when(bookRepository.findById("1")).thenReturn(Optional.empty());
        String res = bookService.updateBook("1", new Book());
        assertTrue(res.contains("not found"));
    }

    @Test
    void testReduceStock_NotFoundOrInsufficient() {
        when(bookRepository.findById("1")).thenReturn(Optional.empty());
        bookService.reduceStock("1", 5);
        
        Book book = new Book();
        book.setStock(2);
        when(bookRepository.findById("2")).thenReturn(Optional.of(book));
        bookService.reduceStock("2", 5);
        assertEquals(2, book.getStock());
    }

    @Test
    void testIncreaseStock_NotFoundOrInvalid() {
        when(bookRepository.findById("1")).thenReturn(Optional.empty());
        bookService.increaseStock("1", 5);
        
        Book book = new Book();
        book.setStock(2);
        when(bookRepository.findById("2")).thenReturn(Optional.of(book));
        bookService.increaseStock("2", 0);
        assertEquals(2, book.getStock());
    }

    @Test
    void testSyncBooks_ExceptionGoogle() {
        when(restTemplate.getForObject(contains("googleapis"), eq(Map.class))).thenThrow(new RuntimeException("API Error"));
        String res = bookService.syncBooks("query");
        assertTrue(res.contains("Google API Error"));
    }

    @Test
    void testSyncBooks_GoogleFallbackToOpenLibrary() {
        Map<String, Object> googleResp = new HashMap<>();
        when(restTemplate.getForObject(contains("googleapis"), eq(Map.class))).thenReturn(googleResp);
        
        Map<String, Object> olResp = new HashMap<>();
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "OL Title");
        olResp.put("docs", Collections.singletonList(doc));
        
        when(restTemplate.getForObject(contains("openlibrary"), eq(Map.class))).thenReturn(olResp);
        
        String res = bookService.syncBooks("query");
        assertTrue(res.contains("Falling back to Open Library"));
    }

    @Test
    void testSyncBooks_GoogleNullResponse() {
        when(restTemplate.getForObject(contains("googleapis"), eq(Map.class))).thenReturn(null);
        when(restTemplate.getForObject(contains("openlibrary"), eq(Map.class))).thenReturn(null);
        String res = bookService.syncBooks("query");
        assertTrue(res.contains("Falling back to Open Library"));
        assertTrue(res.contains("0 books synced from Open Library (No response)"));
    }

    @Test
    void testSyncBooks_GoogleProcessValidBook() {
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> item = new HashMap<>();
        Map<String, Object> vol = new HashMap<>();
        vol.put("title", "Valid Title");
        vol.put("authors", Collections.singletonList("Author"));
        
        Map<String, String> id = new HashMap<>();
        id.put("type", "ISBN_13");
        id.put("identifier", "1234567890");
        vol.put("industryIdentifiers", Collections.singletonList(id));
        
        item.put("volumeInfo", vol);
        resp.put("items", Collections.singletonList(item));

        when(restTemplate.getForObject(contains("googleapis"), eq(Map.class))).thenReturn(resp);
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(false);
        
        String res = bookService.syncBooks("query");
        assertTrue(res.contains("1 books synced from Google"));
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testSyncBooks_GoogleProcessDuplicateIsbn() {
        Map<String, Object> resp = new HashMap<>();
        Map<String, Object> item = new HashMap<>();
        Map<String, Object> vol = new HashMap<>();
        vol.put("title", "Valid Title");
        
        Map<String, String> id = new HashMap<>();
        id.put("type", "ISBN_13");
        id.put("identifier", "1234567890");
        vol.put("industryIdentifiers", Collections.singletonList(id));
        
        item.put("volumeInfo", vol);
        resp.put("items", Collections.singletonList(item));

        when(restTemplate.getForObject(contains("googleapis"), eq(Map.class))).thenReturn(resp);
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(true);
        
        String res = bookService.syncBooks("query");
        assertTrue(res.contains("0 books synced from Google"));
    }

    @Test
    void testSyncBooks_OpenLibraryProcessValidBook() {
        when(restTemplate.getForObject(contains("googleapis"), eq(Map.class))).thenReturn(null);
        
        Map<String, Object> olResp = new HashMap<>();
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "OL Title");
        doc.put("isbn", Collections.singletonList("OL-123"));
        olResp.put("docs", Collections.singletonList(doc));
        
        when(restTemplate.getForObject(contains("openlibrary"), eq(Map.class))).thenReturn(olResp);
        when(bookRepository.existsByIsbn("OL-123")).thenReturn(false);
        
        String res = bookService.syncBooks("query");
        assertTrue(res.contains("1 books synced from Open Library"));
        verify(bookRepository).save(any(Book.class));
    }

    @Test
    void testSyncBooks_OpenLibraryProcessDuplicateIsbn() {
        when(restTemplate.getForObject(contains("googleapis"), eq(Map.class))).thenReturn(null);
        
        Map<String, Object> olResp = new HashMap<>();
        Map<String, Object> doc = new HashMap<>();
        doc.put("title", "OL Title");
        doc.put("isbn", Collections.singletonList("OL-123"));
        olResp.put("docs", Collections.singletonList(doc));
        
        when(restTemplate.getForObject(contains("openlibrary"), eq(Map.class))).thenReturn(olResp);
        when(bookRepository.existsByIsbn("OL-123")).thenReturn(true);
        
        String res = bookService.syncBooks("query");
        assertTrue(res.contains("0 books synced from Open Library"));
    }
}
