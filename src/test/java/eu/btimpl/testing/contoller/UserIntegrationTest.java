package eu.btimpl.testing.controller;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import eu.btimpl.testing.dto.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@WireMockTest(httpPort = 8089)
@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create",
    "external.service.url=http://localhost:8089"
})
@Import(UserIntegrationTest.TestSecurityConfig.class)
public class UserIntegrationTest {

  @TestConfiguration
  static class TestSecurityConfig {
    @Bean
    public UserDetailsService userDetailsService() {
      UserDetails admin = org.springframework.security.core.userdetails.User.builder()
          .username("admin")
          .password("{noop}admin123")
          .roles("ADMIN")
          .build();
      return new InMemoryUserDetailsManager(admin);
    }
  }

  @Container
  @ServiceConnection
  static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16-alpine");

  @LocalServerPort
  private int port;

  @Test
  void shouldFetchExternalDataAndCreateUserInDatabase() {
    stubFor(get(urlEqualTo("/external-users/1"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json")
            .withBody("""
                {
                    "externalId": 1,
                    "status": "VERIFIED"
                }
                """)));

    RestClient restClient = RestClient.builder()
        .baseUrl("http://localhost:" + port)
        .defaultHeaders(headers -> headers.setBasicAuth("admin", "admin123"))
        .build();

    String newUserJson = """
        {
            "fname": "Tamás",
            "lname": "Kovács",
            "age": 30
        }
        """;

    ResponseEntity<User> response = restClient.post()
        .uri("/api/v1/users/external")
        .contentType(MediaType.APPLICATION_JSON)
        .body(newUserJson)
        .retrieve()
        .toEntity(User.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();

    verify(getRequestedFor(urlEqualTo("/external-users/1")));
  }
}