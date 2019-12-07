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
import java.util.Optional;

@RestController
@RequestMapping(path="/jpa")
public class UserJPAResource {

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private PostRepository postRepository;

    @GetMapping("/users")
    public List<User> retrieveAllUsers(){
        return repository.findAll();

    }

    @PostMapping("/users")
    public ResponseEntity<Object> createUser(@Valid @RequestBody User user){
        User userCreated = repository.save(user);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userCreated.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/users/{userId}")
    public Resource<User> retrieveUser(@PathVariable int userId){
        Optional<User> user = repository.findById(userId);
        if (!user.isPresent()){
            throw new UserNotFoundException("id"+userId);
        }
        //HATEOAS
        Resource<User> resource = new Resource<User>(user.get());
        ControllerLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllUsers());
        resource.add(linkTo.withRel("all-users"));
        return resource;

    }

    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable int userId){
        Optional<User> user = repository.findById(userId);
        if (!user.isPresent()){
            throw new UserNotFoundException("id"+userId);
        }
        repository.delete(user.get());
    }
    
    @GetMapping("/users/{id}/posts")
    public List<Post> retrieveAllPost(@PathVariable int id){
        final Optional<User> user = repository.findById(id);
        if (!user.isPresent()) {
            throw new UserNotFoundException("id"+id);
        }
        return user.get().getPosts();

    }
    
    @PostMapping("/users/{id}/posts")
    public ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post){
        final Optional<User> userOptional = repository.findById(id);
        if (!userOptional.isPresent()) {
            throw new UserNotFoundException("id"+id);
        }
        
        User user = userOptional.get();
        post.setUser(user);
        postRepository.save(post);
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(post.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

}
