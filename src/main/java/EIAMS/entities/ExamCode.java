package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "exam_code", schema = "eiams", indexes = {
//        @Index(name = "semester", columnList = "semester_id"),
        @Index(name = "semester_index2", columnList = "semester_id")
})
public class ExamCode {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "subject_id")
    private String subjectId;

    @Column(name = "type")
    private String type;

    @Column(name = "semester_id")
    private Integer semesterId;

    @Column(name = "slot_id")
    private Integer slotId;

}