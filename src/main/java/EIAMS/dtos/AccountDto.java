package EIAMS.dtos;

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
public class AccountDto implements Serializable {
    Integer id;
    Integer active;
    String email;
    String password;
    String role;
    Integer since;
    String username;
}