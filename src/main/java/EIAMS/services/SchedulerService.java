package EIAMS.services;

import EIAMS.entities.*;
import EIAMS.repositories.*;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SchedulerService implements SchedulerServiceInterface {
    private final StudentSubjectRepository studentSubjectRepository;
    private final RoomRepository roomRepository;
    private final SubjectRepository subjectRepository;
    private final SlotRepository slotRepository;
    private final SchedulerRepository schedulerRepository;
    private final PlanExamRepository planExamRepository;

    @Override
    public long countStudentBySubject(int semesterId, String subjectCode) {
        return studentSubjectRepository.countAllBySemesterIdAndSubjectCode(semesterId, subjectCode);
    }

    @Override
    public long countSlotBySubject(int semesterId, String subjectCode) {
        long numberOfStudents = studentSubjectRepository.countAllBySemesterIdAndSubjectCode(semesterId, subjectCode);
        long numberOfBlackList = studentSubjectRepository.countAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, subjectCode, 1);
        long numberOfRooms = roomRepository.count();
        long numberOfSlots = 0;
        List<Subject> subjectDontMixAndNoLab = subjectRepository.findAllByDontMixAndNoLab(1, 1);
        List<Subject> subjectsDontMixAndNotNoLab = subjectRepository.findAllByDontMixAndNoLab(1, 0);
        List<Subject> subjectsMixAndNoLab = subjectRepository.findAllByDontMixAndNoLab(0, 1);
        List<Subject> subjectsMixAndNotNoLab = subjectRepository.findAllByDontMixAndNoLab(0, 0);

        List<Slot> slots = slotRepository.findAll();

        List<PlanExam> planExamList = planExamRepository.findAll();
        for (PlanExam planExam : planExamList) {
            String subjectCodes = planExam.getSubjectCode();
            List<Slot> slotList = slotRepository.findSlotsByTimeRange(planExam.getStartTime(), planExam.getEndTime());
            for (String code : subjectCodes.split(",")){
                Subject subject = subjectRepository.findBySemesterIdAndSubjectCode(semesterId, code);
                List<StudentSubject> listBlackList = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, 1);
                List<StudentSubject> listLegit = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, 0);
                List<StudentSubject> allStudent = studentSubjectRepository.findAllBySemesterIdAndSubjectCode(semesterId, code);
                List<Room> labs = roomRepository.findAllByType("lab");
                List<Room> roomCommon = roomRepository.findAllByType("common");

                if(subject.getNoLab() == 1 && subject.getDontMix() == 1) {
                    int numberOfStudent = allStudent.size();
                    int numberOfRoomNeed = numberOfStudent/roomCommon.get(0).getQuantityStudent();
                    if (numberOfStudent % roomCommon.get(0).getQuantityStudent() != 0) {
                        numberOfRoomNeed++;
                    }
                    // Calculate slot need
                    int numberOfSlotNeed = numberOfRoomNeed/roomCommon.size();
                    if (numberOfRoomNeed % roomCommon.size() != 0) {
                        numberOfSlotNeed++;
                    }

                    // Calculate the base number of students per room (floor division)
                    int baseStudentsPerRoom = numberOfStudent / numberOfRoomNeed;

                    // Calculate the number of rooms that will get the base number of students
                    int roomsWithBaseStudents = numberOfStudent % numberOfRoomNeed;

                    // Initialize an array to hold the number of students in each room
                    int[] studentsInRooms = new int[numberOfRoomNeed];

                    // Distribute the base number of students to rooms
                    for (int i = 0; i < numberOfRoomNeed; i++) {
                        studentsInRooms[i] = baseStudentsPerRoom;
                    }

                    // Distribute the remaining students among the rooms with the fewest students
                    for (int i = 0; i < roomsWithBaseStudents; i++) {
                        studentsInRooms[i]++;
                    }

                    // Assign students to rooms

                    for (int i = 0; i < numberOfRoomNeed; i++) {
                        int studentIndex = 0;
                        String sCodes = "";
                        String studentCodes = "";
                        Room room = roomCommon.get(i);
                        // Assign students to the current room
                        for (int j = 0; j < studentsInRooms[i]; j++) {
                            if (studentIndex < numberOfStudent) {
                                StudentSubject student = allStudent.get(studentIndex);
                                studentCodes += (student.getRollNumber() + ",");
                                sCodes += (student.getSubjectCode() + ",");
                                studentIndex++;
                            } else {
                                break; // No more students to assign
                            }
                        }
                        // Assign the student to the room
                        Scheduler scheduler = new Scheduler();
                        scheduler.setSemesterId(semesterId);
                        scheduler.setSlotId(slots.get(0).getId());
                        scheduler.setRoomId(room.getId());
                        scheduler.setSubjectCode(sCodes);
                        scheduler.setStudentCodes(studentCodes);
                        //TODO: set slot Id
                        schedulerRepository.save(scheduler);
                    }
                }
            }
        }

        return numberOfSlots;
    }
}
