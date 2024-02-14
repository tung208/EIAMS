package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "student", schema = "eiams")
public class StudentSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id", nullable = false)
    private Integer semesterId;

    @Column(name = "roll_number")
    private String rollNumber;

    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "black_list")
    private int blackList;

}
