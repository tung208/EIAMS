package EIAMS.services;

import EIAMS.dtos.ExamCodeDto;
import EIAMS.entities.ExamCode;
import EIAMS.entities.Semester;
import EIAMS.entities.csvRepresentation.ExamCodeRepresentation;
import EIAMS.repositories.ExamCodeRepository;
import EIAMS.services.excel.ExcelExamCode;
import EIAMS.services.interfaces.ExamCodeServiceInterface;
import EIAMS.services.thread.SaveExamCode;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ExamCodeService implements ExamCodeServiceInterface {
    
    private final ExamCodeRepository examCodeRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    @Override
    @Transactional
    public Integer uploadExamCode(MultipartFile file, int semester_id) throws IOException {
        List<ExamCodeRepresentation> examCodeRepresentationList = new ExcelExamCode().getDataFromExcel(file.getInputStream());

        List<ExamCode> examCodeList = new ArrayList<>();

        int corePoolSize = 5;
        int maximumPoolSize = 10;
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

        for (ExamCodeRepresentation item: examCodeRepresentationList){
            ExamCode examCode = ExamCode.builder()
                    .semesterId(semester_id)
                    .subjectCode(item.getSubjectCode())
                    .type(item.getType())
                    .exam(item.getExam())
                    .examCode(item.getExamCode())
                    .build();
            examCodeList.add(examCode);
        }

        examCodeRepository.deleteBySemesterId(semester_id);
        for (int i = 0; i < examCodeList.size(); i += sublistSize) {
            int endIndex = Math.min(i + sublistSize, examCodeList.size());
            List<ExamCode> sublist = examCodeList.subList(i, endIndex);
            executor.execute(new SaveExamCode(sublist,examCodeRepository));
        }
        return null;
    }

    @Override
    public Page<ExamCode> search(Integer page, Integer limit, Integer semesterId , String subjectCode) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return examCodeRepository.findByDynamic(semesterId, subjectCode, pageable);
    }

    @Override
    public ExamCode create(ExamCodeDto examCodeDto) {
        ExamCode examCode = ExamCode.builder()
                .semesterId(examCodeDto.getSemesterId())
                .subjectCode(examCodeDto.getSubjectCode())
                .examCode(examCodeDto.getExamCode())
                .exam(examCodeDto.getExam())
                .type(examCodeDto.getType())
                .build();
        examCodeRepository.save(examCode);
        return examCode;
    }

    @Override
    public void update(ExamCodeDto examCodeDto) {
        ExamCode examCode = ExamCode.builder()
                .id(examCodeDto.getId())
                .semesterId(examCodeDto.getSemesterId())
                .subjectCode(examCodeDto.getSubjectCode())
                .examCode(examCodeDto.getExamCode())
                .exam(examCodeDto.getExam())
                .type(examCodeDto.getType())
                .build();
        examCodeRepository.save(examCode);
    }

    @Override
    public void delete(Integer id) {
        examCodeRepository.deleteById(id);
    }

    @Override
    public void deleteSemesterId(int id){
        examCodeRepository.deleteBySemesterId(id);
    }
}
