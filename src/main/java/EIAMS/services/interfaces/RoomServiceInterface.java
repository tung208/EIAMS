package EIAMS.services.interfaces;

import EIAMS.dtos.RoomDto;
import EIAMS.entities.Room;
import EIAMS.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface RoomServiceInterface {
    Integer uploadRoom(MultipartFile file, int semester_id) throws IOException;
    Page<Room> search(Integer page, Integer limit, Integer semesterId , String subjectCode);

    Room create(RoomDto roomDto);

    void update(int id,RoomDto roomDto) throws EntityNotFoundException;
    void delete(int id);
}
