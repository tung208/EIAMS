package EIAMS.controllers;

import EIAMS.dtos.LecturerDto;
import EIAMS.entities.ExamCode;
import EIAMS.entities.Lecturer;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityExistException;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.services.LecturerService;
import EIAMS.services.StatusService;
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

    @Autowired
    StatusService statusService;
    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importLecturer(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException, EntityNotFoundException {
        lecturerService.uploadLecturer(file,semesterId);
        statusService.update(semesterId,4,1);
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
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getTotalElements(),page.getContent());
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> create(@RequestBody LecturerDto lecturerDto) throws EntityExistException {
        Lecturer lecturer = lecturerService.create(lecturerDto);
        if (lecturer != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(
                    new ResponseObject("OK", "Create Success", lecturer));
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                new ResponseObject("FAIL", "Create Fail", null));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> update(@PathVariable int id, @RequestBody LecturerDto lecturerDto) throws EntityNotFoundException, EntityExistException {
        lecturerService.update(id, lecturerDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update Success", ""));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable int id) throws EntityExistException {
        lecturerService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delete Success", ""));
    }
}
