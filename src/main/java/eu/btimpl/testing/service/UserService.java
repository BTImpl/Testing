package eu.btimpl.testing.service;

import eu.btimpl.testing.dto.CreateUserRequest;
import eu.btimpl.testing.dto.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserDataService userDataService;

  public Optional<User> getUserById(Long id){
    return userDataService.findUserById(id);
  }

  public User createUser(CreateUserRequest user) {
    return userDataService.saveUser(user);


  }
}
