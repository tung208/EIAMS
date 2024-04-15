package EIAMS.controllers;

import EIAMS.dtos.PlanExamDto;
import EIAMS.entities.PlanExam;
import EIAMS.entities.Semester;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.services.PlanExamService;
import EIAMS.services.StatusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/plan-exam")
public class PlanExamController {
    @Autowired
    PlanExamService planExamService;

    @Autowired
    StatusService statusService;

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importPlanExam(@RequestParam("file") MultipartFile file,
                                                        @RequestParam("semester_id") int semesterId,
                                                        @RequestParam("type") String type) throws IOException, ParseException, EntityNotFoundException {
        planExamService.uploadPlanExam(file,semesterId,type);
        statusService.update(semesterId, 1, 1);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK",
                        "Import Success",
                        "Hello World!"));
    }

    @GetMapping()
    public PageResponse<PlanExam> getPlanExam(@RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "2") Integer pageSize,
                                              @RequestParam(defaultValue = "id") String sortBy,
                                              @RequestParam(defaultValue = "") Integer semesterId,
                                              @RequestParam(defaultValue = "") String subjectCode
    ){
        Page<PlanExam> page =  planExamService.search(pageNo, pageSize, semesterId, subjectCode);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getContent());
    }

    @PostMapping
    public ResponseEntity<ResponseObject> createPlanExam(@RequestBody PlanExamDto planExamDto){
        planExamService.create(planExamDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("OK",
                        "Create Success",
                        ""));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> upldatePlanExam(@PathVariable int id, @RequestBody @Valid PlanExamDto planExamDto) throws EntityNotFoundException {
        planExamService.update(id, planExamDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK",
                        "Update Success",
                        ""));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deletePlanExam(@PathVariable int id){
        planExamService.delete((id));
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK",
                        "Delete Success",
                        ""));
    }
}
