package EIAMS.services;

import EIAMS.entities.Student;
import EIAMS.helper.Pagination;
import EIAMS.repositories.StudentRepository;
import EIAMS.services.interfaces.StudentServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService implements StudentServiceInterface {
    private final StudentRepository repository;
    private final Pagination pagination;

    @Override
    public Page<Student> list(Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page,limit);
        return repository.findAll(pageable);
    }

    @Override
    public List<Student> list() {
        return repository.findAll();
    }
}
