package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "scheduler", schema = "eiams")
public class Scheduler {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id", nullable = false)
    private Integer semesterId;

    @Column(name = "slot_id")
    private Integer slotId;

    @Column(name = "room_id", nullable = false)
    private Integer roomId;

    @Lob
    @Column(name = "subject_code")
    private String subjectCode;

    @Lob
    @Column(name = "exam_code_id")
    private String examCodeId;

    @Lob
    @Column(name = "student_id")
    private String studentId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "lecturer_id", nullable = false)
    private Integer lecturerId;

}