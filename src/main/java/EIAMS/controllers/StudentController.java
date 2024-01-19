package EIAMS.controllers;

import EIAMS.dtos.StudentDto;
import EIAMS.entities.ResponseObject;
import EIAMS.entities.Student;
import EIAMS.services.interfaces.StudentServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/student")
public class StudentController {

    private final StudentServiceInterface studentService;

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
    public void exportStudents() {
        List<Student> studentList = studentService.list();

        String filePath = "src/main/resources/export/students.csv";

        studentService.exportListStudent(studentList, filePath);
    }

    @PostMapping("/update")
    public void update(@RequestParam int id, @RequestBody StudentDto dto) {
        studentService.update(id,dto);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestParam int id) {
        studentService.delete(id);
    }
}
