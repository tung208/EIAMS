package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "studentsubject", schema = "eiams", indexes = {
        @Index(name = "semester_index1", columnList = "semester_id")
})
public class StudentSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id")
    private Integer semesterId;

    @Column(name = "roll_number", length = 128)
    private String rollNumber;

    @Column(name = "subject_code", length = 128)
    private String subjectCode;

    @Column(name = "group_name", length = 128)
    private String groupName;

    @Column(name = "black_list")
    private Integer blackList;

}