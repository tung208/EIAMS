package EIAMS.services;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Account;
import EIAMS.entities.Semester;
import EIAMS.helper.Pagination;
import EIAMS.repositories.AccountRepository;
import EIAMS.repositories.SemesterRepository;
import EIAMS.services.interfaces.SemesterServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SemesterService implements SemesterServiceInterface {
    private final SemesterRepository semesterRepository;
    private final AccountRepository accountRepository;
    private final Pagination pagination;

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
        if(semester.isPresent()){
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
}
