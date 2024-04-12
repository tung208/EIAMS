package EIAMS.services;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Account;
import EIAMS.entities.Semester;
import EIAMS.helper.Pagination;
import EIAMS.mapper.SemesterMapping;
import EIAMS.repositories.AccountRepository;
import EIAMS.repositories.SemesterRepository;
import EIAMS.services.interfaces.SemesterServiceInterface;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.text.ParseException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SemesterService implements SemesterServiceInterface {
    private final SemesterRepository semesterRepository;
    private final AccountRepository accountRepository;
    private final Pagination pagination;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Override
    public Page<Semester> list(Integer page, Integer limit) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return semesterRepository.findAll(pageable);
    }

    @Override
    public Page<Semester> search(Integer page, Integer limit, String name, String code) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return semesterRepository.findByDynamic(name, code, pageable);

    }

    @Override
    public List<Semester> list() {
        return semesterRepository.findAll();
    }

    @Override
    public Semester create(SemesterDto semesterDto) {
        Semester semester = SemesterMapping.toEntity(semesterDto);
        return semesterRepository.save(semester);
    }

    @Override
    public void update(int id, Semester semester) {
        Optional<Semester> s = semesterRepository.findById(id);
        if (s.isPresent()) {
            Semester semesterUpdate = s.get();
            semesterUpdate.setName(semester.getName());
            semesterUpdate.setCreatorId(semester.getCreatorId());
            semesterRepository.save(semesterUpdate);
        }
    }

    @Override
    public void delete(int id) {
        semesterRepository.deleteById(id);
    }


}
