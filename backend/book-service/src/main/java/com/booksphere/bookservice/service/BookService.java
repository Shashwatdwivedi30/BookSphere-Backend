package com.booksphere.bookservice.service;

import com.booksphere.bookservice.model.Book;
import com.booksphere.bookservice.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.security.SecureRandom;

@Service
public class BookService {

    private static final SecureRandom random = new SecureRandom();
    private static final String BOOK_NOT_FOUND = "Book not found";

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTemplate restTemplate;

    // Add book (ADMIN)
    public String addBook(Book book) {

        if (bookRepository.existsByIsbn(book.getIsbn())) {
            return "Book with this ISBN already exists";
        }

        bookRepository.save(book);
        return "Book added successfully";
    }

    // Get all books (USER + ADMIN)
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Search by title
    public List<Book> searchByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

    // Search by author
    public List<Book> searchByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author);
    }

    // Delete book (ADMIN)
    public String deleteBook(String id) {

        if (!bookRepository.existsById(id)) {
            return BOOK_NOT_FOUND;
        }

        bookRepository.deleteById(id);
        return "Book deleted successfully";
    }

    // Get book by id
    public Book getBookById(String id) {
        return bookRepository.findById(id).orElse(null);
    }

    // Search by genre
    public List<Book> searchByGenre(String genre) {
        return bookRepository.findByGenreIgnoreCase(genre);
    }

    // Update book
    public String updateBook(String id, Book updatedBook) {

        Book existingBook = bookRepository.findById(id).orElse(null);

        if (existingBook == null) {
            return BOOK_NOT_FOUND;
        }

        existingBook.setTitle(updatedBook.getTitle());
        existingBook.setAuthor(updatedBook.getAuthor());
        existingBook.setGenre(updatedBook.getGenre());
        existingBook.setIsbn(updatedBook.getIsbn());
        existingBook.setPrice(updatedBook.getPrice());
        existingBook.setStock(updatedBook.getStock());
        existingBook.setDescription(updatedBook.getDescription());
        existingBook.setImageUrl(updatedBook.getImageUrl());

        bookRepository.save(existingBook);

        return "Book updated successfully";
    }

    // Sync books from Google Books API
    public String syncBooks(String query) {
        try {
            String result = syncFromGoogleBooks(query);
            if (result.contains("0 books synced")) {
                result += " | Falling back to Open Library: " + syncFromOpenLibrary(query);
            }
            return result;
        } catch (Exception e) {
            return "Error syncing books: " + e.getMessage();
        }
    }

    private String syncFromGoogleBooks(String query) {
        String url = "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=20";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                return "0 books synced from Google (No response)";
            }
            List<Map<String, Object>> items = (List<Map<String, Object>>) response.get("items");

            if (items == null || items.isEmpty()) {
                return "0 books synced from Google";
            }

            int count = 0;
            for (Map<String, Object> item : items) {
                if (processGoogleBook(item, query)) {
                    count++;
                }
            }
            return count + " books synced from Google";
        } catch (Exception e) {
            return "Google API Error: " + e.getMessage();
        }
    }

    private boolean processGoogleBook(Map<String, Object> item, String query) {
        Map<String, Object> volumeInfo = (Map<String, Object>) item.get("volumeInfo");
        if (volumeInfo == null) return false;

        String title = Objects.toString(volumeInfo.get("title"), null);
        if (title == null || title.isBlank()) return false;

        List<String> authors = (List<String>) volumeInfo.get("authors");
        String author = (authors != null && !authors.isEmpty()) ? authors.get(0) : "Unknown Author";

        String isbn = extractIsbnFromGoogle(volumeInfo);
        if (isbn == null || bookRepository.existsByIsbn(isbn)) return false;

        List<String> categories = (List<String>) volumeInfo.get("categories");
        String genre = (categories != null && !categories.isEmpty()) ? categories.get(0) : query;
        String description = Objects.toString(volumeInfo.get("description"), "Imported from Google Books");

        String imageUrl = extractImageUrlFromGoogle(volumeInfo, isbn);

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription(description);
        book.setIsbn(isbn);
        book.setGenre(genre);
        book.setImageUrl(imageUrl);
        book.setPrice((double) Math.round(499.0 + (random.nextDouble() * 500))); 
        book.setStock(5 + random.nextInt(46));
        bookRepository.save(book);
        return true;
    }

    private String extractIsbnFromGoogle(Map<String, Object> volumeInfo) {
        List<Map<String, String>> identifiers = (List<Map<String, String>>) volumeInfo.get("industryIdentifiers");
        if (identifiers != null) {
            for (Map<String, String> id : identifiers) {
                if ("ISBN_13".equals(id.get("type")) || "ISBN_10".equals(id.get("type"))) {
                    return id.get("identifier");
                }
            }
        }
        return "N/A-" + System.currentTimeMillis() + "-" + random.nextInt(1000);
    }

    private String extractImageUrlFromGoogle(Map<String, Object> volumeInfo, String isbn) {
        Map<String, String> imageLinks = (Map<String, String>) volumeInfo.get("imageLinks");
        if (imageLinks != null) {
            String url = imageLinks.get("thumbnail");
            return url != null ? url.replace("http:", "https:") : null;
        }
        return "https://books.google.com/books/content?vid=isbn:" + isbn + "&printsec=frontcover&img=1&zoom=1";
    }

    private String syncFromOpenLibrary(String query) {
        String url = "https://openlibrary.org/search.json?q=" + query + "&limit=20";
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) {
                return "0 books synced from Open Library (No response)";
            }
            List<Map<String, Object>> docs = (List<Map<String, Object>>) response.get("docs");

            if (docs == null || docs.isEmpty()) {
                return "0 books synced from Open Library";
            }

            int count = 0;
            for (Map<String, Object> doc : docs) {
                if (processOpenLibraryBook(doc, query)) {
                    count++;
                }
            }
            return count + " books synced from Open Library";
        } catch (Exception e) {
            return "Open Library Error: " + e.getMessage();
        }
    }

    private boolean processOpenLibraryBook(Map<String, Object> doc, String query) {
        String title = Objects.toString(doc.get("title"), null);
        if (title == null) return false;

        List<String> authors = (List<String>) doc.get("author_name");
        String author = (authors != null && !authors.isEmpty()) ? authors.get(0) : "Unknown Author";

        List<String> isbns = (List<String>) doc.get("isbn");
        String isbn = (isbns != null && !isbns.isEmpty()) ? isbns.get(0) : "OL-" + System.currentTimeMillis() + "-" + random.nextInt(1000);

        if (bookRepository.existsByIsbn(isbn)) return false;

        List<String> subjects = (List<String>) doc.get("subject");
        String genre = (subjects != null && !subjects.isEmpty()) ? subjects.get(0) : query;

        String imageUrl = null;
        Object coverI = doc.get("cover_i");
        if (coverI != null) {
            imageUrl = "https://covers.openlibrary.org/b/id/" + coverI + "-L.jpg";
        }

        Book book = new Book();
        book.setTitle(title);
        book.setAuthor(author);
        book.setDescription("Imported from Open Library. A fascinating read in " + genre);
        book.setIsbn(isbn);
        book.setGenre(genre);
        book.setImageUrl(imageUrl);
        book.setPrice((double) Math.round(399.0 + (random.nextDouble() * 600))); 
        book.setStock(10 + random.nextInt(40));
        bookRepository.save(book);
        return true;
    }

    public String clearAllBooks() {
        bookRepository.deleteAll();
        return "All books cleared successfully!";
    }

    public void reduceStock(String id, int quantity) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null && book.getStock() >= quantity) {
            book.setStock(book.getStock() - quantity);
            bookRepository.save(book);
        }
    }

    public void increaseStock(String id, int quantity) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null && quantity > 0) {
            book.setStock(book.getStock() + quantity);
            bookRepository.save(book);
        }
    }

    public String randomizeAllStocks() {
        List<Book> books = bookRepository.findAll();
        int count = 0;
        for (Book book : books) {
            book.setStock(5 + random.nextInt(46));
            bookRepository.save(book);
            count++;
        }
        return count + " books updated with random stock!";
    }
}