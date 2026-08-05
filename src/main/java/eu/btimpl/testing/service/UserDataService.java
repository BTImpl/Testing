package eu.btimpl.testing.service;

import eu.btimpl.testing.dto.CreateUserRequest;
import eu.btimpl.testing.dto.User;
import eu.btimpl.testing.model.UserEntity;
import eu.btimpl.testing.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDataService {

  private final UserRepository userRepository;

  public Optional<User> findUserById(Long id) {
    Optional<UserEntity> entity = userRepository.findById(id);
    return entity.map(userEntity -> new User(userEntity.getId(), userEntity.getFname(), userEntity.getLname(), userEntity.getAge()));
  }

  public User saveUser(CreateUserRequest user) {
    UserEntity userEntity = new UserEntity();
    userEntity.setFname(user.fname());
    userEntity.setLname(user.lname());
    userEntity.setAge(user.age());
    UserEntity saved = userRepository.save(userEntity);
    return new User(saved.getId(), saved.getFname(), saved.getLname(), saved.getAge());
  }
}
