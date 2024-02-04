package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "exam_code", schema = "eiams")
public class ExamCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "Subject_id")
    private String subjectId;

    @Column(name = "type")
    private String type;

    @Column(name = "Semester_id")
    private Integer semesterId;

    @Column(name = "Slot_id")
    private Integer slotId;

}