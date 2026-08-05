package eu.btimpl.testing.repository;

import eu.btimpl.testing.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
