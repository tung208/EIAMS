package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "planexam", schema = "schedule")
public class PlanExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id", length = 64)
    private Integer semesterId;

    @Column(name = "expected_date")
    private Date expectedDate;

    @Column(name = "expected_time")
    private String expectedTime;

    @Column(name = "type_exam")
    private String typeExam;

    @Column(name = "subject_code", length = 64)
    private String subjectCode;

}