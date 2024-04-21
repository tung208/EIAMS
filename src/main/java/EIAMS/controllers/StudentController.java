package EIAMS.controllers;

import EIAMS.dtos.StudentDto;
import EIAMS.dtos.StudentSubjectDto;
import EIAMS.dtos.SubjectDto;
import EIAMS.entities.StudentSubject;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.entities.Student;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.services.StatusService;
import EIAMS.services.StudentService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    @Autowired
    StatusService statusService;

    @GetMapping()
    public PageResponse<Student> list(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "2") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "") String rollNumber,
            @RequestParam(defaultValue = "") String memberCode,
            @RequestParam(defaultValue = "") String fullName,
            @RequestParam(defaultValue = "") String cmtnd
           ) {
        Page<Student> page = studentService.search( pageNo, pageSize, rollNumber, memberCode, fullName, cmtnd );
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getTotalElements(),page.getContent());
    }

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importStudents(@RequestParam("file") MultipartFile file,@RequestParam("semester_id") int semesterId) throws IOException, EntityNotFoundException, InterruptedException {
        studentService.uploadStudents(file,semesterId);
        statusService.update(semesterId, 5,1);
        Thread.sleep(5000);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
    }

    @PostMapping("/import-profile")
    public ResponseEntity<ResponseObject> importStudentProfile(@RequestParam("file") MultipartFile file,@RequestParam("semester_id") int semesterId) throws IOException, EntityNotFoundException, InterruptedException {
        studentService.uploadCMND(file,semesterId);
        statusService.update(semesterId, 5,2);
        Thread.sleep(5000);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
    }

    @PostMapping("/import-blacklist")
    public ResponseEntity<ResponseObject> importBlackList(@RequestParam("file") MultipartFile file,@RequestParam("semester_id") int semesterId) throws IOException, EntityNotFoundException {
        studentService.uploadBlackList(file,semesterId);
        statusService.update(semesterId, 5,3);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
    }

    @PutMapping("/{id}")
    public void updateStudent(@RequestBody StudentDto studentDto) {
        studentService.update(studentDto);
    }

    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable int id) {
        studentService.delete(id);
    }

    @GetMapping("/subject")
    public PageResponse<StudentSubject> studentSubject(
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "2") Integer pageSize,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam() @NonNull Integer semesterId,
            @RequestParam(defaultValue = "") String rollNumber,
            @RequestParam(defaultValue = "") String subjectCode,
            @RequestParam(defaultValue = "") String groupName,
            @RequestParam(defaultValue = "10") Integer blackList
    ) {
        Page<StudentSubject> page = studentService.searchStudentSubject(pageNo, pageSize,semesterId, rollNumber, subjectCode, groupName, blackList);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getTotalElements(),page.getContent());
    }

    @PutMapping("/subject/{id}")
    public ResponseEntity<ResponseObject> updateStudentSubject(@PathVariable int id, @RequestBody StudentSubjectDto studentSubjectDto) {
        studentService.updateStudentSubject(id, studentSubjectDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update Success", ""));
    }

    @DeleteMapping("/subject/{id}")
    public ResponseEntity<ResponseObject> deleteStudentSubject(@PathVariable int id) {
        studentService.deleteStudentSubject(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delete Success", ""));
    }
    @PostMapping("/subject")
    public ResponseEntity<ResponseObject> createStudentSubject(@RequestBody StudentSubjectDto studentSubjectDto) throws EntityNotFoundException {
        studentService.createStudentSubject(studentSubjectDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update Success", ""));
    }
    @PostMapping()
    public ResponseEntity<ResponseObject> createStudent(@RequestBody StudentDto studentDto) {
        studentService.create(studentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("OK", "Create Success", ""));
    }
}
