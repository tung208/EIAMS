package EIAMS.services;

import EIAMS.constants.DBTableUtils;
import EIAMS.dtos.SchedulerDetailDto;
import EIAMS.dtos.StudentScheduleDto;
import EIAMS.entities.*;
import EIAMS.helper.Pagination;
import EIAMS.repositories.*;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static EIAMS.constants.DBTableUtils.SUBJECT_CODE_SPECIAL;

@Service
@RequiredArgsConstructor
public class SchedulerService implements SchedulerServiceInterface {
    private final StudentSubjectRepository studentSubjectRepository;
    private final StudentRepository studentRepository;
    private final SemesterRepository semesterRepository;
    private final RoomRepository roomRepository;
    private final SubjectRepository subjectRepository;
    private final SlotRepository slotRepository;
    private final SchedulerRepository schedulerRepository;
    private final PlanExamRepository planExamRepository;
    private final ExamCodeRepository examCodeRepository;
    private final LecturerRepository lecturerRepository;
    private final Pagination pagination;

    @Override
    public List<Room> list(String search, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Map<String, Set<String>> subjectCodesByTimeRange = new LinkedHashMap<>();

        List<Integer> results;
        if (startDate.isBlank() && endDate.isBlank()) {
            results = schedulerRepository.findAllBySubjectCodeContains(search);
        } else if (startDate.isBlank() && !endDate.isBlank()) {
            LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
            results = schedulerRepository.findAllByEndDateBeforeAndSubjectCodeContains(endDateSearch, search);
        } else if (!startDate.isBlank() && endDate.isBlank()) {
            LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
            results = schedulerRepository.findAllByStartDateAfterAndSubjectCodeContains(startDateSearch, search);
        } else {
            LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
            LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
            results = schedulerRepository.findAllByStartDateAfterAndEndDateBeforeAndSubjectCodeContains(startDateSearch, endDateSearch, search);
        }

//        // Group subject codes by time range and eliminate duplicates
//        for (Object result : results) {
//            if (result instanceof Object[]) {
//                Object[] row = (Object[]) result;
//                String startTime = row[1].toString();
//                String endTime = row[2].toString();
//                String timeRangeKey = startTime + "_" + endTime;
//                String[] subjectCodes = row[0].toString().split(",");
//                Set<String> uniqueSubjectCodes = subjectCodesByTimeRange.computeIfAbsent(timeRangeKey, k -> new HashSet<>());
//                uniqueSubjectCodes.addAll(Arrays.asList(subjectCodes));
//            }
//        }
//
//        // Convert map entries to List<List<String>> for response
//        List<List<String>> response = new ArrayList<>();
//        for (Map.Entry<String, Set<String>> entry : subjectCodesByTimeRange.entrySet()) {
//            List<String> entryList = new ArrayList<>();
//            entryList.add(String.join(",", entry.getValue()));
//            entryList.add(entry.getKey().split("_")[0]); // Start time
//            entryList.add(entry.getKey().split("_")[1]); // End time
//            response.add(entryList);
//        }

        return roomRepository.findAllByIdIn(results);
    }

    @Override
    public List<Scheduler> listSchedulerByRoom(int roomId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
        LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
        return schedulerRepository.findAllByRoomIdAndStartDateAfterAndEndDateBefore(roomId,startDateSearch,endDateSearch);
    }

