package EIAMS.controllers;

import EIAMS.entities.ExamCode;
import EIAMS.entities.Lecturer;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.LecturerService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/api/v1/lecturer")
public class LecturerController {
    @Autowired
    private LecturerService lecturerService;

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importLecturer(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException {
        lecturerService.uploadLecturer(file,semesterId);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", ""));
    }

    @GetMapping()
    public PageResponse<Lecturer> getExamCode(@RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "2") Integer pageSize,
                                              @RequestParam(defaultValue = "id") String sortBy,
                                              @RequestParam() @NonNull Integer semesterId,
                                              @RequestParam(defaultValue = "") String email,
                                              @RequestParam(defaultValue = "") String examSubject,
                                              @RequestParam(defaultValue = "0") int totalSlot
    ){
        Page<Lecturer> page =  lecturerService.search(pageNo, pageSize, semesterId, email, examSubject, totalSlot);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getContent());
    }
}
