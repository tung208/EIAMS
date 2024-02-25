package EIAMS.services.thread;

import EIAMS.entities.ExamCode;
import EIAMS.repositories.ExamCodeRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SaveExamCode implements Runnable{
    private final List<ExamCode> examCodeList;
    private final ExamCodeRepository examCodeRepository;
    @Override
    public void run() {
        try{
            examCodeRepository.saveAll(examCodeList);
        } catch (Exception e){
//            System.out.println(e);
        }
        System.out.println("Task exam code executed by thread: " + Thread.currentThread().getName());
    }
}
