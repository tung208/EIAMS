package EIAMS.controllers;

import EIAMS.entities.Scheduler;
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

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/scheduler")
public class SchedulerController {

    @Autowired
    private SchedulerServiceInterface schedulerServiceInterface;
    @GetMapping(path = "/index")
    public ResponseEntity<ResponseObject> list(
            @RequestParam(name = "search", defaultValue = "") String search,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "limit", required = false) Integer limit) {
        Page<Scheduler> list = schedulerServiceInterface.list(
                search, page, limit
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
    public ResponseEntity<ResponseObject> arrangeStudent(@RequestParam(name = "semesterId") Integer semesterId) {

            schedulerServiceInterface.arrangeStudent(semesterId);
            return ResponseEntity.status(HttpStatus.OK).body(
                    new ResponseObject("OK", "Arrange Success", null));

//        catch (Exception e){
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
//                    new ResponseObject("Fail", e.getMessage(), null));
//        }

    }
}
