package eu.btimpl.testing.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank
    @Size(min = 3, max = 25)
    String fname,
    @NotBlank
    @Size(min = 3, max = 25)
    String lname,
    @Min(value = 1)
    @Max(value = 130)
    Short age
) {
}
