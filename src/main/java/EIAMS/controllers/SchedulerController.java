package EIAMS.controllers;

import EIAMS.dtos.SchedulerDetailDto;
import EIAMS.dtos.StudentScheduleDto;
import EIAMS.entities.Room;
import EIAMS.entities.Scheduler;
import EIAMS.entities.Student;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/scheduler")
public class SchedulerController {

    @Autowired
    private SchedulerServiceInterface schedulerServiceInterface;

    @GetMapping(path = "/index")
    public ResponseEntity<ResponseObject> list(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "start_date", defaultValue = "") String start_date,
            @RequestParam(name = "end_date", defaultValue = "") String end_date,
            @RequestParam(name = "lecturer_id", defaultValue = "") String lecturer_id,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "limit", required = false) Integer limit) {
        try {
            List<Room> list = schedulerServiceInterface.list(search, start_date, end_date, lecturer_id);
            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("NOT FOUND", "", null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "", list));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }

    @GetMapping(path = "/list-by-room")
    public ResponseEntity<ResponseObject> listByRoom(
            @RequestParam(name = "room_id") Integer roomId,
            @RequestParam(name = "lecturer_id", defaultValue = "") String lecturerId,
            @RequestParam(name = "start_date", defaultValue = "") String start_date,
            @RequestParam(name = "end_date", defaultValue = "") String end_date) {
        try {
            List<Scheduler> list = schedulerServiceInterface.listSchedulerByRoom(roomId, start_date, end_date, lecturerId);
            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("NOT FOUND", "", null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "", list));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }

    @GetMapping(path = "/get")
    public ResponseEntity<ResponseObject> getById(
            @RequestParam(name = "id") Integer id){
        try {
            SchedulerDetailDto s = schedulerServiceInterface.get(id);
            if (s == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("NOT FOUND", "", null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "", s));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }

    @GetMapping(path = "/arrange")
    public ResponseEntity<ResponseObject> arrangeStudent(@RequestParam(name = "semester_id") Integer semesterId) throws Exception {
        try {
            schedulerServiceInterface.arrangeStudent(semesterId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Arrange Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }

    @GetMapping(path = "/student")
    public ResponseEntity<ResponseObject> listStudent(
            @RequestParam(name = "scheduler_id") Integer schedulerId,
            @RequestParam(name = "search", defaultValue = "") String search) {
        try {
            List<StudentScheduleDto> list = schedulerServiceInterface.getListStudentInARoom(
                    schedulerId, search
            );
            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("NOT FOUND", "", null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "", list));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }

    @GetMapping(path = "/get-by-subject")
    public ResponseEntity<ResponseObject> listScheduler(
            @RequestParam(name = "semester_id") Integer semesterId,
            @RequestParam(name = "subject_code", defaultValue = "") String subjectCode) {
        try {
            List<SchedulerDetailDto> list = schedulerServiceInterface.getListSchedulerBySubjectCode(
                    semesterId, subjectCode
            );
            if (list.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        new ResponseObject("NOT FOUND", "", null));
            } else {
                return ResponseEntity.status(HttpStatus.OK).body(
                        new ResponseObject("OK", "", list));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }

    @GetMapping(path = "/set-exam-code")
    public ResponseEntity<ResponseObject> setExamCode(@RequestParam(name = "semester_id") Integer semesterId) {
        try {
            schedulerServiceInterface.setExamCode(semesterId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Set ExamCode Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }

    @GetMapping(path = "arrange-lecturer")
    public ResponseEntity<ResponseObject> arrangeLecturer(@RequestParam(name = "semester_id") Integer semesterId) {
        try {
            schedulerServiceInterface.arrangeLecturer(semesterId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Arrange Lecturer Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }
    @PostMapping(path = "update-lecturer")
    public ResponseEntity<ResponseObject> updateLecture(@RequestParam(name = "scheduler_id") Integer schedulerId,
                                                        @RequestBody() Integer lecturerId) {
        try {
            schedulerServiceInterface.updateLecturer(schedulerId,lecturerId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Update Lecture Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }

    @PostMapping(path = "swap-lecturer")
    public ResponseEntity<ResponseObject> swapLecture(@RequestParam(name = "scheduler_id") Integer schedulerId,
                                                        @RequestBody() Integer schedulerSwapId) {
        try {
            schedulerServiceInterface.swapLecturer(schedulerId,schedulerSwapId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Update Lecture Success", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new ResponseObject("Fail", e.getMessage(), null));
        }
    }
}
