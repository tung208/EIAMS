package EIAMS.controllers;

import EIAMS.dtos.ExamCodeDto;
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
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(),page.getTotalElements() ,page.getContent());
    }


    @PostMapping()
    public ResponseEntity<ResponseObject> createExamCode(@RequestBody ExamCodeDto examCodeDto
    ){
        ExamCode examCode = examCodeService.create(examCodeDto);
        return ResponseEntity.status(HttpStatus.CREATED).body( new ResponseObject("OK", "Create Success", examCode));
    }

    @PutMapping()
    public ResponseEntity<ResponseObject> updateExamCode(@RequestBody ExamCodeDto examCodeDto
    ){
        examCodeService.update(examCodeDto);
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject("OK", "Create Success", ""));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteExamCode(@PathVariable Integer id
    ){
        examCodeService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body( new ResponseObject("OK", "Delete Success", ""));
    }
}
