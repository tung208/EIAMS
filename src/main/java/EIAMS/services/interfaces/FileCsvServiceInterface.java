package EIAMS.services.interfaces;

import EIAMS.entities.Student;

import java.util.List;

public interface FileCsvServiceInterface {
    void exportToCsv(List<Student> students, String filePath);
    void importCsvData(String filePath);
}
