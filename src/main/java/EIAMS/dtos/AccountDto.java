package EIAMS.dtos;

import EIAMS.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Account}
 */
@Data
@AllArgsConstructor
@Builder
public class AccountDto {
    Integer id;
    @NotNull
    Integer active;

    @NotNull
    @Email(message = "Email khong hop le")
    String email;

    @NotNull
    @Size(min = 2, max = 64)
    String password;

    @NotNull
    Role role;

    Integer since;

    @NotNull
    String username;
}