package EIAMS.dtos;

import jakarta.annotation.Nonnull;
import lombok.*;

import java.io.Serializable;

/**
 * DTO for {@link EIAMS.entities.ExamCode}
 */
@Data
@AllArgsConstructor
@Builder
@Setter
@Getter
public class ExamCodeDto {
    Integer id;
    @Nonnull
    String subjectCode;

    @NonNull
    Integer semesterId;

    Integer slotId;

    @NonNull
    String examCode;

    String type;

    String exam;
}