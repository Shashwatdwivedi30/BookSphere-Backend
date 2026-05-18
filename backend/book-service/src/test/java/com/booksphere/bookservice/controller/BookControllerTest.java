package com.booksphere.bookservice.controller;

import com.booksphere.bookservice.model.Book;
import com.booksphere.bookservice.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testEndpoints() throws Exception {
        when(bookService.getAllBooks()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/books/all")).andExpect(status().isOk());

        when(bookService.getBookById(anyString())).thenReturn(new Book());
        mockMvc.perform(get("/books/1")).andExpect(status().isOk());

        String bookJson = "{\"title\":\"T\", \"author\":\"A\", \"isbn\":\"1\", \"genre\":\"G\", \"price\":10.0, \"stock\":10}";

        when(bookService.addBook(any(Book.class))).thenReturn("Success");
        mockMvc.perform(post("/books/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)).andExpect(status().isOk());

        when(bookService.updateBook(anyString(), any(Book.class))).thenReturn("Success");
        mockMvc.perform(put("/books/update/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookJson)).andExpect(status().isOk());

        mockMvc.perform(delete("/books/delete/1")).andExpect(status().isOk());

        when(bookService.searchByTitle(anyString())).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/books/search/title/t")).andExpect(status().isOk());
        
        mockMvc.perform(get("/books/search/author/a")).andExpect(status().isOk());
        mockMvc.perform(get("/books/search/genre/g")).andExpect(status().isOk());
        
        mockMvc.perform(post("/books/sync/java")).andExpect(status().isOk());
        mockMvc.perform(delete("/books/clear")).andExpect(status().isOk());
        mockMvc.perform(put("/books/reduce-stock/1/1")).andExpect(status().isOk());
        mockMvc.perform(put("/books/increase-stock/1/1")).andExpect(status().isOk());
        mockMvc.perform(put("/books/randomize-stocks")).andExpect(status().isOk());
    }
}
