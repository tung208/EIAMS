package EIAMS.services;

import EIAMS.dtos.StudentDto;
import EIAMS.entities.Semester;
import EIAMS.entities.Student;
import EIAMS.helper.Pagination;
import EIAMS.repositories.SemesterRepository;
import EIAMS.repositories.StudentRepository;
import EIAMS.services.interfaces.StudentServiceInterface;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService implements StudentServiceInterface {
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final Pagination pagination;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Override
    public Page<Student> list(Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        return studentRepository.findAll(pageable);
    }

    @Override
    public List<Student> list() {
        return studentRepository.findAll();
    }

    @Override
    public void update(int id, StudentDto dto) {

        Optional<Student> student = studentRepository.findById(id);
        if (student.isPresent()) {
            Student s = student.get();
            s.setStudentCode(dto.getStudentCode());
            s.setSubject(dto.getSubject());
            s.setEmail(dto.getEmail());
            Optional<Semester> semester = semesterRepository.findById(dto.getSemesterId());
            semester.ifPresent(s::setSemester);
            studentRepository.save(s);
        }
    }

    @Override
    public void delete(int id) {
        studentRepository.deleteById(id);
    }

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
    public void importListStudent(String filePath) throws IOException {
        Map<Integer, Student> csvDataMap = new HashMap<>();

        // Load CSV Data
        String subject = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                Integer id = StringUtils.hasText(data[2]) ? Integer.parseInt(data[0]) : null;
                String email = StringUtils.hasText(data[1]) ? data[1] : null;
                subject = StringUtils.hasText(data[2]) ? data[2] : null;
                String semesterName = StringUtils.hasText(data[2]) ? data[3] : null;

                Semester semester = semesterName != null ? semesterRepository.findByName(semesterName).get() : null;

                if (id != null && email != null && subject != null && semester != null) {
                    Student student = new Student();
                    student.setId(id);
                    student.setEmail(email);
                    student.setSubject(subject);
                    student.setSemester(semester);
                    csvDataMap.put(id, student);
                } else {
                    // Handle the case where any required field is missing or invalid
                    System.out.println("Skipping invalid data: " + line);
                }
            }
        }

        // Update Existing Records and Delete Records Not Present in CSV
        List<Student> existingStudents = studentRepository.findAllBySubject(subject);

        for (Student existingStudent : existingStudents) {
            Integer id = existingStudent.getId();

            if (csvDataMap.containsKey(id)) {
                // Update existing record
                Student updatedStudent = csvDataMap.get(id);
                existingStudent.setEmail(updatedStudent.getEmail());
                existingStudent.setSubject(updatedStudent.getSubject());
                existingStudent.setSemester(updatedStudent.getSemester());
                studentRepository.save(existingStudent);
            } else {
                // Delete record not present in CSV
                studentRepository.delete(existingStudent);
            }
        }

        // Save New Records
        studentRepository.saveAll(csvDataMap.values());
    }
}
