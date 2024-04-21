package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "StudentSubject", schema = "schedule",
        indexes = {
        @Index(name = "semester_index1", columnList = "semester_id")
}
)
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

    @Column(name = "black_list", columnDefinition = "integer default 0")
    private Integer blackList;

    @Override
    public String toString(){
        return "StudentSubject{" +
                ", rollNumber='" + rollNumber + '\'' +
                ", subjectCode='" + subjectCode + '\'' +
                ", groupName='" + groupName + '\'' +
                ", blackList='" + blackList + '\'' +
                '}';
    }
}