package EIAMS.services;

import EIAMS.entities.*;
import EIAMS.helper.Pagination;
import EIAMS.repositories.*;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SchedulerService implements SchedulerServiceInterface {
    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentRepository studentRepository;
    private final RoomRepository roomRepository;
    private final SubjectRepository subjectRepository;
    private final SlotRepository slotRepository;
    private final SchedulerRepository schedulerRepository;
    private final PlanExamRepository planExamRepository;
    private final ExamCodeRepository examCodeRepository;
    private final Pagination pagination;

    @Override
    public Page<Scheduler> list(Integer semesterId, String search, Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        return schedulerRepository.findAllBySemesterIdAndSubjectCodeIsContainingIgnoreCase(semesterId, search, pageable);
    }

    @Override
    public Page<Student> getListStudentInARoom(Integer schedulerId,String search, Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        Scheduler scheduler = schedulerRepository.findById(schedulerId).get();
        String[] assignedStudents = scheduler.getStudentId().split(",");
        Collection<Integer> studentIds = new ArrayList<>();
        for(String sId : assignedStudents) {
            studentIds.add(Integer.parseInt(sId));
        }
        List<StudentSubject> studentSubjects = studentSubjectRepository.findAllBySemesterIdAndIdIn(scheduler.getSemesterId(), studentIds);
        Collection<String> rollNumbers = new ArrayList<>();
        for(StudentSubject s : studentSubjects) {
            rollNumbers.add(s.getRollNumber());
        }
        return studentRepository.findAllByRollNumberInAndFullNameContainingIgnoreCaseOrCmtndContainingIgnoreCaseAndMemberCodeContainingIgnoreCase(
                rollNumbers, search, search, search, pageable);
    }

    public List<StudentSubject> shuffleStudents(List<StudentSubject> students) {
        // Shuffle the list
        Collections.shuffle(students);
        return students;
    }

    @Override
    public void arrangeStudent(int semesterId) {
        List<PlanExam> planExamList = planExamRepository.findAllBySemesterId(semesterId);
        List<Room> labs = roomRepository.findAllByQuantityStudentGreaterThanAndNameContainingIgnoreCase(1, "Lab");
        List<Room> roomCommon = roomRepository.findAllByQuantityStudentGreaterThanAndNameNotContainingIgnoreCase(1, "Lab");
        Collection<String> subjectCodesNoLabAndMix = new ArrayList<>();
        Collection<String> subjectCodesLabAndMix = new ArrayList<>();
        for (PlanExam planExam : planExamList) {
            List<Room> availableLabRooms = new ArrayList<>(labs);
            List<Room> availableCommonRooms = new ArrayList<>(roomCommon);

            String code = planExam.getSubjectCode();
            Subject subject = subjectRepository.findBySemesterIdAndSubjectCode(semesterId, code);
            List<StudentSubject> listBlackList = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, 1);
            List<StudentSubject> listLegit = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, 0);
            List<StudentSubject> allStudentBySubjectCode = studentSubjectRepository.findAllBySemesterIdAndSubjectCode(semesterId, code);
            // Fill student
            if (subject.getNoLab() != null && subject.getNoLab() == 1 && subject.getDontMix() != null && subject.getDontMix() == 1) {
                //arrange student don't mix room and only room common
                if (!allStudentBySubjectCode.isEmpty()) {
                    Map<Integer, Integer> studentsInRooms = calculateRoomAllocation(allStudentBySubjectCode, availableCommonRooms);
                    // Assign students to rooms
                    fillStudentToRoom(studentsInRooms, allStudentBySubjectCode, semesterId, planExam);
                }
            }
            if (subject.getNoLab() != null && subject.getNoLab() == 1 && (subject.getDontMix() == null || subject.getDontMix() == 0)) {
                subjectCodesNoLabAndMix.add(code);
            }
            if ((subject.getNoLab() == null || subject.getNoLab() == 0) && subject.getDontMix() != null && subject.getDontMix() == 1) {
                int numberOfStudentBlackList = listBlackList.size();
                int numberOfStudentLegit = listLegit.size();

                int numberOfLabRoomNeed = numberOfStudentBlackList / labs.get(0).getQuantityStudent();
                // Gia su lab co 25 ng. chap nhan 20 ng thi xep 1 phong. Neu ko thi xep phong thuong
                if ((numberOfStudentBlackList % labs.get(0).getQuantityStudent()) > (labs.get(0).getQuantityStudent() - 5)) {
                    numberOfLabRoomNeed++;
                } else {
                    numberOfStudentLegit += numberOfStudentBlackList % labs.get(0).getQuantityStudent();
                }
                int numberOfRoomCommonNeed = numberOfStudentLegit / roomCommon.get(0).getQuantityStudent();
                if (numberOfStudentLegit % roomCommon.get(0).getQuantityStudent() != 0) {
                    numberOfRoomCommonNeed++;
                }
                // Initialize a map to hold the number of students in each room
                if (numberOfLabRoomNeed > 0) {
                    Map<Integer, Integer> studentsInLab = new HashMap<>();
                    // Calculate the base number of students per room (floor division)
                    int baseStudentsPerLab = numberOfStudentBlackList / numberOfLabRoomNeed;
                    if (numberOfStudentBlackList < labs.get(0).getQuantityStudent()) {
                        baseStudentsPerLab = numberOfStudentBlackList;
                    }

                    // Calculate the number of rooms that will get the base number of students
                    int labsWithBaseStudents = numberOfStudentBlackList % numberOfLabRoomNeed;
                    // Distribute the base number of students to rooms
                    for (int i = 0; i < numberOfLabRoomNeed; i++) {
                        Room room = availableLabRooms.get(i);
                        studentsInLab.put(room.getId(), baseStudentsPerLab);
                    }
                    if (labsWithBaseStudents != 0) {
                        // Distribute the remaining students among the rooms with the fewest students
                        for (int i = 0; i < labsWithBaseStudents; i++) {
                            int updatedStudentCount = studentsInLab.get(availableLabRooms.get(i).getId()) + 1;
                            studentsInLab.put(availableLabRooms.get(i).getId(), updatedStudentCount);
                        }
                    }
                    fillStudentToRoom(studentsInLab, listBlackList, semesterId, planExam);
                }
                if (numberOfRoomCommonNeed > 0) {
                    Map<Integer, Integer> studentsInRoomCommon = new HashMap<>();
                    // Calculate the base number of students per room (floor division)
                    int baseStudentsPerRoom = numberOfStudentLegit / numberOfRoomCommonNeed;
                    if (numberOfStudentLegit < roomCommon.get(0).getQuantityStudent()) {
                        baseStudentsPerRoom = numberOfStudentLegit;
                    }

                    // Calculate the number of rooms that will get the base number of students
                    int roomsWithBaseStudents = numberOfStudentLegit % numberOfRoomCommonNeed;
                    // Distribute the base number of students to rooms
                    for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                        Room room = availableCommonRooms.get(i);
                        studentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
                    }

                    if (roomsWithBaseStudents != 0) {
                        // Distribute the remaining students among the rooms with the fewest students
                        for (int i = 0; i < roomsWithBaseStudents; i++) {
                            int updatedStudentCount = studentsInRoomCommon.get(availableCommonRooms.get(i).getId()) + 1;
                            studentsInRoomCommon.put(availableCommonRooms.get(i).getId(), updatedStudentCount);
                        }
                    }
                    fillStudentToRoom(studentsInRoomCommon, listLegit, semesterId, planExam);
                }
            }
            if ((subject.getNoLab() == null || subject.getNoLab() == 0) && (subject.getDontMix() == null || subject.getDontMix() == 0)) {
                subjectCodesLabAndMix.add(code);
            }
        }
        List<StudentSubject> listStudentWithSubjectNoLabAndMix = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesNoLabAndMix);
        for (PlanExam planExam : planExamRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesNoLabAndMix)) {
            List<Room> availableCommonRooms = new ArrayList<>(roomCommon);
            if (!listStudentWithSubjectNoLabAndMix.isEmpty()) {
                Map<Integer, Integer> studentsInRoomCommon = calculateRoomAllocation(listStudentWithSubjectNoLabAndMix, availableCommonRooms);
                fillStudentToRoom(studentsInRoomCommon, listStudentWithSubjectNoLabAndMix, semesterId, planExam);
            }
        }

        List<StudentSubject> ListBlackListWithSubjectLabAndMix
                = studentSubjectRepository.findAllBySemesterIdAndBlackListAndSubjectCodeIn(semesterId, 1, subjectCodesLabAndMix);
        List<StudentSubject> ListLegitWithSubjectLabAndMix
                = studentSubjectRepository.findAllBySemesterIdAndBlackListAndSubjectCodeIn(semesterId, 0, subjectCodesLabAndMix);
        for (PlanExam planExam : planExamRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesLabAndMix)) {
            List<Room> availableLabRooms = new ArrayList<>(labs);
            List<Room> availableCommonRooms = new ArrayList<>(roomCommon);
            int numberOfStudentBlackList = ListBlackListWithSubjectLabAndMix.size();
            int numberOfStudentLegit = ListLegitWithSubjectLabAndMix.size();
            int numberOfLabRoomNeed = numberOfStudentBlackList / labs.get(0).getQuantityStudent();
            // Gia su lab co 25 ng. chap nhan 20 ng thi xep 1 phong. Neu ko thi xep phong thuong
            if ((numberOfStudentBlackList % labs.get(0).getQuantityStudent()) > (labs.get(0).getQuantityStudent() - 5)) {
                numberOfLabRoomNeed++;
            } else {
                numberOfStudentLegit += numberOfStudentBlackList % labs.get(0).getQuantityStudent();
            }
            int numberOfRoomCommonNeed = numberOfStudentLegit / roomCommon.get(0).getQuantityStudent();
            if (numberOfStudentLegit % roomCommon.get(0).getQuantityStudent() != 0) {
                numberOfRoomCommonNeed++;
            }
            // Initialize a map to hold the number of students in each room
            if (numberOfLabRoomNeed > 0) {
                Map<Integer, Integer> studentsInLab = new HashMap<>();
                // Calculate the base number of students per room (floor division)
                int baseStudentsPerLab = numberOfStudentBlackList / numberOfLabRoomNeed;
                if (numberOfStudentBlackList <= labs.get(0).getQuantityStudent()) {
                    baseStudentsPerLab = numberOfStudentBlackList;
                }
                // Calculate the number of rooms that will get the base number of students
                int labsWithBaseStudents = numberOfStudentBlackList % numberOfLabRoomNeed;
                // Distribute the base number of students to rooms
                for (int i = 0; i < numberOfLabRoomNeed; i++) {
                    Room room = availableLabRooms.get(i);
                    studentsInLab.put(room.getId(), baseStudentsPerLab);
                }
                if (labsWithBaseStudents != 0) {
                    // Distribute the remaining students among the rooms with the fewest students
                    for (int i = 0; i < labsWithBaseStudents; i++) {
                        int updatedStudentCount = studentsInLab.get(availableLabRooms.get(i).getId()) + 1;
                        studentsInLab.put(availableLabRooms.get(i).getId(), updatedStudentCount);
                    }
                }
                fillStudentToRoom(studentsInLab, ListBlackListWithSubjectLabAndMix, semesterId, planExam);
            }
            if (numberOfRoomCommonNeed > 0) {
                Map<Integer, Integer> mixStudentsInRoomCommon = new HashMap<>();
                // Calculate the base number of students per room (floor division)
                int baseStudentsPerRoom = numberOfStudentLegit / numberOfRoomCommonNeed;
                if (numberOfStudentLegit < roomCommon.get(0).getQuantityStudent()) {
                    baseStudentsPerRoom = numberOfStudentLegit;
                }
                // Calculate the number of rooms that will get the base number of students
                int roomsWithBaseStudents = numberOfStudentLegit % numberOfRoomCommonNeed;
                // Distribute the base number of students to rooms
                for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                    Room room = availableCommonRooms.get(i);
                    mixStudentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
                }
                if (roomsWithBaseStudents != 0) {
                    // Distribute the remaining students among the rooms with the fewest students
                    for (int i = 0; i < roomsWithBaseStudents; i++) {
                        int updatedStudentCount = mixStudentsInRoomCommon.get(availableCommonRooms.get(i).getId()) + 1;
                        mixStudentsInRoomCommon.put(availableCommonRooms.get(i).getId(), updatedStudentCount);
                    }
                }
                fillStudentToRoom(mixStudentsInRoomCommon, ListLegitWithSubjectLabAndMix, semesterId, planExam);
            }
        }
    }

    public List<Room> getAvailableRoom(PlanExam planExam, List<Scheduler> schedulers, List<Room> allRooms) {
        for (Scheduler scheduler : schedulers) {
            // Check if the start time of the PlanExam falls within the time range of any existing Scheduler
            if (scheduler.getStartDate().isEqual(getStartDateFromPlanExam(planExam))
                    && scheduler.getEndDate().isEqual(getEndDateFromPlanExam(planExam))) {
                // If yes, the room is not available, so continue to the next scheduler
                allRooms.remove(roomRepository.findById(scheduler.getRoomId()).get());
            }
        }

        return allRooms;
    }

    public Map<Integer, Integer> calculateRoomAllocation(List<StudentSubject> allStudentBySubjectCode, List<Room> rooms) {
        int numberOfStudent = allStudentBySubjectCode.size();
        int numberOfRoomNeed = numberOfStudent / rooms.get(0).getQuantityStudent();
        if (numberOfStudent < rooms.get(0).getQuantityStudent() || numberOfStudent % rooms.get(0).getQuantityStudent() != 0) {
            numberOfRoomNeed++;
        }
        Map<Integer, Integer> studentsInRooms = new HashMap<>();
        if (numberOfRoomNeed > 0) {
            // Calculate the base number of students per room (floor division)
            int baseStudentsPerRoom;
            if (numberOfStudent < rooms.get(0).getQuantityStudent()) {
                baseStudentsPerRoom = numberOfStudent;
            } else {
                baseStudentsPerRoom = numberOfStudent / numberOfRoomNeed;
            }

            // Distribute the base number of students to rooms
            for (int i = 0; i < numberOfRoomNeed; i++) {
                if (i < rooms.size()) {
                    Room room = rooms.get(i); // Assuming roomCommon is a cyclic list of rooms
                    studentsInRooms.put(room.getId(), baseStudentsPerRoom);
                }
            }
            // Calculate the number of rooms that will get the base number of students
            int roomsWithBaseStudents = numberOfStudent % numberOfRoomNeed;
            if (roomsWithBaseStudents != 0) {
                // Distribute the remaining students among the rooms with the fewest students
                for (int i = 0; i < roomsWithBaseStudents; i++) {
                    if (i < rooms.size()) {
                        int updatedStudentCount = studentsInRooms.get(rooms.get(i).getId()) + 1;
                        studentsInRooms.put(rooms.get(i).getId(), updatedStudentCount);
                    }
                }
            }
        }
        return studentsInRooms;
    }

    public void fillStudentToRoom(Map<Integer, Integer> studentsInRooms,
                                  List<StudentSubject> allStudentBySubjectCode, int semesterId, PlanExam planExam) {
        if (allStudentBySubjectCode.isEmpty() || studentsInRooms.isEmpty()) return;
        allStudentBySubjectCode = shuffleStudents(allStudentBySubjectCode);
        String code = planExam.getSubjectCode();
        List<ExamCode> examCodes = examCodeRepository.findBySemesterIdAndSubjectCode(semesterId, code);
        StringBuilder examIds = new StringBuilder();
        for (ExamCode examCode : examCodes) {
            examIds.append(examCode.getId().toString()).append(",");
        }
        int studentIndex = 0;
        for (Map.Entry<Integer, Integer> entry : studentsInRooms.entrySet()) {
            String studentIds = "";
            Room room = roomRepository.findById(entry.getKey()).get();
            Scheduler scheduler = schedulerRepository.findBySemesterIdAndRoomIdAndStartDateAndEndDate(
                    semesterId, room.getId(), getStartDateFromPlanExam(planExam), getEndDateFromPlanExam(planExam));

            if (scheduler != null && scheduler.getStudentId() != null) {
                String[] assignedStudents = scheduler.getStudentId().split(",");
                int assignedStudentsCount = assignedStudents.length;
                if (assignedStudentsCount == studentsInRooms.get(room.getId())) {
                    continue;
                }
            }
            // Assign students to the current room
            if (studentsInRooms.get(room.getId()) != null) {
                for (int j = 0; j < studentsInRooms.get(room.getId()); j++) {
                    if (studentIndex < allStudentBySubjectCode.size()) {
                        StudentSubject student = allStudentBySubjectCode.get(studentIndex);
                        studentIds += (student.getId() + ",");
                        studentIndex++;
                    } else {
                        break; // No more students to assign
                    }
                }
            }
            if (scheduler == null) {
                scheduler = new Scheduler();
            }
            // Assign the student to the room
            scheduler.setSemesterId(semesterId);
            scheduler.setRoomId(room.getId());
            scheduler.setSubjectCode(planExam.getSubjectCode());
            scheduler.setStudentId(studentIds);
            scheduler.setExamCodeId(examIds.toString());
            scheduler.setStartDate(getStartDateFromPlanExam(planExam));
            scheduler.setEndDate(getEndDateFromPlanExam(planExam));
            schedulerRepository.save(scheduler);

        }
    }

    public LocalDateTime getStartDateFromPlanExam(PlanExam planExam) {
        Date expectedDate = planExam.getExpectedDate();
        String expectedTime = planExam.getExpectedTime();
        // Convert Date to LocalDateTime
        LocalDateTime localDateTime = expectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        // Parse expectedTime into LocalTime
        String[] timeParts = expectedTime.split("-");
        String startTime = timeParts[0];
        String[] startParts = startTime.split("H");
        int startHour = Integer.parseInt(startParts[0]);
        int startMinute = Integer.parseInt(startParts[1]);

        return localDateTime.withHour(startHour).withMinute(startMinute);
    }

    public LocalDateTime getEndDateFromPlanExam(PlanExam planExam) {
        Date expectedDate = planExam.getExpectedDate();
        String expectedTime = planExam.getExpectedTime();
        // Convert Date to LocalDateTime
        LocalDateTime localDateTime = expectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        // Parse expectedTime into LocalTime
        String[] timeParts = expectedTime.split("-");
        String endTime = timeParts[1];
        String[] endParts = endTime.split("H");
        int endHour = Integer.parseInt(endParts[0]);
        int endMinute = Integer.parseInt(endParts[1]);

        return localDateTime.withHour(endHour).withMinute(endMinute);
    }
}