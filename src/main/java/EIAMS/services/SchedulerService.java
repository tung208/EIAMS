package EIAMS.services;

import EIAMS.entities.*;
import EIAMS.repositories.*;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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

        List<PlanExam> planExamList = planExamRepository.findAllBySemesterId(String.valueOf(semesterId));
        List<Room> allRooms = roomRepository.findAll();
        List<Room> labs = roomRepository.findAllByType("lab");
        List<Room> roomCommon = roomRepository.findAllByType("common");
        List<Scheduler> schedulers = schedulerRepository.findAllBySemesterId(semesterId);
        for (PlanExam planExam : planExamList) {
            List<Room> availableRooms = getAvaiableRoom(planExam, schedulers, allRooms);
            List<Room> availableLabRooms = getAvaiableRoom(planExam, schedulers, labs);
            List<Room> availableCommonRooms = getAvaiableRoom(planExam, schedulers, roomCommon);

            String subjectCodes = planExam.getSubjectCode();
            Collection<String> subjectCodesNoLabAndMix = new ArrayList<>();
            Collection<String> subjectCodesLabAndMix = new ArrayList<>();
//            Collection<String> subjectCodesCollection = new HashSet<>(Arrays.asList(subjectCodes.split(",")));
//            List<StudentSubject> allStudentInListSubjectCodes = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesCollection);
            Slot slot = slotRepository.findSlotByTimeRange(planExam.getStartTime(), planExam.getEndTime());
            StringBuilder examCodes = new StringBuilder();
            for (String code : subjectCodes.split(",")) {
                Subject subject = subjectRepository.findBySemesterIdAndSubjectCode(semesterId, code);
                List<StudentSubject> listBlackList = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, 1);
                List<StudentSubject> listLegit = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, 0);
                List<StudentSubject> allStudentBySubjectCode = studentSubjectRepository.findAllBySemesterIdAndSubjectCode(semesterId, code);
                ExamCode examCode = examCodeRepository.findBySemesterIdAndSlotIdAndSubjectId(semesterId, slot.getId(), String.valueOf(subject.getId()));
                examCodes.append(examCode.getId().toString()).append(",");
                // Fill student
                if (subject.getNoLab() == 1 && subject.getDontMix() == 1) {
                    //arrange student don't mix room and only room common
                    allStudentBySubjectCode = shuffleStudents(allStudentBySubjectCode);

                    Map<Integer, Integer> studentsInRooms = calculateRoomAllocation(allStudentBySubjectCode, availableCommonRooms);
                    // Assign students to rooms
                    fillStudentToRoom(studentsInRooms, availableCommonRooms, allStudentBySubjectCode, semesterId, planExam, slot, examCodes);
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
                    // Initialize a map to hold the number of students in each room
                    Map<Integer, Integer> studentsInLab = new HashMap<>();
                    // Calculate the base number of students per room (floor division)
                    int baseStudentsPerLab = numberOfStudentBlackList / numberOfLabRoomNeed;

                    // Calculate the number of rooms that will get the base number of students
                    int labsWithBaseStudents = numberOfStudentBlackList % numberOfLabRoomNeed;
                    // Distribute the base number of students to rooms
                    for (int i = 0; i < numberOfLabRoomNeed; i++) {
                        Room room = availableLabRooms.get(i);
                        studentsInLab.put(room.getId(), baseStudentsPerLab);
                    }

                    // Distribute the remaining students among the rooms with the fewest students
                    for (int i = 0; i < labsWithBaseStudents; i++) {
                        int updatedStudentCount = studentsInLab.get(availableLabRooms.get(i).getId()) + 1;
                        studentsInLab.put(availableLabRooms.get(i).getId(), updatedStudentCount);
                    }
                    fillStudentToRoom(studentsInLab, availableLabRooms, listBlackList, semesterId, planExam, slot, examCodes);
                    // Initialize a map to hold the number of students in each room
                    Map<Integer, Integer> studentsInRoomCommon = new HashMap<>();
                    // Calculate the base number of students per room (floor division)
                    int baseStudentsPerRoom = numberOfStudentLegit / numberOfRoomCommonNeed;

                    // Calculate the number of rooms that will get the base number of students
                    int roomsWithBaseStudents = numberOfStudentLegit % numberOfRoomCommonNeed;
                    // Distribute the base number of students to rooms
                    for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                        Room room = availableCommonRooms.get(i);
                        studentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
                    }

                    // Distribute the remaining students among the rooms with the fewest students
                    for (int i = 0; i < roomsWithBaseStudents; i++) {
                        int updatedStudentCount = studentsInRoomCommon.get(availableCommonRooms.get(i).getId()) + 1;
                        studentsInRoomCommon.put(availableCommonRooms.get(i).getId(), updatedStudentCount);
                    }
                    fillStudentToRoom(studentsInRoomCommon, availableCommonRooms, listLegit, semesterId, planExam, slot, examCodes);
                }
                if (subject.getNoLab() == 0 && subject.getDontMix() == 0) {
                    subjectCodesLabAndMix.add(code);
                    //TODO: Fill all student blacklist to lab and normal student to room common
                }
            }
            //TODO: Fill all student no lab and mix to room
            List<StudentSubject> listStudentWithSubjectNoLabAndMix = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesNoLabAndMix);
            Map<Integer, Integer> studentsInRoomCommon = calculateRoomAllocation(listStudentWithSubjectNoLabAndMix, availableCommonRooms);
            fillStudentToRoom(studentsInRoomCommon, availableCommonRooms, shuffleStudents(listStudentWithSubjectNoLabAndMix), semesterId, planExam, slot, examCodes);

            //TODO: Fill student to all room (lab and mix)
            List<StudentSubject> ListBlackListWithSubjectLabAndMix
                    = studentSubjectRepository.findAllBySemesterIdAndBlackListAndSubjectCodeIn(semesterId, 1, subjectCodesLabAndMix);

            List<StudentSubject> ListLegitWithSubjectLabAndMix
                    = studentSubjectRepository.findAllBySemesterIdAndBlackListAndSubjectCodeIn(semesterId, 0, subjectCodesLabAndMix);
            int numberOfStudentBlackList = ListBlackListWithSubjectLabAndMix.size();
            int numberOfStudentLegit = ListLegitWithSubjectLabAndMix.size();
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
            // Initialize a map to hold the number of students in each room
            Map<Integer, Integer> studentsInLab = new HashMap<>();
            // Calculate the base number of students per room (floor division)
            int baseStudentsPerLab = numberOfStudentBlackList / numberOfLabRoomNeed;

            // Calculate the number of rooms that will get the base number of students
            int labsWithBaseStudents = numberOfStudentBlackList % numberOfLabRoomNeed;
            // Distribute the base number of students to rooms
            for (int i = 0; i < numberOfLabRoomNeed; i++) {
                Room room = availableLabRooms.get(i);
                studentsInLab.put(room.getId(), baseStudentsPerLab);
            }

            // Distribute the remaining students among the rooms with the fewest students
            for (int i = 0; i < labsWithBaseStudents; i++) {
                int updatedStudentCount = studentsInLab.get(availableLabRooms.get(i).getId()) + 1;
                studentsInLab.put(availableLabRooms.get(i).getId(), updatedStudentCount);
            }
            fillStudentToRoom(studentsInLab, availableLabRooms, ListBlackListWithSubjectLabAndMix, semesterId, planExam, slot, examCodes);
            // Initialize a map to hold the number of students in each room
            Map<Integer, Integer> mixStudentsInRoomCommon = new HashMap<>();
            // Calculate the base number of students per room (floor division)
            int baseStudentsPerRoom = numberOfStudentLegit / numberOfRoomCommonNeed;

            // Calculate the number of rooms that will get the base number of students
            int roomsWithBaseStudents = numberOfStudentLegit % numberOfRoomCommonNeed;
            // Distribute the base number of students to rooms
            for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                Room room = availableCommonRooms.get(i);
                mixStudentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
            }

            // Distribute the remaining students among the rooms with the fewest students
            for (int i = 0; i < roomsWithBaseStudents; i++) {
                int updatedStudentCount = mixStudentsInRoomCommon.get(availableCommonRooms.get(i).getId()) + 1;
                mixStudentsInRoomCommon.put(availableCommonRooms.get(i).getId(), updatedStudentCount);
            }
            fillStudentToRoom(mixStudentsInRoomCommon, availableCommonRooms, ListLegitWithSubjectLabAndMix, semesterId, planExam, slot, examCodes);
        }

    }

    public List<Room> getAvaiableRoom(PlanExam planExam, List<Scheduler> schedulers, List<Room> allRooms) {
        for (Scheduler scheduler : schedulers) {
            // Check if the start time of the PlanExam falls within the time range of any existing Scheduler
            if (scheduler.getStartDate().isAfter(planExam.getStartTime())) {
                // If yes, the room is not available, so continue to the next scheduler
                continue;
            }
            // If the start time of the PlanExam conflict with any scheduler, remove that room
            allRooms.remove(roomRepository.findById(scheduler.getRoomId()).get());
        }

        return allRooms;
    }

    public Map<Integer, Integer> calculateRoomAllocation(List<StudentSubject> allStudentBySubjectCode, List<Room> rooms) {
        int numberOfStudent = allStudentBySubjectCode.size();
        int numberOfRoomNeed = numberOfStudent / rooms.get(0).getQuantityStudent();
        if (numberOfStudent % rooms.get(0).getQuantityStudent() != 0) {
            numberOfRoomNeed++;
        }
        // Calculate the base number of students per room (floor division)
        int baseStudentsPerRoom = numberOfStudent / numberOfRoomNeed;

        // Calculate the number of rooms that will get the base number of students
        int roomsWithBaseStudents = numberOfStudent % numberOfRoomNeed;

        // Initialize a map to hold the number of students in each room
        Map<Integer, Integer> studentsInRooms = new HashMap<>();

        // Distribute the base number of students to rooms
        for (int i = 0; i < numberOfRoomNeed; i++) {
            Room room = rooms.get(i); // Assuming roomCommon is a cyclic list of rooms
            studentsInRooms.put(room.getId(), baseStudentsPerRoom);
        }

        // Distribute the remaining students among the rooms with the fewest students
        for (int i = 0; i < roomsWithBaseStudents; i++) {
            int updatedStudentCount = studentsInRooms.get(rooms.get(i).getId()) + 1;
            studentsInRooms.put(rooms.get(i).getId(), updatedStudentCount);
        }

        return studentsInRooms;
    }

