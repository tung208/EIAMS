package EIAMS.entities.csvRepresentation;

import com.opencsv.bean.CsvBindByName;

public class CMNDCsvRepresentation {
    @CsvBindByName(column = "RollNumber")
    private String rollNumber;
    @CsvBindByName(column = "CMTND")
    private String cmtnd;
}
