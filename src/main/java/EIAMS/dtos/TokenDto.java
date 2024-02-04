package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Token}
 */
@Data
@AllArgsConstructor
@Builder
public class TokenDto {
    Integer id;
    Integer expired;
    Integer revoked;
    String token;
    String tokenType;
    Integer accountId;
}