//    public void assignStudentsToRooms(Map<Integer, Integer> studentsInRooms, List<Room> availableRooms,
//                                      Slot slot, PlanExam planExam, ExamCode examCode, int semesterId) {
//        for (Map.Entry<Integer, Integer> entry : studentsInRooms.entrySet()) {
//            int roomId = entry.getKey();
//            int numberOfStudents = entry.getValue();
//            Room room = roomRepository.findById(roomId).orElse(null);
//            if (room != null) {
//                Scheduler scheduler = schedulerRepository.findBySemesterIdAndSlotIdAndRoomId(semesterId, slot.getId(), room.getId());
//                if (scheduler == null) scheduler = new Scheduler();
//
//                int assignedStudentsCount = scheduler.getStudentCodes().split(",").length;
//                if (assignedStudentsCount == numberOfStudents) {
//                    // If the room is already full, skip to the next room
//                    continue;
//                }
//
//                StringBuilder studentCodesBuilder = new StringBuilder();
//                StringBuilder subjectCodesBuilder = new StringBuilder();
//                List<StudentSubject> allStudentBySubjectCode = studentSubjectRepository.findAllBySemesterIdAndSubjectCode(semesterId, planExam.getSubjectCode());
//
//                for (int i = 0; i < numberOfStudents; i++) {
//                    if (i < allStudentBySubjectCode.size()) {
//                        StudentSubject student = allStudentBySubjectCode.get(i);
//                        studentCodesBuilder.append(student.getRollNumber()).append(",");
//                        subjectCodesBuilder.append(student.getSubjectCode()).append(",");
//                    } else {
//                        break; // No more
//                    }
//                }
//                String studentCodes = studentCodesBuilder.toString();
//                String subjectCodes = subjectCodesBuilder.toString();
//
//// Assign students to the room in the scheduler
//                scheduler.setSemesterId(semesterId);
//                scheduler.setSlotId(slot.getId());
//                scheduler.setRoomId(roomId);
//                scheduler.setSubjectCode(subjectCodes);
//                scheduler.setStudentCodes(studentCodes);
//                scheduler.setExamCodeId(String.valueOf(examCode.getId()));
//                scheduler.setStartDate(planExam.getStartTime());
//                scheduler.setEndDate(planExam.getEndTime());
//
//// Save or update the scheduler
//                schedulerRepository.save(scheduler);
//            }
//        }
//    }

    public void fillStudentToRoom(Map<Integer, Integer> studentsInRooms, List<Room> availableRooms,
                                  List<StudentSubject> allStudentBySubjectCode, int semesterId, PlanExam planExam,
                                  Slot slot, StringBuilder examCodes){
        Scheduler scheduler = null;
        for (int i = 0; i < studentsInRooms.size(); i++) {
            int studentIndex = 0;
            String subjectCodes = "";
            String studentCodes = "";
            Room room = availableRooms.get(i);
            boolean roomRemoved = true;
            while (roomRemoved) {
                roomRemoved = false; // Assume no room will be removed until we find one
                for (Room r : availableRooms) {
                    scheduler = schedulerRepository.findBySemesterIdAndSlotIdAndRoomId(semesterId, slot.getId(), room.getId());
                    if (scheduler == null) scheduler = new Scheduler();
                    int assignedStudentsCount = scheduler.getStudentCodes().split(",").length; // Count assigned students
                    if (assignedStudentsCount == studentsInRooms.get(room.getId())) {
                        if (availableRooms.remove(r)) {
                            roomRemoved = true; // Mark that a room has been removed
                        }
                    } else roomRemoved = false;
                }
            }
            room = availableRooms.get(i);
            // Assign students to the current room
            for (int j = 0; j < studentsInRooms.get(room.getId()); j++) {
                if (studentIndex < allStudentBySubjectCode.size()) {
                    StudentSubject student = allStudentBySubjectCode.get(studentIndex);
                    studentCodes += (student.getRollNumber() + ",");
                    subjectCodes += (student.getSubjectCode() + ",");
                    studentIndex++;
                } else {
                    break; // No more students to assign
                }
            }
            // Assign the student to the room
            scheduler.setSemesterId(semesterId);
            scheduler.setSlotId(slot.getId());
            scheduler.setRoomId(room.getId());
            scheduler.setSubjectCode(subjectCodes);
            scheduler.setStudentCodes(studentCodes);
            scheduler.setExamCodeId(examCodes.toString());
            scheduler.setStartDate(planExam.getStartTime());
            scheduler.setEndDate(planExam.getEndTime());
            schedulerRepository.save(scheduler);
        }
    }
}
