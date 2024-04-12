package EIAMS.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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

    @NotNull (message = "semesterId not null")
    @NotBlank
    Integer semesterId;
}