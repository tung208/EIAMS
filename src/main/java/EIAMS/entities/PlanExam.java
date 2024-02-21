package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "planexam", schema = "eiams")
public class PlanExam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id", length = 64)
    private String semesterId;

    @Column(name = "start_time", length = 64)
    private String startTime;

    @Column(name = "end_time", length = 64)
    private String endTime;

    @Column(name = "subject_code", length = 64)
    private String subjectCode;

}