package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ExamCode", schema = "schedule", indexes = {
        @Index(name = "semester", columnList = "semester_id")
})
public class ExamCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id")
    private Integer semesterId;

    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "type")
    private String type;

    @Column(name = "exam")
    private String exam;

    @Column(name = "exam_code")
    private String examCode;

    @Column(name = "slot_id")
    private Integer slotId;
}