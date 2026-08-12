package eu.btimpl.testing.controller;

import eu.btimpl.testing.model.UserEntity;
import eu.btimpl.testing.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Optional;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create"
})
public class UserRepositoryTest {
  @Container
  @ServiceConnection
  static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine");

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TestEntityManager entityManager;

  @Test
  void shouldFindUserById(){
    UserEntity userEntity = new UserEntity();
    userEntity.setFname("test");
    userEntity.setLname("user");
    userEntity.setAge((short)3);
    UserEntity savedUser = userRepository.save(userEntity);

    entityManager.flush();
    entityManager.clear();

    Optional<UserEntity> user = userRepository.findById(savedUser.getId());

    org.assertj.core.api.Assertions.assertThat(user).isPresent();
  }
}
