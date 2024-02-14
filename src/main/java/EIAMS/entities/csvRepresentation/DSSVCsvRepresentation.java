package EIAMS.entities.csvRepresentation;


import com.opencsv.bean.CsvBindByName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DSSVCsvRepresentation {

    @CsvBindByName(column = "SubjectCode")
    private String subjectCode;
    @CsvBindByName(column = "RollNumber")
    private String rollNumber;
    @CsvBindByName(column = "MemberCode")
    private String memberCode;
    @CsvBindByName(column = "FullName")
    private String fullName;
    @CsvBindByName(column = "GroupName")
    private String groupName;
}
