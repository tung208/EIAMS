package EIAMS.controllers;

import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.PlanExamService;
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
import java.text.ParseException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/plan-exam")
public class PlanExamController {
    @Autowired
    PlanExamService planExamService;

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importPlanExam(@RequestParam("file") MultipartFile file,
                                                        @RequestParam("semester_id") int semesterId,
                                                        @RequestParam("type") String type) throws IOException, ParseException {
        System.out.println("import PlanExam");
        planExamService.uploadPlanExam(file,semesterId,type);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK",
                        "Import Success",
                        "Hello World!"));
    }
}
