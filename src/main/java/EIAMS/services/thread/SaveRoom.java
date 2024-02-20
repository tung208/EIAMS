package EIAMS.services.thread;

import EIAMS.entities.Room;
import EIAMS.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SaveRoom implements Runnable{
    private final List<Room> roomList;
    private final RoomRepository roomRepository;

    @Override
    public void run() {
        try{
            roomRepository.saveAll(roomList);
        } catch (Exception e){
//            System.out.println(e);
        }
        System.out.println("Task room executed by thread: " + Thread.currentThread().getName());
    }
}
