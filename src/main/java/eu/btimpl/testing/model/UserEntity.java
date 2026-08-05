package eu.btimpl.testing.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "users")
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "fname", nullable = false)
  private String fname;

  @Column(name = "lname", nullable = false)
  private String lname;

  @Column(name = "age")
  private Short age;
}
