package EIAMS.controllers;

import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.SubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
}
