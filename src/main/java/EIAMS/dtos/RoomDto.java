package EIAMS.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.Room}
 */
@Data
@AllArgsConstructor
@Builder
public class RoomDto implements Serializable {
    Integer id;
    String name;
    String type;
}