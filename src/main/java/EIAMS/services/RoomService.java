package EIAMS.services;

import EIAMS.dtos.RoomDto;
import EIAMS.entities.Room;
import EIAMS.entities.csvRepresentation.RoomRepresentation;
import EIAMS.repositories.RoomRepository;
import EIAMS.services.excel.ExcelRoom;
import EIAMS.services.interfaces.RoomServiceInterface;
import EIAMS.services.thread.SaveRoom;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RoomService implements RoomServiceInterface {
    private final RoomRepository roomRepository;
    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);


    @Override
    @Transactional
    public Integer uploadRoom(MultipartFile file, int semester_id) throws IOException {
        List<RoomRepresentation> roomRepresentations = new ExcelRoom().getDataFromExcel(file.getInputStream());
        List<Room> subjectList = new ArrayList<>();
        int corePoolSize = 3;
        int maximumPoolSize = 5;
        long keepAliveTime = 60L;
        int queueCapacity = 100;

        // Tạo một ThreadPoolExecutor với các tham số đã cho
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(queueCapacity));

        // Kích thước của danh sách con
        int sublistSize = 100;
        for (RoomRepresentation element: roomRepresentations) {
            Room room = Room.builder()
                    .semesterId(semester_id)
                    .name(element.getRoomNo())
                    .quantityStudent(element.getRoomQuantity())
                    .build();
            subjectList.add(room);
        }

        roomRepository.deleteBySemesterId(semester_id);
        for (int i = 0; i < subjectList.size(); i+=sublistSize){
            int endIndex = Math.min(i + sublistSize, subjectList.size());
            List<Room> sublist = subjectList.subList(i, endIndex);
            executor.execute(new SaveRoom(sublist,roomRepository));
        }
        return null;
    }

    public static String safeTrim(String str,int mode) {
        if (mode == 1){
            return str == null ? null : str.toUpperCase().trim();
        } else {
            return str == null ? null : str.trim();
        }
    }

    @Override
    public Page<Room> search(Integer page, Integer limit, Integer semesterId , String name) {
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return roomRepository.findByDynamic(semesterId, name, pageable);
    }

    @Override
    public Room create(RoomDto roomDto) {
        Room room = Room.builder()
                .name(roomDto.getName())
                .type(roomDto.getType())
                .quantityStudent(roomDto.getQuantityStudent())
                .semesterId(roomDto.getSemesterId())
                .build();
        roomRepository.save(room);
        return room;
    }

    @Override
    public void update(int id, RoomDto roomDto) {
        Optional<Room> room = roomRepository.findById(id);
        if(room.isPresent()){
            Room r = Room.builder()
                    .id(id)
                    .name(roomDto.getName())
                    .semesterId(roomDto.getSemesterId())
                    .type(roomDto.getType())
                    .quantityStudent(roomDto.getQuantityStudent())
                    .build();
            roomRepository.save(r);
        }
    }

    @Override
    public void delete(int id) {
        roomRepository.deleteById(id);
    }
}
