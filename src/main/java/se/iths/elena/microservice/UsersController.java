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
@RequestMapping("/api/users")
@Slf4j
public class UsersController {

    final UsersRepository repository;
    private final UsersModelAssembler assembler;

    public UsersController(UsersRepository storage, UsersModelAssembler usersModelAssembler) {
        this.repository = storage;
        this.assembler = usersModelAssembler;
    }

    @GetMapping
    public CollectionModel<EntityModel<User>> all() {
        log.debug("All users called");
        return assembler.toCollectionModel(repository.findAll());
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<EntityModel<User>> one(@PathVariable long id) {
        return repository.findById(id)
                .map(assembler::toModel)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        log.info("POST create User " + user);
        var p = repository.save(user);
        log.info("Saved to repository " + p);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(linkTo(UsersController.class).slash(p.getId()).toUri());
        //headers.add("Location", "/api/persons/" + p.getId());
        return new ResponseEntity<>(p, headers, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (repository.existsById(id)) {
            //log.info("Product deleted");
            repository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PutMapping("/{id}")
    ResponseEntity<User> replaceUser(@RequestBody User newUser, @PathVariable Long id) {
        return repository.findById(id)
                .map(user -> {
                    user.setName(newUser.getName());
                    repository.save(user);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(UsersController.class).slash(user.getId()).toUri());
                    return new ResponseEntity<>(user, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PatchMapping("/{id}")
    ResponseEntity<User> modifyUser(@RequestBody User newUser, @PathVariable Long id) {
        return repository.findById(id)
                .map(user -> {
                    if (newUser.getName() != null)
                        user.setName(newUser.getName());

                    repository.save(user);
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(linkTo(UsersController.class).slash(user.getId()).toUri());
                    return new ResponseEntity<>(user, headers, HttpStatus.OK);
                })
                .orElseGet(() ->
                        new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
