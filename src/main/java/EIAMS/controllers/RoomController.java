package EIAMS.controllers;


import EIAMS.dtos.RoomDto;
import EIAMS.entities.Room;
import EIAMS.entities.Semester;
import EIAMS.entities.Status;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.exception.EntityNotFoundException;
import EIAMS.services.RoomService;
import EIAMS.services.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/room")
public class RoomController {
    @Autowired
    RoomService roomService;

    @Autowired
    StatusService statusService;

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importSubject(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException, EntityNotFoundException {
        roomService.uploadRoom(file,semesterId);
        statusService.update(semesterId, 3, 1);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Import Success", "Import Success"));
    }

    @GetMapping()
    public PageResponse<Room> getRooms(@RequestParam(defaultValue = "1") Integer pageNo,
                                          @RequestParam(defaultValue = "2") Integer pageSize,
                                          @RequestParam(defaultValue = "id") String sortBy,
                                          @RequestParam(defaultValue = "") Integer semesterId,
                                          @RequestParam(defaultValue = "") String name
    ){
        Page<Room> page =  roomService.search(pageNo, pageSize, semesterId, name);
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getTotalElements(),page.getContent());
    }

    @PostMapping()
    public ResponseEntity<ResponseObject> create(@RequestBody RoomDto roomDto) throws EntityNotFoundException {
        Room room = roomService.create(roomDto);
//        statusService.update(roomDto.getSemesterId(), 3, 1);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("OK", "Create Success", room));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> update(@PathVariable int id, @RequestBody RoomDto roomDto) throws EntityNotFoundException {
        roomService.update(id, roomDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update Success", ""));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable int id) throws EntityNotFoundException {
        roomService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delete Success", ""));
    }
}
