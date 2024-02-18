package EIAMS.services.thread;

import EIAMS.entities.Student;
import EIAMS.repositories.StudentRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class SaveCMND implements Runnable{
    private final List<Student> students;
    private final StudentRepository studentRepository;
    @Override
    public void run() {
        for (Student element: students) {
            Optional<Student> optionalStudent = studentRepository.findByRollNumber(element.getRollNumber());
            if (optionalStudent.isPresent()){
                try{
                    Student student = optionalStudent.get();
                    student.setCmtnd(element.getCmtnd());
                    studentRepository.save(student);
                } catch (Exception e){
//                    System.out.println(e.getMessage());
                }
            }
        }
        System.out.println("Task cmnd executed by thread: " + Thread.currentThread().getName());
    }
}
