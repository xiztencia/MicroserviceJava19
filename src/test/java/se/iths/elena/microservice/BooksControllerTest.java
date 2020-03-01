package se.iths.elena.microservice;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BooksController.class)
@Import({BooksModelAssembler.class})

public class BooksControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BooksRepository repository;

    @BeforeEach
    void setUp() {
        when(repository.findAll()).thenReturn(List.of(new Book(1L, "Lolita","Vladimir Nabokov", 2011, 361, "Klassiker"), new Book(2L, "Slated", "Teri Terry", 2013, 368, "Familjproblem")));
        when(repository.findById(1L)).thenReturn(Optional.of(new Book(1L, "Lolita", "Vladimir Nabokov", 2011, 361, "Klassiker")));
        when(repository.save(any(Book.class))).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            var b = (Book) args[0];
            return new Book(1L, b.getTitle(), b.getAuthor(), b.getYear(), b.getPages(), b.getGenre());
        });

    }

    @Test
    void getAllReturnsListOfAllBooks() throws Exception {
        mockMvc.perform(
                get("/api/books").contentType("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.bookList[0]._links.self.href", is("http://localhost/api/book/1")))
                .andExpect(jsonPath("_embedded.bookList[0].title", is("Lolita")));
        //Build json paths with: https://jsonpath.com/
    }

    @Test
    @DisplayName("Calls Get method with url /api/books/1")
    void getOneBookWithValidIdOne() throws Exception {
        mockMvc.perform(
                get("/api/books/1").accept("application/hal+json"))
                .andExpect(status().isOk())
                //.andExpect(jsonPath("content[0].links[2].rel", is("self")))
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/books/1")));
    }

    @Test
    @DisplayName("Calls Get method with invalid id url /api/books/3")
    void getOneBookWithInValidIdThree() throws Exception {
        mockMvc.perform(
                get("/api/books/3").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void addNewBookWithPostReturnsCreatedPerson() throws Exception {
        mockMvc.perform(
                post("/api/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"title\":\"Lolita\"}"))
                .andExpect(status().isCreated());
    }


}
