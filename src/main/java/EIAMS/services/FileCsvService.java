package EIAMS.services;

import EIAMS.entities.Semester;
import EIAMS.entities.Student;
import EIAMS.repositories.SemesterRepository;
import EIAMS.repositories.StudentRepository;
import EIAMS.services.interfaces.FileCsvServiceInterface;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileCsvService implements FileCsvServiceInterface {
    private final SemesterRepository semesterRepository;
    private final StudentRepository studentRepository;

    @Override
    public void exportToCsv(List<Student> students, String filePath) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))) {
            // Writing header
            String[] header = {"ID", "Email", "Subject", "Semester"};
            csvWriter.writeNext(header);

            // Writing data
            for (Student student : students) {
                String[] data = {
                        String.valueOf(student.getId()),
                        student.getEmail(),
                        student.getSubject(),
                        student.getSemester().getName()
                };
                csvWriter.writeNext(data);
            }
            System.out.println("CSV file exported successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception appropriately, e.g., log it or throw a custom exception
        }
    }

    @Override
    public void importCsvData(String filePath) {

//        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            while ((line = br.readLine()) != null) {
//                String[] data = line.split(",");
//
//                // Assuming the order of columns in the CSV is: id,email,subject,semester_id
//                int id = Integer.parseInt(data[0]);
//                String email = data[1];
//                String subject = data[2];
//                int semesterId = Integer.parseInt(data[3]);
//
//                Student student = new Student();
//                student.setId(id);
//                student.setEmail(email);
//                student.setSubject(subject);
//                // Assuming Semester entity has a method to find by id
//                Optional<Semester> semester = semesterRepository.findById(semesterId);
//                semester.ifPresent(student::setSemester);
//
//                studentRepository.save(student);
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
