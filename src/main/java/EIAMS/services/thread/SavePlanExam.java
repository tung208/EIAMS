package EIAMS.services.thread;

import EIAMS.entities.PlanExam;
import EIAMS.entities.Subject;
import EIAMS.repositories.PlanExamRepository;
import EIAMS.repositories.SubjectRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;
@RequiredArgsConstructor
public class SavePlanExam implements Runnable{
    private final List<PlanExam> planExams;
    private final PlanExamRepository planExamRepository;

    @Override
    public void run() {
        try{
            planExamRepository.saveAll(planExams);
        } catch (Exception e){
//            System.out.println(e);
        }
        System.out.println("Task plan exam executed by thread: " + Thread.currentThread().getName());
    }
}
