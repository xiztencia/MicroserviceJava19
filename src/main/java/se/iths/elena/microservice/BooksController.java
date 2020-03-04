package se.iths.elena.microservice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class BooksController {

    final BooksRepository repository;
    private final BooksModelAssembler assembler;

    public BooksController(BooksRepository storage, BooksModelAssembler booksModelAssembler) {
        this.repository = storage;
        this.assembler = booksModelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<Book>> all() {
        log.debug("All books called");
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<Book>> one(@PathVariable long id) {
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        log.info("POST create Book " + book);
        var p = repository.save(book);
        log.info("Saved to repository " + p);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(BooksController.class).slash(p.getId()).toUri());
        return new ResponseEntity<>(p, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteBook(@PathVariable Long id) {
        if (repository.existsById(id)) {
            //log.info("Product deleted");
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    ResponseEntity<Book> replaceBook(@RequestBody Book newBook, @PathVariable Long id) {
        return repository.findById(id)
                .map(book -> {
                    book.setTitle(newBook.getTitle());
                    book.setAuthor(newBook.getAuthor());
                    book.setGenre(newBook.getGenre());
                    book.setPages(newBook.getPages());
                    book.setYear(newBook.getYear());
                    repository.save(book);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(BooksController.class).slash(book.getId()).toUri());
                    return new ResponseEntity<>(book, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    ResponseEntity<Book> modifyBook(@RequestBody Book newBook, @PathVariable Long id) {
        return repository.findById(id)
                .map(book -> {
                    if (newBook.getTitle() != null)
                        book.setTitle(newBook.getTitle());
                        book.setAuthor(newBook.getAuthor());
                        book.setGenre(newBook.getGenre());
                        book.setPages(newBook.getPages());
                        book.setYear(newBook.getYear());
                        repository.save(book);
                        HttpHeaders headers = new HttpHeaders();
                        headers.setLocation(linkTo(BooksController.class).slash(book.getId()).toUri());
                        return new ResponseEntity<>(book, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
