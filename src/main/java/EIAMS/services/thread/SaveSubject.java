package EIAMS.services.thread;

import EIAMS.entities.StudentSubject;
import EIAMS.entities.Subject;
import EIAMS.repositories.StudentSubjectRepository;
import EIAMS.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SaveSubject implements Runnable{
    private final List<Subject> subjectList;
    private final SubjectRepository subjectRepository;

    @Override
    public void run() {
        try{
            subjectRepository.saveAll(subjectList);
        } catch (Exception e){
//            System.out.println(e);
        }
        System.out.println("Task subject executed by thread: " + Thread.currentThread().getName());
    }
}
