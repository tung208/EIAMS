package EIAMS.services;

import EIAMS.repositories.RoomRepository;
import EIAMS.repositories.StudentSubjectRepository;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SchedulerService implements SchedulerServiceInterface {
    private final StudentSubjectRepository studentSubjectRepository;
    private final RoomRepository roomRepository;

    @Override
    public long countStudentBySubject(int semesterId, String subjectCode) {
        return studentSubjectRepository.countAllBySemesterIdAndSubjectCode(semesterId, subjectCode);
    }

    @Override
    public long countSlotBySubject(int semesterId, String subjectCode) {
        long numberOfStudents;
        if (subjectCode.isEmpty() || subjectCode.isBlank()) {
            numberOfStudents = studentSubjectRepository.countAllBySemesterId(semesterId);
        } else {
            numberOfStudents = studentSubjectRepository.countAllBySemesterIdAndSubjectCode(semesterId, subjectCode);
        }
        long numberOfRooms = roomRepository.count();
        long numberOfSlots = numberOfStudents / numberOfRooms;
        if (numberOfStudents % numberOfRooms != 0) {
            numberOfSlots++;
        }
        return numberOfSlots;
    }
}