    @Override
    public List<StudentScheduleDto> getListStudentInARoom(Integer schedulerId, String search) {
        List<StudentScheduleDto> studentScheduleDtos = new ArrayList<>();
        Scheduler scheduler = schedulerRepository.findById(schedulerId).get();
        String[] assignedStudents = scheduler.getStudentId().split(",");
        Collection<Integer> studentIds = new ArrayList<>();
        for (String sId : assignedStudents) {
            studentIds.add(Integer.parseInt(sId));
        }
        List<StudentSubject> studentSubjects = studentSubjectRepository.findAllBySemesterIdAndIdIn(scheduler.getSemesterId(), studentIds);

        for (StudentSubject s : studentSubjects) {
            Student student = studentRepository.findByRollNumber(s.getRollNumber()).get();
            StudentScheduleDto studentScheduleDto = new StudentScheduleDto();
            studentScheduleDto.setId(student.getId());
            studentScheduleDto.setCmtnd(student.getCmtnd());
            studentScheduleDto.setRollNumber(student.getRollNumber());
            studentScheduleDto.setBlackList(s.getBlackList());
            studentScheduleDto.setSemesterId(s.getSemesterId());
            studentScheduleDto.setFullName(student.getFullName());
            studentScheduleDto.setMemberCode(student.getMemberCode());
            studentScheduleDto.setSubjectCode(s.getSubjectCode());
            studentScheduleDtos.add(studentScheduleDto);
        }
        return studentScheduleDtos;
    }

