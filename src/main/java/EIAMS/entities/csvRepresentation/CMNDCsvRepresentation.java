package EIAMS.entities.csvRepresentation;

import com.opencsv.bean.CsvBindByName;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CMNDCsvRepresentation {
//    @CsvBindByName(column = "RollNumber")
    private String rollNumber;
//    @CsvBindByName(column = "CMTND")
    private String cmtnd;
}
