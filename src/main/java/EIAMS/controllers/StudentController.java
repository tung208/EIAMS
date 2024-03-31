package EIAMS.controllers;

import EIAMS.dtos.StudentDto;
import EIAMS.entities.StudentSubject;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.entities.Student;
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
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getContent());
    }

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importStudents(@RequestParam("file") MultipartFile file,@RequestParam("semester_id") int semesterId) throws IOException {
        studentService.uploadStudents(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
    }

    @PostMapping("/import-profile")
    public ResponseEntity<ResponseObject> importStudentProfile(@RequestParam("file") MultipartFile file,@RequestParam("semester_id") int semesterId) throws IOException {
        studentService.uploadCMND(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
    }

    @PostMapping("/import-blacklist")
    public ResponseEntity<ResponseObject> importBlackList(@RequestParam("file") MultipartFile file,@RequestParam("semester_id") int semesterId) throws IOException {
        studentService.uploadBlackList(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
    }

    @PutMapping("/update")
    public void updateStudent(@RequestBody StudentDto studentDto) {
        studentService.update(studentDto);
    }

    @DeleteMapping("/delete")
    public void deleteStudent(@RequestParam int id) {
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
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getContent());
    }

    @PutMapping("/subject")
    public ResponseEntity<ResponseObject> updateStudentSubject() {

        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update Success", ""));
    }
}
