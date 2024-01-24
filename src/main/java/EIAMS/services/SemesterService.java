package EIAMS.services;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Account;
import EIAMS.entities.Semester;
import EIAMS.entities.Student;
import EIAMS.helper.Pagination;
import EIAMS.repositories.AccountRepository;
import EIAMS.repositories.SemesterRepository;
import EIAMS.services.interfaces.SemesterServiceInterface;
import com.opencsv.CSVWriter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SemesterService implements SemesterServiceInterface {
    private final SemesterRepository semesterRepository;
    private final AccountRepository accountRepository;
    private final Pagination pagination;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    @Override
    public Page<Semester> list(Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        return semesterRepository.findAll(pageable);
    }

    @Override
    public List<Semester> list() {
        return semesterRepository.findAll();
    }

    @Override
    public void update(int id, SemesterDto dto) {
        Optional<Semester> semester = semesterRepository.findById(id);
        if (semester.isPresent()) {
            Semester s = semester.get();
            s.setName(dto.getName());
            Optional<Account> account = accountRepository.findById(dto.getCreatorId());
            account.ifPresent(s::setCreator);
            semesterRepository.save(s);
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
                        semester.getCreator().getEmail(),
                };
                csvWriter.writeNext(data);
            }
            System.out.println("CSV file exported successfully!");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void importListSemester(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");

                // Assuming the order of columns in the CSV is: id,email,subject,semester_id
                int id = data[0] != null ? Integer.parseInt(data[0]) : 0;
                String name = data[1];
                String creatorEmail = data[2];

                Optional<Semester> semester = semesterRepository.findById(id);

                if (semester.isEmpty() && !name.isEmpty() && !creatorEmail.isEmpty()) {
                    Semester s = new Semester();
                    s.setName(name);
                    Optional<Account> account = accountRepository.getAccountByEmail(creatorEmail);
                    account.ifPresent(s::setCreator);
                    semesterRepository.save(s);
                }
                if (semester.isPresent() && !name.isEmpty() && !creatorEmail.isEmpty()) {
                    Semester s = semester.get();
                    s.setName(name);
                    Optional<Account> account = accountRepository.getAccountByEmail(creatorEmail);
                    account.ifPresent(s::setCreator);
                    semesterRepository.save(s);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
