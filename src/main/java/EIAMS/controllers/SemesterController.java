package EIAMS.controllers;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Semester;
import EIAMS.entities.Status;
import EIAMS.entities.Subject;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.repositories.LecturerRepository;
import EIAMS.repositories.SchedulerRepository;
import EIAMS.repositories.StudentSubjectRepository;
import EIAMS.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/semester")
//@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
public class SemesterController {
    @Autowired
    SemesterService semesterService;
    @Autowired
    StatusService statusService;

    @Autowired
    RoomService roomService;

    @Autowired
    PlanExamService planExamService;

    @Autowired
    ExamCodeService examCodeService;

    @Autowired
    SubjectService subjectService;

    @Autowired
    StudentSubjectRepository studentSubjectRepository;

    @Autowired
    SchedulerRepository schedulerRepository;

    @Autowired
    LecturerRepository lecturerRepository;

    @GetMapping()
    public PageResponse<Semester> getSemester(@RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "2") Integer pageSize,
                                              @RequestParam(defaultValue = "id") String sortBy,
                                              @RequestParam(defaultValue = "") String name,
                                              @RequestParam(defaultValue = "") String code
                                      ){
        Page<Semester> page =  semesterService.search(pageNo, pageSize, name, code);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getTotalElements() ,page.getContent());
    }

    @PostMapping()
    public ResponseEntity<ResponseObject> postSemester(@RequestBody @Valid SemesterDto semesterDto){
        Semester semester = semesterService.create(semesterDto);
        statusService.create(semester.getId());
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Create Semester Successfully!", "semester"));

    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateSemester(@PathVariable int id, @RequestBody @Valid SemesterDto semesterDto) throws EntityNotFoundException {
        semesterService.update(id, semesterDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update Successfully!", "semester"));

    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<ResponseObject> deleteSemester(@PathVariable int id){
        semesterService.delete(id);
        statusService.delete(id);
        roomService.deleteSemesterId(id);
        planExamService.deleteSemesterId(id);
        examCodeService.deleteSemesterId(id);
        subjectService.deleteSemesterId(id);
        studentSubjectRepository.deleteBySemesterId(id);
        schedulerRepository.deleteBySemesterId(id);
        lecturerRepository.deleteBySemesterId(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delete Successfully!", "semester"));

    }

}
