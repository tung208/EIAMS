package EIAMS.services.thread;

import EIAMS.entities.Student;
import EIAMS.entities.StudentSubject;
import EIAMS.entities.csvRepresentation.DSSVCsvRepresentation;
import EIAMS.repositories.StudentRepository;
import EIAMS.repositories.StudentSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@RequiredArgsConstructor
public class SaveStudent implements Runnable{

    private final List<DSSVCsvRepresentation> students;
    private final int semester_id;
    private final StudentRepository studentRepository;
    private final StudentSubjectRepository studentSubjectRepository;

    @Override
    public void run() {
        for (DSSVCsvRepresentation element : students) {
            StudentSubject studentSubject = StudentSubject
                    .builder()
                    .semesterId(semester_id)
                    .rollNumber(element.getRollNumber())
                    .subjectCode(element.getSubjectCode())
                    .build();
            try {
                studentSubjectRepository.save(studentSubject);
            } catch (DataIntegrityViolationException e){
                e.printStackTrace();
            }

            Student student = Student
                    .builder()
                    .rollNumber(element.getRollNumber())
                    .memberCode(element.getMemberCode())
                    .fullName(element.getFullName())
                    .build();
            try{
                studentRepository.save(student);
            } catch (DataIntegrityViolationException e){
//                e.printStackTrace();
            }
        }
        System.out.println("Task " + semester_id + " executed by thread: " + Thread.currentThread().getName());
    }
}
