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
    public List<Semester> list() {
        return semesterRepository.findAll();
    }

    @Override
    public Semester create(SemesterDto semesterDto) {
        Semester semester = SemesterMapping.toEntity(semesterDto);
        System.out.println(semester);
        if (semester == null) {
            return null;
        }
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

    @Override
    public void exportListSemester(List<Semester> semesters, String filePath) {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))) {
            // Writing header
            String[] header = {"ID", "Name", "Creator Email"};
            csvWriter.writeNext(header);

            // Writing data
            for (Semester semester : semesters) {
                String[] data = {
                        String.valueOf(semester.getId()),
                        semester.getName(),
                        accountRepository.findById(semester.getCreatorId()).get().getEmail(),
                };
                csvWriter.writeNext(data);
            }
            System.out.println("CSV file exported successfully!");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void importListSemester(MultipartFile file) {
        Map<Integer, Semester> csvDataMap = new HashMap<>();
        List<Semester> newSemesters = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                Integer id = data[0] != null ? Integer.parseInt(data[0]) : null;
                String name = StringUtils.hasText(data[2]) ? data[2] : null;
                String creatorEmail = StringUtils.hasText(data[2]) ? data[2] : null;

                if (id != null && name != null && creatorEmail != null) {
                    Semester semester = new Semester();
                    semester.setId(id);
                    semester.setName(name);
                    Optional<Account> account = accountRepository.getAccountByEmail(creatorEmail);
                    account.ifPresent(value -> semester.setCreatorId(value.getId()));
                    csvDataMap.put(id, semester);
                } else if (id == null && name != null && creatorEmail != null) {
                    Semester semester = new Semester();
                    semester.setName(name);
                    Optional<Account> account = accountRepository.getAccountByEmail(creatorEmail);
                    account.ifPresent(value -> semester.setCreatorId(value.getId()));
                    newSemesters.add(semester);
                } else {
                    // Handle the case where any required field is missing or invalid
                    System.out.println("Skipping invalid data: " + line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<Semester> existSemesters = semesterRepository.findAll();
        for (Semester existSemester : existSemesters) {
            int id = existSemester.getId();
            if (csvDataMap.containsKey(id)) {
                //TODO: update exist account and delete not exist
                Semester semesterUpdate = csvDataMap.get(id);
                existSemester.setName(semesterUpdate.getName());
                existSemester.setCreatorId(semesterUpdate.getCreatorId());
                semesterRepository.save(semesterUpdate);
            }else {
                semesterRepository.delete(existSemester);
            }
        }
        semesterRepository.saveAll(newSemesters);
    }
}
