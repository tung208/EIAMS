package EIAMS.dtos;

import EIAMS.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotNull
    @NotBlank
    private String username;

    @Email(message = "data not valid")
    private String email;

    @NotNull
    @NotBlank(message = "data not valid")
    private String password;

    @NotNull
    @NotBlank()
    private Role role;

    @NotNull
    @NotBlank()
    private int active;
}