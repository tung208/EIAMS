package EIAMS.services;

import EIAMS.entities.PlanExam;
import EIAMS.entities.Semester;
import EIAMS.entities.csvRepresentation.PlanExamRepresentation;
import EIAMS.repositories.PlanExamRepository;
import EIAMS.services.excel.ExcelPlanExam;
import EIAMS.services.interfaces.PlanExamServiceInterface;
import EIAMS.services.thread.SavePlanExam;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.poi.ss.usermodel.DateUtil;

@Service
@RequiredArgsConstructor
public class PlanExamService implements PlanExamServiceInterface {
    private final PlanExamRepository planExamRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    @Override
    @Transactional
    public Integer uploadPlanExam(MultipartFile file, int semester_id, String type) throws IOException, ParseException {
        List<PlanExamRepresentation> planExamRepresentations = new ExcelPlanExam().getDataFromExcel(file.getInputStream(),type);

        List<PlanExam> planExamList = new ArrayList<>();

        int corePoolSize = 3;
        int maximumPoolSize = 5;
        long keepAliveTime = 60L;
        int queueCapacity = 100;

        // Tạo một ThreadPoolExecutor với các tham số đã cho
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity));

        // Kích thước của danh sách con
        int sublistSize = 100;
        // Định dạng của chuỗi ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
        for (PlanExamRepresentation element: planExamRepresentations) {
            Date expectedDate = DateUtil.getJavaDate((Double.parseDouble(element.getExpectedDate())));
            PlanExam planExam = PlanExam.builder()
                    .semesterId(semester_id)
                    .subjectCode(safeTrim(element.getSubjectCode(),1))
                    .expectedDate(expectedDate)
                    .expectedTime(safeTrim(element.getExpectedTime(),1))
                    .typeExam(safeTrim(element.getTypeExam(),1))
                    .build();
            planExamList.add(planExam);
        }
        planExamRepository.deleteBySemesterId(semester_id);
        for (int i = 0; i < planExamList.size(); i += sublistSize) {
            int endIndex = Math.min(i + sublistSize, planExamList.size());
            List<PlanExam> sublist = planExamList.subList(i, endIndex);
            executor.execute(new SavePlanExam(sublist,planExamRepository));
        }
        return null;
    }

    public static String safeTrim(String str,int mode) {
        if (mode == 1){
            return str == null ? null : str.toUpperCase().trim();
        } else {
            return str == null ? null : str.trim();
        }
    }

    @Override
    public Page<Semester> search(Integer page, Integer limit, Integer semesterId , String subjectCode) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return planExamRepository.findByDynamic(semesterId, subjectCode, pageable);
    }
}
