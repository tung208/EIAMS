package EIAMS.dtos;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.sql.Date;

/**
 * DTO for {@link EIAMS.entities.Semester}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SemesterDto {

    @NotEmpty(message = "Name is not empty")
    private String name;

    @NotEmpty(message = "Code is not empty")
    String code;

    @NotNull
    String from_date;

    @NonNull
    String to_date;

    Integer creatorId;
}