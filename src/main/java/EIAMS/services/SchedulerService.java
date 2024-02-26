package EIAMS.services;

import EIAMS.entities.*;
import EIAMS.repositories.*;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    private final ExamCodeRepository examCodeRepository;

    @Override
    public long countStudentBySubject(int semesterId, String subjectCode) {
        return studentSubjectRepository.countAllBySemesterIdAndSubjectCode(semesterId, subjectCode);
    }

    public List<StudentSubject> shuffleStudents(List<StudentSubject> students) {
        // Shuffle the list
        Collections.shuffle(students);
        return students;
    }

    @Override
    public void arrangeStudent(int semesterId) {

        long numberOfRooms = roomRepository.count();
        long numberOfSlots = 0;

        List<Slot> slots = slotRepository.findAll();

        List<PlanExam> planExamList = planExamRepository.findAll();
        for (PlanExam planExam : planExamList) {
            String subjectCodes = planExam.getSubjectCode();
            Collection<String> subjectCodesNoLabAndMix = new ArrayList<>();
            Collection<String> subjectCodesLabAndMix = new ArrayList<>();
//            Collection<String> subjectCodesCollection = new HashSet<>(Arrays.asList(subjectCodes.split(",")));
//            List<StudentSubject> allStudentInListSubjectCodes = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesCollection);
            Slot slot = slotRepository.findSlotByTimeRange(planExam.getStartTime(), planExam.getEndTime());
            for (String code : subjectCodes.split(",")) {
                Subject subject = subjectRepository.findBySemesterIdAndSubjectCode(semesterId, code);
                List<StudentSubject> listBlackList = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, 1);
                List<StudentSubject> listLegit = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, 0);
                List<StudentSubject> allStudentBySubjectCode = studentSubjectRepository.findAllBySemesterIdAndSubjectCode(semesterId, code);
                List<Room> labs = roomRepository.findAllByType("lab");
                List<Room> roomCommon = roomRepository.findAllByType("common");
                ExamCode examCode = examCodeRepository.findBySemesterIdAndSlotIdAndSubjectId(semesterId, slot.getId(), String.valueOf(subject.getId()));
                // Fill student
                if (subject.getNoLab() == 1 && subject.getDontMix() == 1) {
                    allStudentBySubjectCode = shuffleStudents(allStudentBySubjectCode);
                    int numberOfStudent = allStudentBySubjectCode.size();
                    int numberOfRoomNeed = numberOfStudent / roomCommon.get(0).getQuantityStudent();
                    if (numberOfStudent % roomCommon.get(0).getQuantityStudent() != 0) {
                        numberOfRoomNeed++;
                    }
                    // TODO : if a subject need more than 1 slot
                    int numberOfSlotNeed = numberOfRoomNeed / roomCommon.size();
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
                                StudentSubject student = allStudentBySubjectCode.get(studentIndex);
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
                        scheduler.setSlotId(slot.getId());
                        scheduler.setRoomId(room.getId());
                        scheduler.setSubjectCode(sCodes);
                        scheduler.setStudentCodes(studentCodes);
                        scheduler.setExamCodeId(String.valueOf(examCode.getId()));
                        scheduler.setStartDate(planExam.getStartTime());
                        scheduler.setEndDate(planExam.getEndTime());
                        schedulerRepository.save(scheduler);
                    }
                }
                if (subject.getNoLab() == 1 && subject.getDontMix() == 0) {
                    //TODO: Fill all student no lab and mix to room ===> after loop
                    subjectCodesNoLabAndMix.add(code);
                }
                if (subject.getNoLab() == 0 && subject.getDontMix() == 1) {
                    int numberOfStudentBlackList = listBlackList.size();
                    int numberOfStudentLegit = listLegit.size();
                    int numberOfRoomCommonNeed = numberOfStudentLegit / roomCommon.get(0).getQuantityStudent();
                    if (numberOfStudentLegit % roomCommon.get(0).getQuantityStudent() != 0) {
                        numberOfRoomCommonNeed++;
                    }
                    int numberOfLabRoomNeed = numberOfStudentBlackList / labs.get(0).getQuantityStudent();
                    // Gia su lab co 25 ng. chap nhan 20 ng thi xep 1 phong. Neu ko thi xep phong thuong
                    if ((numberOfStudentBlackList / labs.get(0).getQuantityStudent() >= 1) &&
                            (numberOfStudentBlackList % labs.get(0).getQuantityStudent()) > (labs.get(0).getQuantityStudent() - 5)) {
                        numberOfLabRoomNeed++;
                    }
                    if ((numberOfStudentBlackList / labs.get(0).getQuantityStudent() < 1) &&
                            (numberOfStudentBlackList % labs.get(0).getQuantityStudent()) > (labs.get(0).getQuantityStudent() - 5)) {
                        numberOfRoomCommonNeed++;
                    }
                    //TODO: Fill all student to lab and room common
                }
                if (subject.getNoLab() == 0 && subject.getDontMix() == 0) {
                    subjectCodesLabAndMix.add(code);
                    //TODO: Fill all student blacklist to lab and normal student to room common
                }
            }
            //TODO: Fill all student no lab and mix to room
            List<StudentSubject> listStudentWithSubjectNoLabAndMix = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesNoLabAndMix);
            //TODO: Fill student to all room (lab and mix)
            List<StudentSubject> ListBlackListWithSubjectLabAndMix
                    = studentSubjectRepository.findAllBySemesterIdAndBlackListAndSubjectCodeIn(semesterId, 1, subjectCodesLabAndMix);
            List<StudentSubject> ListLegitWithSubjectLabAndMix
                    = studentSubjectRepository.findAllBySemesterIdAndBlackListAndSubjectCodeIn(semesterId, 0, subjectCodesLabAndMix);
        }

    }
}
