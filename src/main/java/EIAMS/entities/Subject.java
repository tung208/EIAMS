package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "subject", schema = "eiams", indexes = {
        @Index(name = "semester_index", columnList = "semester_id")
})
public class Subject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id", nullable = false)
    private Integer semesterId;

    @Lob
    @Column(name = "subject_code")
    private String subjectCode;

    @Column(name = "old_subject_code")
    private String oldSubjectCode;

    @Column(name = "short_name")
    private String shortName;

    @Column(name = "subject_name")
    private String subjectName;

    @Column(name = "no_lab")
    private Integer noLab;

    @Column(name = "dont_mix")
    private Integer dontMix;

    @Column(name = "replaced_by")
    private String replacedBy;

}