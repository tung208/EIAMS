package EIAMS.services;

import EIAMS.dtos.StudentDto;
import EIAMS.entities.Semester;
import EIAMS.entities.Student;
import EIAMS.helper.Pagination;
import EIAMS.repositories.SemesterRepository;
import EIAMS.repositories.StudentRepository;
import EIAMS.services.interfaces.StudentServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService implements StudentServiceInterface {
    private final StudentRepository repository;
    private final SemesterRepository semesterRepository;
    private final Pagination pagination;

    @Override
    public Page<Student> list(Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        return repository.findAll(pageable);
    }

    @Override
    public List<Student> list() {
        return repository.findAll();
    }

    @Override
    public void update(int id, StudentDto dto) {

        Optional<Student> student = repository.findById(id);
        if (student.isPresent()) {
            Student s = student.get();
            s.setStudentCode(dto.getStudentCode());
            s.setSubject(dto.getSubject());
            s.setEmail(dto.getEmail());
            Optional<Semester> semester = semesterRepository.findById(dto.getSemesterId());
            semester.ifPresent(s::setSemester);
            repository.save(s);
        }
    }

    @Override
    public void delete(int id) {
        repository.deleteById(id);
    }
}
