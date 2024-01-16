package EIAMS.services;

import EIAMS.entities.Semester;
import EIAMS.entities.Student;
import EIAMS.repositories.SemesterRepository;
import EIAMS.repositories.StudentRepository;
import EIAMS.services.interfaces.FileCsvServiceInterface;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(FileCsvService.class);


    @Override
    public void exportListStudent(List<Student> students, String filePath) {
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
            logger.error(e.getMessage());
        }
    }

    @Override
    public void importListStudent(String filePath) {

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                // Assuming the order of columns in the CSV is: id,email,subject,semester_id
                int id = data[0] != null ? Integer.parseInt(data[0]) : 0;
                String studentCode = data[1];
                String email = data[2];
                String subject = data[3];
                int semesterId = data[4] != null ? Integer.parseInt(data[4]) : 0;

                Optional<Student> student = studentRepository.findById(id);

                if(student.isEmpty() && !studentCode.isEmpty() && !email.isEmpty()) {
                    Student s = new Student();
                    s.setEmail(email);
                    s.setSubject(subject);
                    s.setStudentCode(studentCode);
                    Optional<Semester> semester = semesterRepository.findById(semesterId);
                    semester.ifPresent(s::setSemester);
                    studentRepository.save(s);
                }
                if(student.isPresent() && !studentCode.isEmpty() && !email.isEmpty()) {
                    Student s = student.get();
                    s.setEmail(email);
                    s.setSubject(subject);
                    s.setStudentCode(studentCode);
                    Optional<Semester> semester = semesterRepository.findById(semesterId);
                    semester.ifPresent(s::setSemester);
                    studentRepository.save(s);
                }

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
