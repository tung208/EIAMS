package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "Student", schema = "schedule", indexes = {
        @Index(name = "roll_number", columnList = "roll_number", unique = true),
        @Index(name = "member_code", columnList = "member_code", unique = true),
        @Index(name = "CMTND", columnList = "CMTND", unique = true)
})
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "roll_number", nullable = false, length = 128)
    private String rollNumber;

    @Column(name = "member_code", nullable = false, length = 128)
    private String memberCode;

    @Column(name = "full_name", length = 128)
    private String fullName;

    @Column(name = "CMTND", length = 64)
    private String cmtnd;

}