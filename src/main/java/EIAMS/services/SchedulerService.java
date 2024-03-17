package EIAMS.services;

import EIAMS.entities.*;
import EIAMS.helper.Pagination;
import EIAMS.repositories.*;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Page<Scheduler> list(Integer semesterId, String search, String startDate, String endDate, Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        if (startDate.isBlank() && endDate.isBlank()) {
            return schedulerRepository.findAllBySemesterIdAndSubjectCodeContains(semesterId, search, pageable);
        }
        if (startDate.isBlank() && !endDate.isBlank()) {
            LocalDateTime endDateSearch = LocalDateTime.parse(endDate);
            return schedulerRepository.findAllBySemesterIdAndEndDateBeforeAndSubjectCodeContains(semesterId, endDateSearch, search, pageable);
        }
        if (!startDate.isBlank() && endDate.isBlank()) {
            LocalDateTime startDateSearch = LocalDateTime.parse(startDate);
            return schedulerRepository.findAllBySemesterIdAndStartDateAfterAndSubjectCodeContains(semesterId, startDateSearch, search, pageable);
        }
        if (!startDate.isBlank() && !endDate.isBlank()) {
            LocalDateTime endDateSearch = LocalDateTime.parse(endDate);
            LocalDateTime startDateSearch = LocalDateTime.parse(startDate);
            return schedulerRepository.findAllBySemesterIdAndStartDateAfterAndEndDateBeforeAndSubjectCodeContains(
                    semesterId, startDateSearch, endDateSearch, search, pageable);
        }
        return null;
    }

    @Override
    public Page<Student> getListStudentInARoom(Integer schedulerId, String search, Integer page, Integer limit) {
        Pageable pageable = pagination.getPageable(page, limit);
        Scheduler scheduler = schedulerRepository.findById(schedulerId).get();
        String[] assignedStudents = scheduler.getStudentId().split(",");
        Collection<Integer> studentIds = new ArrayList<>();
        for (String sId : assignedStudents) {
            studentIds.add(Integer.parseInt(sId));
        }
        List<StudentSubject> studentSubjects = studentSubjectRepository.findAllBySemesterIdAndIdIn(scheduler.getSemesterId(), studentIds);
        Collection<String> rollNumbers = new ArrayList<>();
        for (StudentSubject s : studentSubjects) {
            rollNumbers.add(s.getRollNumber());
        }
        return studentRepository.findAllByRollNumberInAndFullNameContainingIgnoreCaseOrCmtndContainingIgnoreCaseAndMemberCodeContainingIgnoreCase(
                rollNumbers, search, search, search, pageable);
    }

    @Override
    public List<Scheduler> getListSchedulerBySubjectCode(Integer semesterId, String subjectCode) {
        return schedulerRepository.findAllBySemesterIdAndSubjectCode(semesterId, subjectCode);
    }

    @Transactional
    public void deleteBySemesterId(Integer semesterId) {
        if (!schedulerRepository.findAllBySemesterId(semesterId).isEmpty()) {
            schedulerRepository.deleteBySemesterId(semesterId);
        }
    }

    public List<StudentSubject> shuffleStudents(List<StudentSubject> students) {
        // Shuffle the list
        Collections.shuffle(students);
        return students;
    }

    public <T> List<List<T>> divideList(List<T> list, int n) {
        List<List<T>> dividedLists = new ArrayList<>();
        int size = list.size();
        int chunkSize = size / n;
        int remainder = size % n;
        int index = 0;
        for (int i = 0; i < n; i++) {
            int chunkCount = chunkSize + (i < remainder ? 1 : 0);
            List<T> chunk = list.subList(index, index + chunkCount);
            dividedLists.add(chunk);
            index += chunkCount;
        }
        return dividedLists;
    }

    @Override
    @Transactional
    public void arrangeStudent(int semesterId) throws Exception {
        deleteBySemesterId(semesterId);
        List<PlanExam> planExamList = planExamRepository.findAllBySemesterId(semesterId);
        List<Room> labs = roomRepository.findAllByQuantityStudentGreaterThanAndNameContainingIgnoreCase(1, "Lab");
        List<Room> roomCommon = roomRepository.findAllByQuantityStudentGreaterThanAndNameNotContainingIgnoreCase(1, "Lab");
        Collection<String> subjectCodesNoLabAndMix = new ArrayList<>();
        Collection<String> subjectCodesLabAndMix = new ArrayList<>();
        for (PlanExam planExam : planExamList) {
            List<Room> availableLabRooms = getAvailableRoom(planExam, labs);
            List<Room> availableCommonRooms = getAvailableRoom(planExam, roomCommon);
            String code = planExam.getSubjectCode();
            Subject subject = subjectRepository.findBySemesterIdAndSubjectCode(semesterId, code);
            if (subject == null) {
                continue;
            }
            List<StudentSubject> listBlackList = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, 1);
            List<StudentSubject> listLegit = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code, null);
            List<StudentSubject> allStudentBySubjectCode = studentSubjectRepository.findAllBySemesterIdAndSubjectCode(semesterId, code);

            if (allStudentBySubjectCode.isEmpty()) {
                continue;
            }
            List<PlanExam> planExamsByCode = planExamRepository.findAllBySemesterIdAndSubjectCode(semesterId, code);
            int planExamsByCodeSize = planExamsByCode.size();
            int indexOfPlanExam = planExamsByCode.indexOf(planExam);
            List<List<StudentSubject>> dividedBlackList = divideList(listBlackList, planExamsByCodeSize);
            List<List<StudentSubject>> dividedListLegit = divideList(listLegit, planExamsByCodeSize);
            List<List<StudentSubject>> dividedAllStudentBySubjectCode = divideList(allStudentBySubjectCode, planExamsByCodeSize);
            List<StudentSubject> allBlackList = dividedBlackList.get(indexOfPlanExam);
            List<StudentSubject> allLegit = dividedListLegit.get(indexOfPlanExam);
            List<StudentSubject> allStudent = dividedAllStudentBySubjectCode.get(indexOfPlanExam);
            // Fill student
            if (subject.getNoLab() != null && subject.getNoLab() == 1 && subject.getDontMix() != null && subject.getDontMix() == 1) {
                //arrange student don't mix room and only room common
                Map<Integer, Integer> studentsInRooms = calculateRoomAllocation(planExam, allStudent, availableCommonRooms);
                // Assign students to rooms
                fillStudentToRoom(studentsInRooms, allStudent, semesterId, planExam);
            }
            if (subject.getNoLab() != null && subject.getNoLab() == 1 && (subject.getDontMix() == null || subject.getDontMix() == 0)) {
                subjectCodesNoLabAndMix.add(code);
            }
            if ((subject.getNoLab() == null || subject.getNoLab() == 0) && subject.getDontMix() != null && subject.getDontMix() == 1) {
                int numberOfStudentBlackList = allBlackList.size();
                int numberOfStudentLegit = allLegit.size();

                int numberOfLabRoomNeed = numberOfStudentBlackList / labs.get(0).getQuantityStudent();
                // Gia su lab co 25 ng. chap nhan 20 ng thi xep 1 phong. Neu ko thi xep phong thuong
                if ((numberOfStudentBlackList % labs.get(0).getQuantityStudent()) > (labs.get(0).getQuantityStudent() - 8)) {
                    numberOfLabRoomNeed++;
                } else {
                    numberOfStudentLegit += numberOfStudentBlackList % labs.get(0).getQuantityStudent();
                    numberOfStudentBlackList -= numberOfStudentBlackList % labs.get(0).getQuantityStudent();
                }
                int numberOfRoomCommonNeed = numberOfStudentLegit / roomCommon.get(0).getQuantityStudent();
                if (numberOfStudentLegit % roomCommon.get(0).getQuantityStudent() != 0) {
                    numberOfRoomCommonNeed++;
                }
                if (numberOfRoomCommonNeed > availableCommonRooms.size()) {
                    throw new Exception("Not enough normal room for " + planExam.getSubjectCode() + " with " + numberOfStudentLegit + " student." +
                            "We have only " + availableCommonRooms.size() + " normal rooms." +
                            "We need at least " + numberOfRoomCommonNeed + " rooms.");
                }
                if (numberOfLabRoomNeed > availableLabRooms.size()) {
                    throw new Exception("Not enough lab room for " + planExam.getSubjectCode() + " with " + numberOfStudentBlackList + " student." +
                            "We have only " + availableLabRooms.size() + " lab rooms." +
                            "We need at least " + numberOfLabRoomNeed + " lab rooms");
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
                    if (numberOfStudentBlackList > labs.get(0).getQuantityStudent() && labsWithBaseStudents != 0) {
                        // Distribute the remaining students among the rooms with the fewest students
                        for (int i = 0; i < labsWithBaseStudents; i++) {
                            int updatedStudentCount = studentsInLab.get(availableLabRooms.get(i).getId()) + 1;
                            studentsInLab.put(availableLabRooms.get(i).getId(), updatedStudentCount);
                        }
                    }
                    fillStudentToRoom(studentsInLab, allBlackList, semesterId, planExam);
                }
                if (numberOfRoomCommonNeed > 0) {
                    Map<Integer, Integer> studentsInRoomCommon = new HashMap<>();
                    // Calculate the base number of students per room (floor division)
                    int baseStudentsPerRoom = numberOfStudentLegit / numberOfRoomCommonNeed;
                    if (numberOfStudentLegit <= roomCommon.get(0).getQuantityStudent()) {
                        baseStudentsPerRoom = numberOfStudentLegit;
                    }

                    // Calculate the number of rooms that will get the base number of students
                    int roomsWithBaseStudents = numberOfStudentLegit % numberOfRoomCommonNeed;
                    // Distribute the base number of students to rooms
                    for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                        Room room = availableCommonRooms.get(i);
                        studentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
                    }

                    if (numberOfStudentLegit > roomCommon.get(0).getQuantityStudent() && roomsWithBaseStudents != 0) {
                        // Distribute the remaining students among the rooms with the fewest students
                        for (int i = 0; i < roomsWithBaseStudents; i++) {
                            int updatedStudentCount = studentsInRoomCommon.get(availableCommonRooms.get(i).getId()) + 1;
                            studentsInRoomCommon.put(availableCommonRooms.get(i).getId(), updatedStudentCount);
                        }
                    }
                    fillStudentToRoom(studentsInRoomCommon, allLegit, semesterId, planExam);
                }
            }
            if ((subject.getNoLab() == null || subject.getNoLab() == 0) && (subject.getDontMix() == null || subject.getDontMix() == 0)) {
                subjectCodesLabAndMix.add(code);
            }
        }
        List<StudentSubject> listStudentWithSubjectNoLabAndMix = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesNoLabAndMix);
        for (PlanExam planExam : planExamRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesNoLabAndMix)) {
            List<Room> availableCommonRooms = getAvailableRoom(planExam, roomCommon);
            List<PlanExam> planExamsByCode = planExamRepository.findAllBySemesterIdAndSubjectCode(semesterId, planExam.getSubjectCode());
            int planExamsByCodeSize = planExamsByCode.size();
            int indexOfPlanExam = planExamsByCode.indexOf(planExam);
            List<List<StudentSubject>> dividedlistStudentWithSubjectNoLabAndMix = divideList(listStudentWithSubjectNoLabAndMix, planExamsByCodeSize);
            List<StudentSubject> allStudentBySubjectCode = dividedlistStudentWithSubjectNoLabAndMix.get(indexOfPlanExam);
            if (!listStudentWithSubjectNoLabAndMix.isEmpty()) {
                Map<Integer, Integer> studentsInRoomCommon = calculateRoomAllocation(planExam, allStudentBySubjectCode, availableCommonRooms);
                fillStudentToRoom(studentsInRoomCommon, allStudentBySubjectCode, semesterId, planExam);
            }
        }

        List<StudentSubject> ListBlackListWithSubjectLabAndMix
                = studentSubjectRepository.findAllBySemesterIdAndBlackListAndSubjectCodeIn(semesterId, 1, subjectCodesLabAndMix);
        List<StudentSubject> ListLegitWithSubjectLabAndMix
                = studentSubjectRepository.findAllBySemesterIdAndBlackListAndSubjectCodeIn(semesterId, null, subjectCodesLabAndMix);
        for (PlanExam planExam : planExamRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, subjectCodesLabAndMix)) {
            List<Room> availableLabRooms = getAvailableRoom(planExam, labs);
            List<Room> availableCommonRooms = getAvailableRoom(planExam, roomCommon);

            List<PlanExam> planExamsByCode = planExamRepository.findAllBySemesterIdAndSubjectCode(semesterId, planExam.getSubjectCode());
            int planExamsByCodeSize = planExamsByCode.size();
            int indexOfPlanExam = planExamsByCode.indexOf(planExam);
            List<List<StudentSubject>> dividedListBlackListWithSubjectLabAndMix = divideList(ListBlackListWithSubjectLabAndMix, planExamsByCodeSize);
            List<List<StudentSubject>> dividedListLegitWithSubjectLabAndMix = divideList(ListLegitWithSubjectLabAndMix, planExamsByCodeSize);
            List<StudentSubject> allBlackListWithSubjectLabAndMix = dividedListBlackListWithSubjectLabAndMix.get(indexOfPlanExam);
            List<StudentSubject> allListLegitWithSubjectLabAndMix = dividedListLegitWithSubjectLabAndMix.get(indexOfPlanExam);

            int numberOfStudentBlackList = allBlackListWithSubjectLabAndMix.size();
            int numberOfStudentLegit = allListLegitWithSubjectLabAndMix.size();
            int numberOfLabRoomNeed = numberOfStudentBlackList / labs.get(0).getQuantityStudent();
            // Gia su lab co 25 ng. chap nhan 20 ng thi xep 1 phong. Neu ko thi xep phong thuong
            if ((numberOfStudentBlackList % labs.get(0).getQuantityStudent()) > (labs.get(0).getQuantityStudent() - 8)) {
                numberOfLabRoomNeed++;
            } else {
                numberOfStudentLegit += numberOfStudentBlackList % labs.get(0).getQuantityStudent();
                numberOfStudentBlackList -= numberOfStudentBlackList % labs.get(0).getQuantityStudent();
            }
            int numberOfRoomCommonNeed = numberOfStudentLegit / roomCommon.get(0).getQuantityStudent();
            if (numberOfStudentLegit % roomCommon.get(0).getQuantityStudent() != 0) {
                numberOfRoomCommonNeed++;
            }
            if (numberOfRoomCommonNeed > availableCommonRooms.size()) {
                throw new Exception("Not enough normal room for " + planExam.getSubjectCode() + " with " + numberOfStudentLegit + " student." +
                        "We have only " + availableCommonRooms.size() + " normal rooms." +
                        "We need at least " + numberOfRoomCommonNeed + " rooms.");
            }
            if (numberOfLabRoomNeed > availableLabRooms.size()) {
                throw new Exception("Not enough lab room for " + planExam.getSubjectCode() + " with " + numberOfStudentBlackList + " student." +
                        "We have only " + availableLabRooms.size() + " lab rooms." +
                        "We need at least " + numberOfLabRoomNeed + " lab rooms");
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
                if (numberOfStudentBlackList > labs.get(0).getQuantityStudent() && labsWithBaseStudents != 0) {
                    // Distribute the remaining students among the rooms with the fewest students
                    for (int i = 0; i < labsWithBaseStudents; i++) {
                        int updatedStudentCount = studentsInLab.get(availableLabRooms.get(i).getId()) + 1;
                        studentsInLab.put(availableLabRooms.get(i).getId(), updatedStudentCount);
                    }
                }
                fillStudentToRoom(studentsInLab, allBlackListWithSubjectLabAndMix, semesterId, planExam);
            }
            if (numberOfRoomCommonNeed > 0) {
                Map<Integer, Integer> mixStudentsInRoomCommon = new HashMap<>();
                // Calculate the base number of students per room (floor division)
                int baseStudentsPerRoom = numberOfStudentLegit / numberOfRoomCommonNeed;
                if (numberOfStudentLegit <= roomCommon.get(0).getQuantityStudent()) {
                    baseStudentsPerRoom = numberOfStudentLegit;
                }
                // Calculate the number of rooms that will get the base number of students
                int roomsWithBaseStudents = numberOfStudentLegit % numberOfRoomCommonNeed;
                // Distribute the base number of students to rooms
                for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                    Room room = availableCommonRooms.get(i);
                    mixStudentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
                }
                if (numberOfStudentLegit > roomCommon.get(0).getQuantityStudent() && roomsWithBaseStudents != 0) {
                    // Distribute the remaining students among the rooms with the fewest students
                    for (int i = 0; i < roomsWithBaseStudents; i++) {
                        int updatedStudentCount = mixStudentsInRoomCommon.get(availableCommonRooms.get(i).getId()) + 1;
                        mixStudentsInRoomCommon.put(availableCommonRooms.get(i).getId(), updatedStudentCount);
                    }
                }
                fillStudentToRoom(mixStudentsInRoomCommon, allListLegitWithSubjectLabAndMix, semesterId, planExam);
            }
        }
    }

    public List<Room> getAvailableRoom(PlanExam planExam, List<Room> allRooms) {
        List<Scheduler> schedulers = schedulerRepository.findAllBySemesterIdAndStartDateBeforeOrEndDateAfter(
                planExam.getSemesterId(), getEndDateFromPlanExam(planExam), getStartDateFromPlanExam(planExam));
        if (!schedulers.isEmpty()) {
            for (Scheduler scheduler : schedulers) {
                allRooms.remove(roomRepository.findById(scheduler.getRoomId()).get());
            }
        }
        return allRooms;
    }

    public Map<Integer, Integer> calculateRoomAllocation(PlanExam planExam, List<StudentSubject> allStudentBySubjectCode, List<Room> rooms) throws Exception {
        int numberOfStudent = allStudentBySubjectCode.size();
        int numberOfRoomNeed = numberOfStudent / rooms.get(0).getQuantityStudent();
        if (numberOfStudent % rooms.get(0).getQuantityStudent() != 0) {
            numberOfRoomNeed++;
        }
        if (numberOfRoomNeed > rooms.size()) {
            throw new Exception("Not enough room for " + planExam.getSubjectCode() + " with " + allStudentBySubjectCode.size() + " student. " +
                    "We have only " + rooms.size() + " rooms." +
                    "We need at least " + numberOfRoomNeed + " rooms.");
        }
        Map<Integer, Integer> studentsInRooms = new HashMap<>();
        if (numberOfRoomNeed > 0) {
            // Calculate the base number of students per room (floor division)
            int baseStudentsPerRoom;
            if (numberOfStudent <= rooms.get(0).getQuantityStudent()) {
                baseStudentsPerRoom = numberOfStudent;
            } else {
                baseStudentsPerRoom = numberOfStudent / numberOfRoomNeed;
            }

            // Distribute the base number of students to rooms
            for (int i = 0; i < numberOfRoomNeed; i++) {
                if (i < rooms.size()) {
                    Room room = rooms.get(i);
                    studentsInRooms.put(room.getId(), baseStudentsPerRoom);
                }
            }
            // Calculate the number of rooms that will get the base number of students
            int roomsWithBaseStudents = numberOfStudent % numberOfRoomNeed;
            if (numberOfStudent > rooms.get(0).getQuantityStudent() && roomsWithBaseStudents != 0) {
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
        for (int i = 0; i < examCodes.size(); i++) {
            examIds.append(examCodes.get(i).getId().toString());
            if (i < examCodes.size() - 1) {
                examIds.append(",");
            }
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
            for (int j = 0; j < entry.getValue(); j++) {
                if (studentIndex < allStudentBySubjectCode.size()) {
                    StudentSubject student = allStudentBySubjectCode.get(studentIndex);
                    studentIds += student.getId();
                    if (j < allStudentBySubjectCode.size() - 1) {
                        studentIds += ",";
                    }
                    studentIndex++;
                } else {
                    break; // No more students to assign
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