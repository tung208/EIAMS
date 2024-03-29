package EIAMS.controllers;

import EIAMS.dtos.SemesterDto;
import EIAMS.entities.Semester;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.SemesterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1/semester")
public class SemesterController {
    @Autowired
    SemesterService semesterService;

    @GetMapping()
    public PageResponse<Semester> getSemester(@RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "2") Integer pageSize,
                                              @RequestParam(defaultValue = "id") String sortBy,
                                              @RequestParam(defaultValue = "") String name,
                                              @RequestParam(defaultValue = "") String code
                                      ){
        Page<Semester> page =  semesterService.search(pageNo, pageSize, name, code);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getContent());
    }

    @PostMapping()
    public ResponseEntity<ResponseObject> postSemester(@RequestBody SemesterDto semesterDto){
        Semester semester = semesterService.create(semesterDto);
        if (semester == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("Fail", "Can't Create Semester !",null));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Create Semester Successfully!", semester));
        }
    }
}
