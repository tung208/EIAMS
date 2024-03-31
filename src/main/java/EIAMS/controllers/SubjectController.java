package EIAMS.controllers;

import EIAMS.dtos.SubjectDto;
import EIAMS.entities.Semester;
import EIAMS.entities.Subject;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.SubjectService;
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
@RequestMapping(value = "/api/v1/subject")
public class SubjectController {
    @Autowired
    SubjectService subjectService;
    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importSubject(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException {
        subjectService.uploadSubject(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", "Xin chao the gioi"));
    }

    @PostMapping("/import-nolab")
    public ResponseEntity<ResponseObject> importSubjectNoLab(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException {
        subjectService.uploadSubjectNoLab(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", "Xin chao the gioi"));
    }
    @PostMapping("/import-dontmix")
    public ResponseEntity<ResponseObject> importSubjectDontMix(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException {
        subjectService.uploadSubjectDontMix(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", "Xin chao the gioi"));
    }

    @GetMapping()
    public PageResponse<Subject> getSemester(@RequestParam(defaultValue = "1") Integer pageNo,
                                             @RequestParam(defaultValue = "2") Integer pageSize,
                                             @RequestParam(defaultValue = "id") String sortBy,
                                             @RequestParam(defaultValue = "") Integer semesterId,
                                             @RequestParam(defaultValue = "") String code,
                                             @RequestParam(defaultValue = "") String name
    ){
        Page<Subject> page =  subjectService.search(pageNo, pageSize, semesterId, code, name);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getContent());
    }

    @PutMapping
    public ResponseEntity<ResponseObject> update(@RequestBody SubjectDto subjectDto){
        subjectService.update(subjectDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update Success", ""));
    }

    @DeleteMapping
    public ResponseEntity<ResponseObject> delete(@RequestBody int id){
        subjectService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delele Success", ""));
    }
}
