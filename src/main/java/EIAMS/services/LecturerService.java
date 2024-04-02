package EIAMS.services;

import EIAMS.dtos.LecturerDto;
import EIAMS.entities.ExamCode;
import EIAMS.entities.Lecturer;
import EIAMS.entities.csvRepresentation.ExamCodeRepresentation;
import EIAMS.entities.csvRepresentation.LecturerRepresentation;
import EIAMS.repositories.LecturerRepository;
import EIAMS.services.excel.ExcelExamCode;
import EIAMS.services.excel.ExcelLecturer;
import EIAMS.services.interfaces.LecturerServiceInterface;
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
public class LecturerService implements LecturerServiceInterface {

    private final LecturerRepository lecturerRepository;

    private static final Logger logger = LoggerFactory.getLogger(LecturerService.class);

    @Override
    @Transactional
    public Integer uploadLecturer(MultipartFile file, int semester_id) throws IOException {
        List<LecturerRepresentation> lecturerRepresentations = new ExcelLecturer().getDataFromExcel(file.getInputStream());

        List<Lecturer> lecturers = new ArrayList<>();

        Lecturer lecturer = null;
        for (LecturerRepresentation item : lecturerRepresentations) {
            int index = item.getEmail().indexOf("@");
            String codeName = item.getEmail().substring(0, index);
            lecturer = Lecturer.builder()
                    .semesterId(semester_id)
                    .email(item.getEmail())
                    .codeName(codeName)
                    .totalSlot(item.getTotalSlot())
                    .examSubject(item.getExamSubject())
                    .build();
            lecturers.add(lecturer);
        }

        lecturerRepository.deleteBySemesterId(semester_id);
        lecturerRepository.saveAll(lecturers);
        return 1;
    }

    @Override
    public Page<Lecturer> search(Integer page, Integer limit, Integer semesterId, String email, String examSubject, int totalSlot) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return lecturerRepository.findByDynamic(semesterId, email, examSubject, totalSlot, pageable);
    }

    @Override
    public Lecturer create(LecturerDto lecturerDto) {
        List<Lecturer> lecturerList = lecturerRepository.findBySemesterIdAndEmail(lecturerDto.getSemesterId(), lecturerDto.getEmail());
        if (lecturerList.size() > 0){
            return null;
        }
        int index = lecturerDto.getEmail().indexOf("@");
        String codeName = lecturerDto.getEmail().substring(0, index);
        Lecturer lecturer = Lecturer.builder()
                .semesterId(lecturerDto.getSemesterId())
                .email(lecturerDto.getEmail())
                .examSubject(lecturerDto.getExamSubject())
                .totalSlot(lecturerDto.getTotalSlot())
                .codeName(codeName)
                .build();
        lecturerRepository.save(lecturer);
        return lecturer;
    }

    @Override
    public void update(int id, LecturerDto lecturerDto) {
        int index = lecturerDto.getEmail().indexOf("@");
        String codeName = lecturerDto.getEmail().substring(0, index);
        Lecturer lecturer = Lecturer.builder()
                .id(id)
                .semesterId(lecturerDto.getSemesterId())
                .email(lecturerDto.getEmail())
                .examSubject(lecturerDto.getExamSubject())
                .codeName(codeName)
                .totalSlot(lecturerDto.getTotalSlot())
                .build();
    }

    @Override
    public void delete(int id) {
        lecturerRepository.deleteById(id);
    }
}
