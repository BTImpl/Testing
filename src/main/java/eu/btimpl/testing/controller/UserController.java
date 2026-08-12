package eu.btimpl.testing.controller;

import eu.btimpl.testing.dto.CreateUserRequest;
import eu.btimpl.testing.dto.User;
import eu.btimpl.testing.exception.UserNotFoundException;
import eu.btimpl.testing.service.ExternalUserService;
import eu.btimpl.testing.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
public record UserController(UserService userService, ExternalUserService externalUserService) {

  @GetMapping("/{id}")
  public ResponseEntity<User> getUserById(@PathVariable long id) {
    return ResponseEntity.ok(userService.getUserById(id).orElseThrow(UserNotFoundException::new));
  }

  /**
   * Simulate external api call, but it never happens
   *
   * @param user insertable user
   * @return original user object
   */
  @PostMapping("/external")
  public ResponseEntity<User> createUser(@RequestBody User user) {
    externalUserService.fetchExternalUserData(1L);
    return ResponseEntity.status(HttpStatus.CREATED).body(user);
  }

  /**
   * Add a new user to the database - working code part
   *
   * @param user insertable user
   * @return saved user object
   */
  @PostMapping
  public ResponseEntity<User> createUser(@Valid @RequestBody CreateUserRequest user) {
    User createdUser = userService.createUser(user);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
        .buildAndExpand(createdUser.id())
        .toUri();

    return ResponseEntity.created(location).body(createdUser);
  }
}
