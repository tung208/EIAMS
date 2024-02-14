package EIAMS.services.thread;

import EIAMS.entities.Student;
import EIAMS.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

@RequiredArgsConstructor
public class SaveStudent implements Runnable{

    private final List<Student> students;
    private final int i;
    private final StudentRepository studentRepository;

    @Override
    public void run() {
        for (Student element : students) {
            try{
                studentRepository.save(element);
            } catch (DataIntegrityViolationException e){
//                e.printStackTrace();
            }
        }
        System.out.println("Task " + i + " executed by thread: " + Thread.currentThread().getName());
    }
}
