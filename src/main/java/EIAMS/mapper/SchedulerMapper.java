package EIAMS.mapper;

import EIAMS.dtos.SchedulerDto;
import EIAMS.entities.Scheduler;

public class SchedulerMapper {
    public static Scheduler toEntity(SchedulerDto schedulerDto){
        return Scheduler
                .builder()
                .id(schedulerDto.getId())
                .quantitySupervisor(schedulerDto.getQuantitySupervisor())
                .examDate(schedulerDto.getExamDate())
                .build();
    }

    public static SchedulerDto toDto(Scheduler scheduler){
        return SchedulerDto
                .builder()
                .id(scheduler.getId())
                .roomId(scheduler.getRoom().getId())
                .slotId(scheduler.getSlot().getId())
                .semesterId(scheduler.getSemester().getId())
                .quantitySupervisor(scheduler.getQuantitySupervisor())
                .examDate(scheduler.getExamDate())
                .build();
    }
}
