package EIAMS.controllers;

import EIAMS.entities.Scheduler;
import EIAMS.entities.Student;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/scheduler")
public class SchedulerController {

    @Autowired
    private SchedulerServiceInterface schedulerServiceInterface;

    @GetMapping(path = "/index")
    public ResponseEntity<ResponseObject> list(
            @RequestParam(name = "semesterId") Integer semesterId,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "start_date", defaultValue = "") String start_date,
            @RequestParam(name = "start_date", defaultValue = "") String end_date,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "limit", required = false) Integer limit) {
        Page<Scheduler> list = schedulerServiceInterface.list(
                semesterId, search,start_date, end_date, page, limit
        );
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT FOUND", "", null));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "", list));
        }
    }

    @GetMapping(path = "/arrange")
    public ResponseEntity<ResponseObject> arrangeStudent(@RequestParam(name = "semesterId") Integer semesterId) throws Exception {
        try {
            schedulerServiceInterface.arrangeStudent(semesterId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Arrange Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }
    @GetMapping(path = "/student")
    public ResponseEntity<ResponseObject> listStudent(
            @RequestParam(name = "schedulerId") Integer schedulerId,
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "limit", required = false) Integer limit) {
        Page<Student> list = schedulerServiceInterface.getListStudentInARoom(
                schedulerId, search, page, limit
        );
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new ResponseObject("NOT FOUND", "", null));
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "", list));
        }
    }
}
