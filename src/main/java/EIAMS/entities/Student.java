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
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "SubjectCode", length = 128)
    private String subjectCode;

    @Column(name = "RollNumber", length = 128)
    private String rollNumber;

    @Column(name = "MemberCode", length = 128)
    private String memberCode;

    @Column(name = "FullName", length = 128)
    private String fullName;

    @Column(name = "CMTND", length = 64)
    private String cmtnd;

    @Column(name = "semester_id", nullable = false)
    private Integer semesterId;

    @Lob
    @Column(name = "black_list")
    private String blackList;

}