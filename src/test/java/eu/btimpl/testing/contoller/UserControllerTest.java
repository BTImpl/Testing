package eu.btimpl.testing.contoller;

import eu.btimpl.testing.config.SecurityConfig;
import eu.btimpl.testing.dto.User;
import eu.btimpl.testing.service.ExternalUserService;
import eu.btimpl.testing.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private ExternalUserService externalUserService;

  @Test
  @WithMockUser(username = "admin_tamas", roles = {"ADMIN"})
  void shouldReturnUser_WhenIdExist() throws Exception {
    User user = new User(1L, "test", "user", (short) 4);
    BDDMockito.given(userService.getUserById(1L)).willReturn(Optional.of(user));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1").accept(MediaType.APPLICATION_JSON))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
        .andExpect(MockMvcResultMatchers.jsonPath("$.fname").value("test"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.lname").value("user"))
        .andExpect(MockMvcResultMatchers.jsonPath("$.age").value(4));

  }

  @Test
  @WithMockUser(username = "admin_tamas", roles = {"ADMIN"})
  void shouldReturn400BadRequest_WhenFnameIsShort() throws Exception {
    String invalidUser = """
        {
        "fname": "te",
        "lname": "user",
        "age": 4
        }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(invalidUser))
        .andExpect(MockMvcResultMatchers.status().isBadRequest());

    BDDMockito.verifyNoInteractions(userService);
  }

}