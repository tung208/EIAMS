package EIAMS.dtos;


import jakarta.annotation.Nonnull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

@Data
@AllArgsConstructor
@Builder
public class PlanExamDto {

    @NotNull (message = "Semester_id not null value")
    Integer semesterId;

    @NotNull (message = "expectedDate not null value")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date expectedDate;

    @NotNull (message = "expectedTime not null value")
    @NotBlank
    String expectedTime;

    @NotNull (message = "typeExam not null value")
    @NotBlank
    String typeExam;

    @NotNull (message = "subjectCode not null value")
    @NotBlank
    String subjectCode;
}
