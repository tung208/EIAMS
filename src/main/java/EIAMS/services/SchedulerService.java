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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    public List<Object> list(Integer semesterId, String search, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (startDate.isBlank() && endDate.isBlank()) {
            return schedulerRepository.findAllBySemesterIdAndSubjectCodeContains(semesterId, search);
        }
        if (startDate.isBlank() && !endDate.isBlank()) {
            LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
            return schedulerRepository.findAllBySemesterIdAndEndDateBeforeAndSubjectCodeContains(semesterId, endDateSearch, search);
        }
        if (!startDate.isBlank() && endDate.isBlank()) {
            LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
            return schedulerRepository.findAllBySemesterIdAndStartDateAfterAndSubjectCodeContains(semesterId, startDateSearch, search);
        }
        if (!startDate.isBlank() && !endDate.isBlank()) {
            LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
            LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
            return schedulerRepository.findAllBySemesterIdAndStartDateAfterAndEndDateBeforeAndSubjectCodeContains(
                    semesterId, startDateSearch, endDateSearch, search);
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

        Map<String, Collection<StudentSubject>> listStudentNoLabAndCanMix = new HashMap<>();
        Map<String, Collection<StudentSubject>> listStudentLabAndCanMix = new HashMap<>();
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
            String key = planExam.getExpectedDate().toString() + "@" + planExam.getExpectedTime();
            // Fill student
            if (subject.getNoLab() != null && subject.getNoLab() == 1 && subject.getDontMix() != null && subject.getDontMix() == 1) {
                List<Scheduler> schedulers = schedulerRepository.findAllBySemesterIdAndSubjectCodeNotAndStartDateAndEndDate(
                        semesterId, planExam.getSubjectCode(), getStartDateFromPlanExam(planExam), getEndDateFromPlanExam(planExam)
                );
                if (schedulers != null) {
                    for (Scheduler s : schedulers) {
                        if(s.getStudentId() != null) {
                            availableCommonRooms.remove(roomRepository.findById(s.getRoomId()).get());
                        }
                    }
                }
                //arrange student don't mix room and only room common
                Map<Integer, Integer> studentsInRooms = calculateRoomAllocation(allStudent, availableCommonRooms);
                // Assign students to rooms
                fillStudentToRoom(studentsInRooms, allStudent, semesterId, planExam);
            }
            if (subject.getNoLab() != null && subject.getNoLab() == 1 && (subject.getDontMix() == null || subject.getDontMix() == 0)) {
                if (listStudentNoLabAndCanMix.containsKey(key)) {
                    Collection<StudentSubject> students = listStudentNoLabAndCanMix.get(key);
                    students.addAll(allStudent);
                    listStudentNoLabAndCanMix.put(key, students);
                } else {
                    Collection<StudentSubject> newList = new ArrayList<>(allStudent);
                    listStudentNoLabAndCanMix.put(key, newList);
                }
            }
            if ((subject.getNoLab() == null || subject.getNoLab() == 0) && subject.getDontMix() != null && subject.getDontMix() == 1) {
                List<Scheduler> schedulers = schedulerRepository.findAllBySemesterIdAndSubjectCodeNotAndStartDateAndEndDate(
                        semesterId, planExam.getSubjectCode(), getStartDateFromPlanExam(planExam), getEndDateFromPlanExam(planExam)
                );
                if (schedulers != null) {
                    for (Scheduler s : schedulers) {
                        Room r = roomRepository.findById(s.getRoomId()).get();
                        if (r.getName().contains("Lab")) {
                            if(s.getStudentId() != null) {
                                availableLabRooms.remove(r);
                            }
                        } else {
                            availableCommonRooms.remove(r);
                        }
                    }
                }
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
                if (listStudentLabAndCanMix.containsKey(key)) {
                    Collection<StudentSubject> students = listStudentLabAndCanMix.get(key);
                    students.addAll(allStudent);
                    listStudentLabAndCanMix.put(key, students);
                } else {
                    Collection<StudentSubject> newList = new ArrayList<>(allStudent);
                    listStudentLabAndCanMix.put(key, newList);
                }
            }
        }

        for (Map.Entry<String, Collection<StudentSubject>> entry : listStudentNoLabAndCanMix.entrySet()) {
            String key = entry.getKey();
            String[] parts = key.split("@");
            String datePart = parts[0];
            Date date;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(datePart);
            } catch (ParseException e) {
                throw new Exception(e.getMessage());
            }
            // Extracting time
            String timePart = parts[1];
            Collection<StudentSubject> students = entry.getValue();
            PlanExam planExam = planExamRepository.findAllBySemesterIdAndExpectedDateAndExpectedTime(semesterId, date, timePart).get(0);
            List<Room> availableCommonRooms = getAvailableRoom(planExam, roomCommon);
            Map<Integer, Integer> studentsInRoomCommon = calculateRoomAllocation((List<StudentSubject>) students, availableCommonRooms);
            fillStudentToRoom(studentsInRoomCommon, (List<StudentSubject>) students, semesterId, planExam);
        }
        for (Map.Entry<String, Collection<StudentSubject>> entry : listStudentLabAndCanMix.entrySet()) {
            String key = entry.getKey();
            String[] parts = key.split("@");
            String datePart = parts[0];
            Date date;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(datePart);
            } catch (ParseException e) {
                throw new Exception(e.getMessage());
            }
            // Extracting time
            String timePart = parts[1];
            Collection<StudentSubject> students = entry.getValue();
            PlanExam planExam = planExamRepository.findAllBySemesterIdAndExpectedDateAndExpectedTime(semesterId, date, timePart).get(0);
            List<StudentSubject> allStudentLegitPerSlot = students.stream()
                    .filter(student -> (student.getBlackList() == null || student.getBlackList() == 0))
                    .collect(Collectors.toList());
            List<StudentSubject> allStudentBlackListPerSlot = students.stream()
                    .filter(student ->(student.getBlackList() != null && student.getBlackList() == 1) )
                    .collect(Collectors.toList());
            List<String> uniqueSubjectCodesBlackList = allStudentBlackListPerSlot.stream()
                    .map(StudentSubject::getSubjectCode)
                    .distinct()
                    .toList();
            List<String> uniqueSubjectCodesLegit = allStudentBlackListPerSlot.stream()
                    .map(StudentSubject::getSubjectCode)
                    .distinct()
                    .toList();

            List<Room> availableCommonRooms = getAvailableRoom(planExam, roomCommon);
            List<Room> availableLabRooms = getAvailableRoom(planExam, labs);


            int numberOfStudentBlackList = allStudentBlackListPerSlot.size();
            int numberOfStudentLegit = allStudentLegitPerSlot.size();
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
                throw new Exception("Not enough normal room for " + uniqueSubjectCodesLegit.toString() + " with " + numberOfStudentLegit + " student." +
                        "We have only " + availableCommonRooms.size() + " normal rooms." +
                        "We need at least " + numberOfRoomCommonNeed + " rooms.");
            }
            if (numberOfLabRoomNeed > availableLabRooms.size()) {
                throw new Exception("Not enough lab room for " + uniqueSubjectCodesBlackList.toString() + "with" + numberOfStudentBlackList + " student." +
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
                fillStudentToRoom(studentsInLab, allStudentBlackListPerSlot, semesterId, planExam);
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
                fillStudentToRoom(mixStudentsInRoomCommon, allStudentLegitPerSlot, semesterId, planExam);
            }
        }
    }

    public List<Room> getAvailableRoom(PlanExam planExam, List<Room> allRooms) {
        List<Scheduler> schedulers = schedulerRepository.findAllBySemesterIdAndStartDateBeforeAndEndDateAfter(
                planExam.getSemesterId(), getStartDateFromPlanExam(planExam), getStartDateFromPlanExam(planExam));
        if (!schedulers.isEmpty()) {
            for (Scheduler scheduler : schedulers) {
                if (scheduler.getStudentId() != null) {
                    allRooms.remove(roomRepository.findById(scheduler.getRoomId()).get());
                }
            }
        }
        return allRooms;
    }

    public Map<Integer, Integer> calculateRoomAllocation(List<StudentSubject> allStudentBySubjectCode, List<Room> rooms) throws Exception {
        int numberOfStudent = allStudentBySubjectCode.size();
        List<String> uniqueSubjectCodes = allStudentBySubjectCode.stream()
                .map(StudentSubject::getSubjectCode)
                .distinct()
                .toList();

        int numberOfRoomNeed = numberOfStudent / rooms.get(0).getQuantityStudent();
        if (numberOfStudent % rooms.get(0).getQuantityStudent() != 0) {
            numberOfRoomNeed++;
        }
        if (numberOfRoomNeed > rooms.size()) {
            throw new Exception("Not enough room for " + uniqueSubjectCodes + " with " + allStudentBySubjectCode.size() + " student. " +
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

        int studentIndex = 0;
        for (Map.Entry<Integer, Integer> entry : studentsInRooms.entrySet()) {
            String studentIds = "";
            String subjectCodes = "";
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
                    if (!subjectCodes.contains(student.getSubjectCode())) {
                        subjectCodes += (student.getSubjectCode() + ",");
                    }
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
            scheduler.setSubjectCode(subjectCodes);
            scheduler.setStudentId(studentIds);
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