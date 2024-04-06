package EIAMS.controllers;


import EIAMS.dtos.RoomDto;
import EIAMS.entities.Room;
import EIAMS.entities.Semester;
import EIAMS.entities.responeObject.PageResponse;
import EIAMS.entities.responeObject.ResponseObject;
import EIAMS.services.RoomService;
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

    @PostMapping("/import")
    public ResponseEntity<ResponseObject> importSubject(@RequestParam("file") MultipartFile file, @RequestParam("semester_id") int semesterId) throws IOException {
        roomService.uploadRoom(file,semesterId);
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
        return new PageResponse<>(page.getNumber() + 1, page.getTotalPages(), page.getSize(), page.getContent());
    }

    @PostMapping
    public ResponseEntity<ResponseObject> create(@RequestBody RoomDto roomDto){
        Room room = roomService.create(roomDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                new ResponseObject("OK", "Create Success", room));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> update(@PathVariable int id, @RequestBody RoomDto roomDto){
        roomService.update(id, roomDto);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Update Success", ""));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> delete(@PathVariable int id){
        roomService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(
                new ResponseObject("OK", "Delete Success", ""));
    }
}
