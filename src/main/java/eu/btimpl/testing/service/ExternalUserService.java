package eu.btimpl.testing.service;

import eu.btimpl.testing.dto.ExternalUserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

/**
 *  Never used just for support external api call test
 */
@Service
public class ExternalUserService {
  private final RestClient restClient;

  public ExternalUserService(@Value("${external.service.url}") String baseUrl) {
    this.restClient = RestClient.create(baseUrl);
  }

  public ExternalUserDto fetchExternalUserData(Long id) {
    return restClient.get()
        .uri("/external-users/" + id)
        .retrieve()
        .body(ExternalUserDto.class);
  }
}
