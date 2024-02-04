package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "subject", schema = "eiams")
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id", nullable = false)
    private Integer semesterId;

    @Column(name = "Subject_Code")
    private String subjectCode;

    @Column(name = "Old_Subject_Code")
    private String oldSubjectCode;

    @Column(name = "Short_Name")
    private String shortName;

    @Column(name = "Subject_Name")
    private String subjectName;

    @Column(name = "No_Lab")
    private Integer noLab;

    @Column(name = "Dont_mix")
    private Integer dontMix;

    @Column(name = "Replaced_by")
    private String replacedBy;

}