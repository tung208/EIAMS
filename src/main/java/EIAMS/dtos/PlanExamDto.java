package EIAMS.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Date;

@Data
@AllArgsConstructor
@Builder
public class PlanExamDto {

    @NonNull
    @NotBlank
    int semesterId;

    @NonNull
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date expectedDate;

    @NonNull
    @NotBlank
    String expectedTime;

    @NonNull
    @NotBlank
    String typeExam;

    @NonNull
    @NotBlank
    String subjectCode;
}
