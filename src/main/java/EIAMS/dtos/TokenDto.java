package EIAMS.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
public class TokenDto implements Serializable {
    Integer id;
    Integer expired;
    Integer revoked;
    String token;
    String tokenType;
    Integer accountId;
}