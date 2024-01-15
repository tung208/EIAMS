package EIAMS.mapper;

import EIAMS.dtos.RoomDto;
import EIAMS.entities.Room;

public class RoomMapper {
    public static Room toEntity(RoomDto roomDto){
        return Room
                .builder()
                .id(roomDto.getId())
                .name(roomDto.getName())
                .type(roomDto.getType())
                .build();
    }
}
