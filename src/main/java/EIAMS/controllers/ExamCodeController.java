package EIAMS.controllers;

import EIAMS.entities.ExamCode;
import EIAMS.entities.Semester;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.ExamCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/exam-code")
public class ExamCodeController {
    @Autowired
    ExamCodeService examCodeService;

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importStudents(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException {
        System.out.println("vao day");
        examCodeService.uploadExamCode(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", "Hello world"));
    }

    @GetMapping()
    public PageResponse<ExamCode> getExamCode(@RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "2") Integer pageSize,
                                              @RequestParam(defaultValue = "id") String sortBy,
                                              @RequestParam(defaultValue = "") Integer semesterId,
                                              @RequestParam(defaultValue = "") String subjectCode
    ){
        Page<ExamCode> page =  examCodeService.search(pageNo, pageSize, semesterId, subjectCode);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getContent());
    }
}
