package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "exam_code_id")
    private String examCodeId;

    @Column(name = "student_id")
    private String studentId;

    @Column(name = "start_date")
    private LocalDate startDate;

}