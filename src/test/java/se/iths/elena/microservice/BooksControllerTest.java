package se.iths.elena.microservice;


import com.fasterxml.jackson.databind.ObjectMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BooksController.class)
@Import({BooksModelAssembler.class})

public class BooksControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    BooksRepository repository;

    @BeforeEach
    void setUp() {
        when(repository.findAll()).thenReturn(List.of(new Book(1L, "Lolita", "Vladimir Nabokov", 2011, 361, "Klassiker"), new Book(2L, "Slated", "Teri Terry", 2013, 368, "Familjproblem")));
        when(repository.findById(1L)).thenReturn(Optional.of(new Book(1L, "Lolita", "Vladimir Nabokov", 2011, 361, "Klassiker")));
        when(repository.save(any(Book.class))).thenAnswer(invocationOnMock -> {
            Object[] args = invocationOnMock.getArguments();
            var b = (Book) args[0];
            return new Book(1L, b.getTitle(), b.getAuthor(), b.getYear(), b.getPages(), b.getGenre());
        });

    }

    @Test
    @DisplayName("Calls Get method with return of list with all books")
    void getAllReturnsListOfAllBooks() throws Exception {
        mockMvc.perform(
                get("/api/books").contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_embedded.bookList[0]._links.self.href", is("http://localhost/api/books/1")))
                .andExpect(jsonPath("_embedded.bookList[0].title", is("Lolita")));
    }

    @Test
    @DisplayName("Calls Get method with url /api/books/1")
    void getOneBookWithValidIdOne() throws Exception {
        mockMvc.perform(
                get("/api/books/1").accept("application/hal+json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("_links.self.href", is("http://localhost/api/books/1")));
    }

    @Test
    @DisplayName("Calls Get method with invalid id url /api/books/3")
    void getOneBookWithInValidIdFive() throws Exception {
        mockMvc.perform(
                get("/api/books/5").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Calls Post method with definite parameters")
    void addNewBookWithPostReturnsCreatedBook() throws Exception {
        mockMvc.perform(
                post("/api/books/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"title\":\"Lolita\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Check through all layers of application")
    void registrationWorksThroughAllLayers() throws Exception {
        Book book = new Book(0L, "Lolita", "Vladimir Nabokov", 2011, 361, "Klassiker");

        mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("title").value("Lolita"))
                .andExpect(jsonPath("id").isNumber());
    }

    @Test
    @DisplayName("Calls Put method with url /api/books/1")
    void replaceBookWithPutReturnsReplacedBook() throws Exception {
        mockMvc.perform(
                put("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"title\":\"Fated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Calls Patch method with url /api/books/1")
    void modifyBookWithPatchReturnsRModifiedBook() throws Exception {
        mockMvc.perform(
                patch("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"title\":\"Fated\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Calls Delete method with url /api/books/1")
    void removeBookWithDeleteReturnsNoBookFound() throws Exception {
        mockMvc.perform(
                delete("/api/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"id\":0,\"title\":\"Lolita\"}"))
                .andExpect(status().isNotFound());
    }
}
