package EIAMS.controllers;

import EIAMS.dtos.PlanExamDto;
import EIAMS.dtos.SchedulerSlotDto;
import EIAMS.entities.PlanExam;
import EIAMS.entities.Room;
import EIAMS.entities.Scheduler;
import EIAMS.entities.Semester;
import EIAMS.entities.responeObject.DashboardResponse;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.repositories.RoomRepository;
import EIAMS.services.PlanExamService;
import EIAMS.services.SchedulerService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/plan-exam")
public class PlanExamController {
    @Autowired
    PlanExamService planExamService;

    @Autowired
    StatusService statusService;

    @Autowired
    SchedulerService schedulerService;

    @Autowired
    RoomRepository roomRepository;

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
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getTotalElements(),page.getContent());
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
    public ResponseEntity<ResponseObject> deletePlanExam(@PathVariable int id) throws EntityNotFoundException {
        planExamService.delete((id));
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK",
                        "Delete Success",
                        ""));
    }

    @GetMapping("/get-list-date")
    public PageResponse<String> getListDate(@RequestParam(defaultValue = "1") Integer pageNo,
                                              @RequestParam(defaultValue = "2") Integer pageSize,
                                              @RequestParam(defaultValue = "id") String sortBy,
                                              @RequestParam(defaultValue = "") Integer semesterId
    ){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<LocalDate> dates = new ArrayList<>();
        Page<String> page =  planExamService.getListDate(pageNo, pageSize, semesterId);
        System.out.println(page.getContent());
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getTotalElements(),page.getContent());
    }

    @GetMapping("/get-list-time")
    public PageResponse<PlanExam> getListTime(@RequestParam(defaultValue = "1") Integer pageNo,
                                            @RequestParam(defaultValue = "2") Integer pageSize,
                                            @RequestParam(defaultValue = "id") String sortBy,
                                            @RequestParam(defaultValue = "") Integer semesterId,
                                            @RequestParam(defaultValue = "") String expectedDate
    ) throws ParseException {
        Page<PlanExam> page =  planExamService.getListTime(pageNo, pageSize, semesterId, expectedDate);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getTotalElements(),page.getContent());
    }

    @GetMapping("/get-list-slot")
    public ResponseEntity<DashboardResponse> getListSlot(@RequestParam(defaultValue = "1") Integer pageNo,
                                         @RequestParam(defaultValue = "2") Integer pageSize,
                                         @RequestParam(defaultValue = "id") String sortBy,
                                         @RequestParam(defaultValue = "") Integer semesterId,
                                         @RequestParam(defaultValue = "") String expectedDate,
                                         @RequestParam(defaultValue = "") String expectedTime
    ) throws ParseException {
        Page<Scheduler> page =  schedulerService.getListSlot(pageNo, pageSize, semesterId, expectedDate, expectedTime);

//        prepare data
        List<Scheduler> schedulers = page.getContent();
        List<Integer> roomId = schedulers.stream()
                .map(obj -> ((Scheduler) obj).getRoomId())
                .collect(Collectors.toList());
        List<Room> rooms = roomRepository.findAllById(roomId);
        Hashtable<Integer,String> roomHashtable = new Hashtable<>();
        rooms.stream().forEach(element -> {
            roomHashtable.put(element.getId(), element.getName());
        });
        List<SchedulerSlotDto> schedulerSlotDtos = new ArrayList<>();

        final int[] totalStudent = {0};
        schedulers.stream().forEach(element -> {
            SchedulerSlotDto schedulerSlotDto = SchedulerSlotDto.builder()
                    .id(element.getId())
                    .semesterId(element.getSemesterId())
                    .roomId(element.getRoomId())
                    .roomName(roomHashtable.get(element.getRoomId()))
                    .startDate(element.getStartDate())
                    .endDate(element.getEndDate())
                    .type(element.getType())
                    .build();
            String[] studentIds = element.getStudentId().split(",");
            totalStudent[0] += studentIds.length;
            schedulerSlotDtos.add(schedulerSlotDto);
        });
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new DashboardResponse(page.getNumber() + 1, page.getTotalPages(),
                        page.getSize(), page.getTotalElements(), totalStudent[0], schedulerSlotDtos));
    }
}
