package com.coolsoft.restfulwebservices.user;

import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
public class UserResource {

    @Autowired
    private UserDao userDao;

    @GetMapping("/users")
    public List<User> retrieveAllUsers(){
        return userDao.findAll();

    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user){
        User userCreated = userDao.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userCreated.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/users/{userId}")
    public Resource<User> retrieveUser(@PathVariable int userId){
        User user = userDao.findOne(userId);
        if (user == null){
            throw new UserNotFoundException("id"+userId);
        }
        //HATEOAS
        Resource<User> resource = new Resource<User>(user);
        ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        resource.add(linkTo.withRel("all-users"));
        return resource;

    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable int userId){
        User user = userDao.deleteById(userId);
        if (user == null){
            throw new UserNotFoundException("id"+userId);
        }
    }

}
