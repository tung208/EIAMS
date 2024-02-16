package EIAMS.services.thread;

import EIAMS.entities.StudentSubject;
import EIAMS.repositories.StudentRepository;
import EIAMS.repositories.StudentSubjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@RequiredArgsConstructor
public class SaveStudentSubject implements Runnable{
    private final List<StudentSubject> studentSubjects;
    private final StudentSubjectRepository studentSubjectRepository;
    private final int i;
    @Override
    public void run() {
        try{
            studentSubjectRepository.saveAll(studentSubjects);
        } catch (DataIntegrityViolationException e){
//                e.printStackTrace();
        }
        System.out.println("Task student object "+i+" executed by thread: " + Thread.currentThread().getName());
    }
}
