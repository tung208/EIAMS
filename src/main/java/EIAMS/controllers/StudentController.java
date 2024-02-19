package EIAMS.controllers;

import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.entities.Student;
import EIAMS.services.StudentService;
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

    @GetMapping(path = "/index")
    public ResponseEntity<ResponseObject> list(
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "limit", required = false) Integer limit) {
        Page<Student> list = studentService.list(page, limit);
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT FOUND", "", null));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "", list));
        }
    }

    @GetMapping("/export")
    public ResponseEntity<ResponseObject> exportStudents() {
        try {
            List<Student> studentList = studentService.list();
            String filePath = "src/main/resources/export/students.csv";
//            studentService.exportListStudent(studentList, filePath);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Export Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseObject("FAIL", "Export Fail", e.getMessage()));
        }

    }

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importStudents(@RequestParam("file") MultipartFile file,@RequestParam("semester_id") int semesterId) throws IOException {
        studentService.uploadStudents(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", "Xin chao the gioi"));
    }

    @PostMapping("/import-profile")
    public ResponseEntity<ResponseObject> importStudentProfile(@RequestParam("file") MultipartFile file,@RequestParam("semester_id") int semesterId) throws IOException {
        studentService.uploadCMND(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", "Xin chao the gioi"));
    }

    @PostMapping("/import-blacklist")
    public ResponseEntity<ResponseObject> importBlackList(@RequestParam("file") MultipartFile file,@RequestParam("semester_id") int semesterId) throws IOException {
        studentService.uploadBlackList(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", "Xin chao the gioi"));
    }

    @PostMapping("/update")
    public void update(@RequestParam int id, @RequestBody Student dto) {
        studentService.update(id, dto);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam int id) {
        studentService.delete(id);
    }


}
