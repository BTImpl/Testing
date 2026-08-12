package eu.btimpl.testing.controller;

import eu.btimpl.testing.config.SecurityConfig;
import eu.btimpl.testing.dto.CreateUserRequest;
import eu.btimpl.testing.dto.User;
import eu.btimpl.testing.service.ExternalUserService;
import eu.btimpl.testing.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;


@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@ExtendWith(MockitoExtension.class)
public class UserControllerCaptorTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private ExternalUserService externalUserService;

  @Captor
  private ArgumentCaptor<CreateUserRequest> requestCaptor;

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void shouldPassCorrectDtoToService_WhenPostUser() throws Exception {
    String validUserJson = """
        {
          "fname": "Tamas",
          "lname": "Bodis",
          "age": 30
        }
        """;

    User mockUser = new User(1L, "Tamas", "Bodis", (short)30);
    BDDMockito.given(userService.createUser(Mockito.any())).willReturn(mockUser);

    mockMvc.perform(post("/api/v1/users")
    .contentType(MediaType.APPLICATION_JSON)
        .content(validUserJson))
        .andExpect(MockMvcResultMatchers.status().isCreated());

    Mockito.verify(userService).createUser(requestCaptor.capture());

    CreateUserRequest capturedRequest = requestCaptor.getValue();

    org.assertj.core.api.Assertions.assertThat(capturedRequest.fname()).isEqualTo("Tamas");
    org.assertj.core.api.Assertions.assertThat(capturedRequest.lname()).isEqualTo("Bodis");
    org.assertj.core.api.Assertions.assertThat(capturedRequest.age()).isEqualTo((short)30);

  }
}
