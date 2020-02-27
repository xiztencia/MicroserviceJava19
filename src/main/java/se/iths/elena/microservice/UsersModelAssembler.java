package se.iths.elena.microservice;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
class UsersModelAssembler implements RepresentationModelAssembler <User, EntityModel<User>> {

    @Override
    public EntityModel<User> toModel(User user) {
        return new EntityModel<>(user,
                linkTo(methodOn(UsersController.class).one(user.getId())).withSelfRel(),
                linkTo(methodOn(UsersController.class).all()).withRel("user"));
    }

    @Override
    public CollectionModel<EntityModel<User>> toCollectionModel(Iterable<? extends User> entities) {
        var collection = StreamSupport.stream(entities.spliterator(), false)
                .map(this::toModel)
                .collect(Collectors.toList());

        return new CollectionModel<>(collection,
                linkTo(methodOn(UsersController.class).all()).withSelfRel());
    }
}
