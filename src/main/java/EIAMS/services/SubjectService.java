package EIAMS.services;

import EIAMS.dtos.SubjectDto;
import EIAMS.entities.Semester;
import EIAMS.entities.StudentSubject;
import EIAMS.entities.Subject;
import EIAMS.entities.csvRepresentation.DontMixRepresentation;
import EIAMS.entities.csvRepresentation.NoLabRepresentation;
import EIAMS.entities.csvRepresentation.SubjectCsvRepresentation;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.repositories.SubjectRepository;
import EIAMS.services.excel.ExcelDontMix;
import EIAMS.services.excel.ExcelNoLab;
import EIAMS.services.excel.ExcelSubject;
import EIAMS.services.interfaces.SubjectServiceInterface;
import EIAMS.services.thread.SaveStudentSubject;
import EIAMS.services.thread.SaveSubject;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
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
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SubjectService implements SubjectServiceInterface {
    private final SubjectRepository subjectRepository;
    @Override
    public Page<Subject> search(Integer page, Integer limit,Integer semesterId , String name, String code) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return subjectRepository.findByDynamic(semesterId, name, code, pageable);

    }
    
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);
    @Override
    @Transactional
    public Integer uploadSubject(MultipartFile file, int semester_id) throws IOException {
        List<SubjectCsvRepresentation> subjectCsvRepresentations = new ExcelSubject().getDataFromExcel(file.getInputStream());

        List<Subject> subjectList = new ArrayList<>();

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
        int sublistSize = 500;
        for (SubjectCsvRepresentation element: subjectCsvRepresentations) {
            Subject subject = Subject.builder()
                    .semesterId(semester_id)
                    .subjectCode(safeTrim(element.getSubjectCode(),1))
                    .oldSubjectCode(safeTrim(element.getOldSubjectCode(),1))
                    .shortName(safeTrim(element.getShortName(),1))
                    .subjectName(safeTrim(element.getSubjectName(),0))
                    .replacedBy(safeTrim(element.getReplacedBy(),1))
                    .dontMix(0)
                    .noLab(0)
                    .build();
            subjectList.add(subject);
        }

        subjectRepository.deleteBySemesterId(semester_id);
        for (int i = 0; i < subjectList.size(); i += sublistSize) {
            int endIndex = Math.min(i + sublistSize, subjectList.size());
            List<Subject> sublist = subjectList.subList(i, endIndex);
            executor.execute(new SaveSubject(sublist,subjectRepository));
        }
        return null;
    }

    @Override
    @Transactional
    public Integer uploadSubjectNoLab(MultipartFile file, int semester_id) throws IOException {
        List<NoLabRepresentation> noLabRepresentations = new ExcelNoLab().getDataFromExcel(file.getInputStream());

        // Kích thước của danh sách con
        int sublistSize = 500;
        for (NoLabRepresentation element: noLabRepresentations) {
            List<Subject> subjects =  subjectRepository.findBySubjectCodeAndSemeterId(safeTrim(element.getSubjectCode(),1), semester_id);
            for (Subject item: subjects){
                item.setNoLab(1);
            }
            subjectRepository.saveAll(subjects);
        }
        return null;
    }

    @Override
    public Integer uploadSubjectDontMix(MultipartFile file, int semester_id) throws IOException {
        List<DontMixRepresentation> dontMixRepresentations = new ExcelDontMix().getDataFromExcel(file.getInputStream());

        // Kích thước của danh sách con
        int sublistSize = 500;
        for (DontMixRepresentation element: dontMixRepresentations) {
            List<Subject> subjects =  subjectRepository.findBySubjectCodeAndSemeterId(safeTrim(element.getSubjectCode(),1), semester_id);
            for (Subject item: subjects){
                item.setDontMix(1);
            }
            subjectRepository.saveAll(subjects);
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
    public void update(int id, SubjectDto subject) throws EntityNotFoundException {
        Optional<Subject> s = subjectRepository.findById(subject.getId());
        if (s.isPresent()) {
            Subject subjectUpdate = Subject.builder()
                    .id(id)
                    .semesterId(subject.getSemesterId())
                    .subjectCode(subject.getSubjectCode())
                    .oldSubjectCode(subject.getOldSubjectCode())
                    .shortName(subject.getShortName())
                    .subjectName(subject.getSubjectName())
                    .noLab(subject.getNoLab())
                    .dontMix(subject.getDontMix())
                    .replacedBy(subject.getReplacedBy())
                    .build();
            subjectRepository.save(subjectUpdate);
        } else throw new EntityNotFoundException("Not found subject");
    }

    @Override
    public void delete(int id){
        subjectRepository.deleteById(id);
    }

    @Override
    public Subject create(SubjectDto subjectDto) {
        Subject subject = Subject.builder()
                .semesterId(subjectDto.getSemesterId())
                .subjectCode(subjectDto.getSubjectCode())
                .oldSubjectCode(subjectDto.getOldSubjectCode())
                .shortName(subjectDto.getShortName())
                .subjectName(subjectDto.getSubjectName())
                .noLab(subjectDto.getNoLab())
                .dontMix(subjectDto.getDontMix())
                .replacedBy(subjectDto.getReplacedBy())
                .build();
        subjectRepository.save(subject);
        return subject;
    }

    @Override
    public void deleteSemesterId(int id){
        subjectRepository.deleteBySemesterId(id);
    }
    @Override
    public List<Subject> getSubjectDontMix(int semesterId, String dontMix) {
        if(Objects.equals(dontMix, "1")) {
            return subjectRepository.findBySemesterIdAndDontMix(semesterId);
        }
        else if(Objects.equals(dontMix, "0")) {
            return subjectRepository.findBySemesterIdAndCanMix(semesterId);
        } else {
            return subjectRepository.findAllBySemesterId(semesterId);
        }
    }
}
