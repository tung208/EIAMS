package EIAMS.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Room}
 */
@Data
@AllArgsConstructor
@Builder
public class RoomDto {
    Integer id;
    String name;
    String type;
    Integer quantityStudent;

    @NotNull
    @NotBlank
    Integer semesterId;
}