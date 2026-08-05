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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
public class UserControllerSecurityTest {
  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @MockitoBean
  private ExternalUserService externalUserService;

  @Test
  @WithMockUser(roles = {"ADMIN"})
  public void shouldAllowAdminToGetUser() throws Exception {
    User user = new User(1L, "Tamas", "Bodis", (short) 30);
    BDDMockito.given(userService.getUserById(1L)).willReturn(Optional.of(user));

    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1"))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.fname").value("Tamas"));
  }

  @Test
  @WithMockUser(roles = {"USER"})
  public void shouldBlockNormaUserWith403Forbidden() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/1"))
        .andExpect(MockMvcResultMatchers.status().isForbidden());
  }
}
