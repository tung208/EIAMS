package EIAMS.services.interfaces;

import EIAMS.entities.Student;

import java.util.List;

public interface FileCsvServiceInterface {
    void exportListStudent(List<Student> students, String filePath);
    void importListStudent(String filePath);
}