    @Override
    public List<SchedulerDetailDto> getListSchedulerBySubjectCode(Integer semesterId, String subjectCode) {
        List<Scheduler> schedulers = schedulerRepository.findAllBySemesterIdAndSubjectCodeContainingOrderByStartDate(semesterId, subjectCode);

        return schedulers.stream()
                .map(scheduler -> {
                    String lecturerEmail = null;
                    if(scheduler.getLecturerId() != null && lecturerRepository.findById(scheduler.getLecturerId()).isPresent()){
                        lecturerEmail = lecturerRepository.findById(scheduler.getLecturerId()).get().getEmail();
                    };
                    return SchedulerDetailDto.builder()
                            .id(scheduler.getId())
                            .semesterId(scheduler.getSemesterId())
                            .semesterName(semesterRepository.findById(scheduler.getSemesterId()).get().getName())
                            .lecturerId(scheduler.getLecturerId())
                            .lecturerEmail(lecturerEmail)
                            .startDate(scheduler.getStartDate())
                            .endDate(scheduler.getEndDate())
                            .examCodeId(scheduler.getExamCodeId())
                            .roomId(scheduler.getRoomId())
                            .roomName(roomRepository.findById(scheduler.getRoomId()).get().getName())
                            .studentId(scheduler.getStudentId())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void swapLecturer(int schedulerId, int schedulerSwapId) throws Exception {
        Scheduler scheduler = schedulerRepository.findById(schedulerId).get();
        int oldId = scheduler.getLecturerId();
        Scheduler schedulerSwap = schedulerRepository.findById(schedulerSwapId).get();
        int newId = schedulerSwap.getLecturerId();
        LocalDateTime newStartDate = schedulerSwap.getStartDate();
        LocalDateTime newEndDate = schedulerSwap.getEndDate();
        if(!schedulerRepository.findAllBySemesterIdAndStartDateAndEndDateAndIdNotAndLectureId(
                scheduler.getSemesterId(), newStartDate, newEndDate, scheduler.getId(), scheduler.getLecturerId()).isEmpty()){
            throw new Exception("It is not possible to change teachers because this teacher has an exam scheduled conflict time");
        } else {
            scheduler.setLecturerId(newId);
            schedulerSwap.setLecturerId(oldId);
            schedulerRepository.save(scheduler);
            schedulerRepository.save(schedulerSwap);
        }
    }

    @Override
    public SchedulerDetailDto get(int schedulerId) {
        if(schedulerRepository.findById(schedulerId).isPresent()) {
            Scheduler s = schedulerRepository.findById(schedulerId).get();
            Room room = roomRepository.findById(s.getRoomId()).get();
            String lecturerEmail = null;
            if(s.getLecturerId() != null){
                Lecturer lecturer = lecturerRepository.findById(s.getLecturerId()).get();
                lecturerEmail = lecturer.getEmail();
            }
            String semesterName = null;
            if(semesterRepository.findById(s.getSemesterId()).isPresent()){
                semesterName = semesterRepository.findById(s.getSemesterId()).get().getName();
            }
            SchedulerDetailDto schedulerDetailDto = new SchedulerDetailDto();
            schedulerDetailDto.setId(s.getId());
            schedulerDetailDto.setSemesterId(s.getSemesterId());
            schedulerDetailDto.setStartDate(s.getStartDate());
            schedulerDetailDto.setEndDate(s.getEndDate());
            schedulerDetailDto.setLecturerId(s.getLecturerId());
            schedulerDetailDto.setRoomId(s.getRoomId());
            schedulerDetailDto.setStudentId(s.getStudentId());
            schedulerDetailDto.setRoomName(room.getName());
            schedulerDetailDto.setLecturerEmail(lecturerEmail);
            schedulerDetailDto.setExamCodeId(s.getExamCodeId());
            schedulerDetailDto.setSlotId(s.getSlotId());
            schedulerDetailDto.setSemesterName(semesterName);

            return schedulerDetailDto;

        } else return null;
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
        List<Room> labs = roomRepository.findAllBySemesterIdAndQuantityStudentGreaterThanAndNameContainingIgnoreCase(semesterId, 1, "Lab");
        List<Room> roomCommon = roomRepository.findAllBySemesterIdAndQuantityStudentGreaterThanAndNameNotContainingIgnoreCase(semesterId, 1, "Lab");
        int quantityLabRoom = DBTableUtils.ROOM_LAB_QUANTITY;
        int quantityNormalRoom = DBTableUtils.ROOM_NORMAL_QUANTITY;
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
                        if (s.getStudentId() != null) {
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
                        if (s.getStudentId() != null) {
                            if (r.getName().contains("Lab")) {
                                availableLabRooms.remove(r);
                            } else {
                                availableCommonRooms.remove(r);
                            }
                        }
                    }
                }
                int numberOfStudentBlackList = allBlackList.size();
                int numberOfStudentLegit = allLegit.size();

                int numberOfLabRoomNeed = numberOfStudentBlackList / quantityLabRoom;
                // Gia su lab co 28 ng. chap nhan 14 ng thi xep 1 phong. Neu ko thi xep phong thuong
                if ((numberOfStudentBlackList % quantityLabRoom) > (quantityLabRoom / 2)) {
                    numberOfLabRoomNeed++;
                } else {
                    numberOfStudentLegit += numberOfStudentBlackList % quantityLabRoom;
                    numberOfStudentBlackList -= numberOfStudentBlackList % quantityLabRoom;
                    for (int i = 0; i < (numberOfStudentBlackList % quantityLabRoom); i++) {
                        StudentSubject studentToMove = allBlackList.remove(0);
                        allLegit.add(studentToMove);
                    }
                }
                int numberOfRoomCommonNeed = numberOfStudentLegit / quantityNormalRoom;
                if (numberOfStudentLegit % quantityNormalRoom != 0) {
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
                    if (numberOfStudentBlackList <= quantityLabRoom) {
                        baseStudentsPerLab = numberOfStudentBlackList;
                        for (int i = 0; i < numberOfLabRoomNeed; i++) {
                            Room room = availableLabRooms.get(i);
                            studentsInLab.put(room.getId(), baseStudentsPerLab);
                        }
                    }
                    else {
                        int labsWithBaseStudents = numberOfStudentBlackList % numberOfLabRoomNeed;
                        // Distribute the base number of students to rooms
                        for (int i = 0; i < numberOfLabRoomNeed; i++) {
                            Room room = availableLabRooms.get(i);
                            studentsInLab.put(room.getId(), baseStudentsPerLab);
                        }
                        if (labsWithBaseStudents != 0) {
                            for (int i = 0; i < labsWithBaseStudents; i++) {
                                int updatedStudentCount = studentsInLab.get(availableLabRooms.get(i).getId()) + 1;
                                studentsInLab.put(availableLabRooms.get(i).getId(), updatedStudentCount);
                            }
                        }
                    }

                    fillStudentToRoom(studentsInLab, allBlackList, semesterId, planExam);
                }
                if (numberOfRoomCommonNeed > 0) {
                    Map<Integer, Integer> studentsInRoomCommon = new HashMap<>();
                    // Calculate the base number of students per room (floor division)
                    int baseStudentsPerRoom = numberOfStudentLegit / numberOfRoomCommonNeed;
                    if (numberOfStudentLegit <= quantityNormalRoom) {
                        baseStudentsPerRoom = numberOfStudentLegit;
                        for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                            Room room = availableCommonRooms.get(i);
                            studentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
                        }
                    }
                    else {
                        int roomsWithBaseStudents = numberOfStudentLegit % numberOfRoomCommonNeed;
                        for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                            Room room = availableCommonRooms.get(i);
                            studentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
                        }

                        if (roomsWithBaseStudents != 0) {
                            for (int i = 0; i < roomsWithBaseStudents; i++) {
                                int updatedStudentCount = studentsInRoomCommon.get(availableCommonRooms.get(i).getId()) + 1;
                                studentsInRoomCommon.put(availableCommonRooms.get(i).getId(), updatedStudentCount);
                            }
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
                    .filter(student -> (student.getBlackList() != null && student.getBlackList() == 1))
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
            int numberOfLabRoomNeed = numberOfStudentBlackList / quantityLabRoom;
            // Gia su lab co 28 ng. chap nhan 14 ng thi xep 1 phong. Neu ko thi xep phong thuong
            if ((numberOfStudentBlackList % quantityLabRoom) > (quantityLabRoom / 2)) {
                numberOfLabRoomNeed++;
            } else {
                numberOfStudentLegit += numberOfStudentBlackList % quantityLabRoom;
                numberOfStudentBlackList -= numberOfStudentBlackList % quantityLabRoom;
                for (int i = 0; i < (numberOfStudentBlackList % quantityLabRoom); i++) {
                    StudentSubject studentToMove = allStudentBlackListPerSlot.remove(0);
                    allStudentLegitPerSlot.add(studentToMove);
                }
            }
            int numberOfRoomCommonNeed = numberOfStudentLegit / quantityNormalRoom;
            if (numberOfStudentLegit % quantityNormalRoom != 0) {
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

            if (numberOfLabRoomNeed > 0) {
                Map<Integer, Integer> studentsInLab = new HashMap<>();
                int baseStudentsPerLab = numberOfStudentBlackList / numberOfLabRoomNeed;
                if (numberOfStudentBlackList <= quantityLabRoom) {
                    baseStudentsPerLab = numberOfStudentBlackList;
                    for (int i = 0; i < numberOfLabRoomNeed; i++) {
                        Room room = availableLabRooms.get(i);
                        studentsInLab.put(room.getId(), baseStudentsPerLab);
                    }
                } else {
                    int labsWithBaseStudents = numberOfStudentBlackList % numberOfLabRoomNeed;
                    for (int i = 0; i < numberOfLabRoomNeed; i++) {
                        Room room = availableLabRooms.get(i);
                        studentsInLab.put(room.getId(), baseStudentsPerLab);
                    }
                    if (labsWithBaseStudents != 0) {
                        for (int i = 0; i < labsWithBaseStudents; i++) {
                            int updatedStudentCount = studentsInLab.get(availableLabRooms.get(i).getId()) + 1;
                            studentsInLab.put(availableLabRooms.get(i).getId(), updatedStudentCount);
                        }
                    }
                }
                fillStudentToRoom(studentsInLab, allStudentBlackListPerSlot, semesterId, planExam);
            }
            if (numberOfRoomCommonNeed > 0) {
                Map<Integer, Integer> mixStudentsInRoomCommon = new HashMap<>();
                // Calculate the base number of students per room (floor division)
                int baseStudentsPerRoom = numberOfStudentLegit / numberOfRoomCommonNeed;
                if (numberOfStudentLegit <= quantityNormalRoom) {
                    baseStudentsPerRoom = numberOfStudentLegit;
                    for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                        Room room = availableCommonRooms.get(i);
                        mixStudentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
                    }
                } else {
                    int roomsWithBaseStudents = numberOfStudentLegit % numberOfRoomCommonNeed;

                    for (int i = 0; i < numberOfRoomCommonNeed; i++) {
                        Room room = availableCommonRooms.get(i);
                        mixStudentsInRoomCommon.put(room.getId(), baseStudentsPerRoom);
                    }
                    if (roomsWithBaseStudents != 0) {
                        for (int i = 0; i < roomsWithBaseStudents; i++) {
                            int updatedStudentCount = mixStudentsInRoomCommon.get(availableCommonRooms.get(i).getId()) + 1;
                            mixStudentsInRoomCommon.put(availableCommonRooms.get(i).getId(), updatedStudentCount);
                        }
                    }
                }
                fillStudentToRoom(mixStudentsInRoomCommon, allStudentLegitPerSlot, semesterId, planExam);
            }
        }
    }

    public static Date getDateFromLocalDateTime(LocalDateTime localDateTime) throws ParseException {
        LocalDate date = localDateTime.toLocalDate(); // Extract date part
        LocalDateTime midnight = date.atStartOfDay(); // Set time to midnight
        return Date.from(midnight.atZone(ZoneId.systemDefault()).toInstant());

    }

    public static String getTimeStringFromLocalDateTime(LocalDateTime localDateTime) {
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();

        return String.format("%02dH%02d", hour, minute);
    }

    @Override
    public void setExamCode(int semesterId) throws Exception {
        List<Scheduler> schedulers = schedulerRepository.findAll();
        if (schedulers.isEmpty()) return;

        for (Scheduler s : schedulers) {
            String subjectCodes = s.getSubjectCode();
            String[] subjectCodesArray = subjectCodes.split(",");
            StringBuilder examIdsBuilder = new StringBuilder();

            for (String subjectCode : subjectCodesArray) {
                List<ExamCode> examCodes = examCodeRepository.findBySemesterIdAndSubjectCode(semesterId, subjectCode);
                List<PlanExam> planExamsByCode = planExamRepository.findAllBySemesterIdAndSubjectCode(semesterId, subjectCode);
                if(planExamsByCode.size() > examCodes.size()) {
                    throw new Exception("Not enough exam code for " + planExamsByCode.size() + "slots of subject " + subjectCode);
                }
                String expectedTime = getTimeStringFromLocalDateTime(s.getStartDate())+"-"+getTimeStringFromLocalDateTime(s.getEndDate());
                Date expectedDate = getDateFromLocalDateTime(s.getStartDate());
                PlanExam planExam = planExamRepository.findBySemesterIdAndSubjectCodeAndExpectedDateAndExpectedTime(
                        semesterId, subjectCode, expectedDate, expectedTime);
                int indexOfPlanExam = planExamsByCode.indexOf(planExam);
                examIdsBuilder.append(examCodes.get(indexOfPlanExam).getId()).append(",");
            }

            String examIds = examIdsBuilder.toString();
            if (examIds.endsWith(",")) {
                examIds = examIds.substring(0, examIds.length() - 1);
            }

            s.setExamCodeId(examIds);
            schedulerRepository.save(s);
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
            int baseStudentsPerRoom;
            if (numberOfStudent <= rooms.get(0).getQuantityStudent()) {
                baseStudentsPerRoom = numberOfStudent;
                for (int i = 0; i < numberOfRoomNeed; i++) {
                    if (i < rooms.size()) {
                        Room room = rooms.get(i);
                        studentsInRooms.put(room.getId(), baseStudentsPerRoom);
                    }
                }
            } else {
                baseStudentsPerRoom = numberOfStudent / numberOfRoomNeed;
                for (int i = 0; i < numberOfRoomNeed; i++) {
                    if (i < rooms.size()) {
                        Room room = rooms.get(i);
                        studentsInRooms.put(room.getId(), baseStudentsPerRoom);
                    }
                }
                int roomsWithBaseStudents = numberOfStudent % numberOfRoomNeed;
                if (roomsWithBaseStudents != 0) {
                    for (int i = 0; i < roomsWithBaseStudents; i++) {
                        if (i < rooms.size()) {
                            int updatedStudentCount = studentsInRooms.get(rooms.get(i).getId()) + 1;
                            studentsInRooms.put(rooms.get(i).getId(), updatedStudentCount);
                        }
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
            if (subjectCodes.endsWith(",")) {
                subjectCodes = subjectCodes.substring(0, subjectCodes.length() - 1);
            }
            if (studentIds.endsWith(",")) {
                studentIds = studentIds.substring(0, studentIds.length() - 1);
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

    @Override
    @Transactional
    public void arrangeLecturer(int semesterId){
        schedulerRepository.resetLecturerId(semesterId);
        List<Lecturer> allLecturers = lecturerRepository.findAllBySemesterId(semesterId);
//        int totalSlots = (int) schedulerRepository.countAllBySemesterId(semesterId);
//        List<Scheduler> schedulers = schedulerRepository.findAllBySemesterIdOrderByStartDate(semesterId);
        List<Scheduler> schedulerWithSpecialSubject = schedulerRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, SUBJECT_CODE_SPECIAL);

        List<Scheduler> schedulerWithNormalSubject = schedulerRepository.findAllBySemesterIdAndSubjectCodeNotIn(semesterId, SUBJECT_CODE_SPECIAL);

        for (Scheduler scheduler : schedulerWithSpecialSubject) {
            String[] subjectCodes = scheduler.getSubjectCode().split(",");
            String subjectMatch = null;

            // Find a subject code match in the scheduler
            for (String code : subjectCodes) {
                if (SUBJECT_CODE_SPECIAL.contains(code)) {
                    subjectMatch = code;
                    break;
                }
            }
            for (Lecturer l: allLecturers ) {
                if(isAvailableSlotExamOfLecturer(semesterId, l.getId()) && subjectMatch != null && Arrays.asList(l.getExamSubject().split(",")).contains(subjectMatch)
                    && isNotHaveSlotExamOfLecturer(semesterId,l.getId(), scheduler)) {
                    scheduler.setLecturerId(l.getId());
                    schedulerRepository.save(scheduler);
                    break;
                }
            }
        }
        for (Scheduler scheduler : schedulerWithNormalSubject) {
            for (Lecturer l: allLecturers ) {
                if(isAvailableSlotExamOfLecturer(semesterId, l.getId()) && isNotHaveSlotExamOfLecturer(semesterId,l.getId(), scheduler)) {
                    scheduler.setLecturerId(l.getId());
                    schedulerRepository.save(scheduler);
                    break;
                }
            }
        }
    }

    @Override
    public void updateLecturer(int schedulerId, int lecturerId) throws Exception {
        Scheduler scheduler = schedulerRepository.findById(schedulerId).get();
        LocalDateTime startDate = scheduler.getStartDate();
        LocalDateTime endDate = scheduler.getEndDate();
        if(lecturerId != scheduler.getLecturerId()) {
            if (!schedulerRepository.findAllBySemesterIdAndStartDateAndEndDateAndLectureId(
                    scheduler.getSemesterId(), startDate, endDate, scheduler.getId(), lecturerId).isEmpty()) {
                throw new Exception("It is not possible to change teachers because this teacher has an exam scheduled conflict time");
            } else {
                scheduler.setLecturerId(lecturerId);
                schedulerRepository.save(scheduler);
            }
        }
    }

    public boolean isAvailableSlotExamOfLecturer(int semesterId, int lecturerId) {
        Lecturer lecturer = lecturerRepository.findLecturerByIdAndSemesterId(lecturerId, semesterId);
        int slotMin = lecturer.getTotalSlot();
        int slotExamNow = schedulerRepository.countAllBySemesterIdAndLecturerId(semesterId, lecturerId);
        return slotExamNow < slotMin;
    }
    public boolean isNotHaveSlotExamOfLecturer(int semesterId, int lecturerId, Scheduler scheduler) {
        List<Scheduler> schedulers = schedulerRepository.findBySemesterIdAndLecturerIdAvailable(semesterId, lecturerId, scheduler.getStartDate(), scheduler.getEndDate());
        return schedulers.isEmpty();
    }
}