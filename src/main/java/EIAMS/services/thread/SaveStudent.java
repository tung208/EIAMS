package EIAMS.services.thread;

import EIAMS.entities.Student;
import EIAMS.entities.StudentSubject;
import EIAMS.entities.csvRepresentation.DSSVCsvRepresentation;
import EIAMS.repositories.StudentRepository;
import EIAMS.repositories.StudentSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
public class SaveStudent implements Runnable{

    private final List<Student> students;
    private final int semester_id;
    private final StudentRepository studentRepository;
    private final int i;
    @Override
    public void run() {
//        studentRepository.saveAll(students);
        for (Student element : students) {
            try{
                studentRepository.save(element);
            } catch (DataIntegrityViolationException e){
//                e.printStackTrace();
            }
        }
        System.out.println("Task student " + i + " executed by thread: " + Thread.currentThread().getName());
    }
}
