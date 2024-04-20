package EIAMS.entities;

import jakarta.persistence.*;
import lombok.*;

import java.awt.*;
import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "ActionLog", schema = "schedule")
public class ActionLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column(name = "semester_id")
    private Integer semesterId;

    @Column(name = "user_name", length = 64)
    private String userName;

    @Column(name = "log_table", length = 128)
    private String logTable;

    @Column(name = "log_action", length = 128)
    private String logAction;

    @Column(name = "log_content")
    private String logContent;

    @Column(name = "since", length = 64)
    private Date since;

}
