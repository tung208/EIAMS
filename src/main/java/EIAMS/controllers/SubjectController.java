package EIAMS.controllers;

import EIAMS.dtos.SubjectDto;
import EIAMS.entities.Semester;
import EIAMS.entities.Subject;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.services.StatusService;
import EIAMS.services.SubjectService;
import jakarta.validation.Valid;
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

    @Autowired
    StatusService statusService;

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importSubject(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException, EntityNotFoundException {
        subjectService.uploadSubject(file,semesterId);
        statusService.update(semesterId, 2 ,1);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
    }

    @PostMapping("/import-nolab")
    public ResponseEntity<ResponseObject> importSubjectNoLab(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException {
        subjectService.uploadSubjectNoLab(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
    }
    @PostMapping("/import-dontmix")
    public ResponseEntity<ResponseObject> importSubjectDontMix(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException {
        subjectService.uploadSubjectDontMix(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
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

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> update(@PathVariable int id, @RequestBody SubjectDto subjectDto) throws EntityNotFoundException {
        subjectService.update(id, subjectDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update Success", ""));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable int id){
        subjectService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delele Success", ""));
    }

    @PostMapping()
    public ResponseEntity<ResponseObject> create(@RequestBody @Valid SubjectDto subjectDto){
        Subject subject = subjectService.create(subjectDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("OK", "Create Success", ""));
    }
}
