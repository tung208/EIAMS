package EIAMS.services;

import EIAMS.constants.DBTableUtils;
import EIAMS.dtos.*;
import EIAMS.entities.*;
import EIAMS.repositories.*;
import EIAMS.services.interfaces.SchedulerServiceInterface;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.InvalidParameterException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
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
    private final SchedulerRepository schedulerRepository;
    private final PlanExamRepository planExamRepository;
    private final ExamCodeRepository examCodeRepository;
    private final LecturerRepository lecturerRepository;

    @Override
    public List<RoomScheduleDto> list(Integer semesterId, String search, String startDate, String endDate, String lecturerId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        List<Object[]> results;
        if (startDate.isBlank() || endDate.isBlank()) {
            throw new InvalidParameterException("Start date and end date cannot be empty");
        } else {
            LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
            LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
            if (lecturerId == null || lecturerId.isBlank()) {
                results = schedulerRepository.findAllRoomByDate(startDateSearch, endDateSearch, semesterId);
            } else if (lecturerId.equals("-1")) {
                results = schedulerRepository.findAllRoomByDateAndLecturerIdIsNull(startDateSearch, endDateSearch, semesterId);
            } else {
                results = schedulerRepository.findAllRoomByDateAndLecturerId(startDateSearch, endDateSearch, Integer.valueOf(lecturerId), semesterId);
            }
        }

        List<RoomScheduleDto> roomScheduleDtos = new ArrayList<>();
        for (Object[] o : results) {
            RoomScheduleDto dto = new RoomScheduleDto();
            int roomId = (Integer) o[0];
            Room room = roomRepository.findById(roomId).get();
            String roomName = room.getName();
            if (search == null || search.isEmpty() || roomName.toLowerCase().contains(search.toLowerCase())) {
                dto.setId(roomId);
                dto.setName(room.getName());
                dto.setQuantityStudent(room.getQuantityStudent());
                dto.setSemesterId(room.getSemesterId());
                dto.setType(room.getType());

                Date d = (Date) o[1];
                Timestamp timestamp = new Timestamp(d.getTime());

                // Convert Timestamp to LocalDateTime
                LocalDateTime localDateTime = timestamp.toLocalDateTime();
                dto.setDate(localDateTime);
                roomScheduleDtos.add(dto);
            }
        }

        return roomScheduleDtos;
    }

    @Override
    public List<SchedulerDetailDto> listSchedulerByRoom(Integer semesterId, int roomId, String startDate, String endDate, String lecturerId) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
        LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
        if (lecturerId.isBlank()) {
            List<Scheduler> schedulers = schedulerRepository.findAllByRoomIdAndStartDateAfterAndEndDateBefore(roomId, startDateSearch, endDateSearch, semesterId);
            return schedulers.stream()
                    .map(scheduler -> {
                        String lecturerEmail = null;
                        String lecturerCode = null;
                        if (scheduler.getLecturerId() != null && lecturerRepository.findById(scheduler.getLecturerId()).isPresent()) {
                            Lecturer l = lecturerRepository.findById(scheduler.getLecturerId()).get();
                            lecturerEmail = l.getEmail();
                            lecturerCode = l.getCodeName();
                        }
                        return SchedulerDetailDto.builder()
                                .id(scheduler.getId())
                                .semesterId(scheduler.getSemesterId())
                                .semesterName(semesterRepository.findById(scheduler.getSemesterId()).get().getName())
                                .lecturerId(scheduler.getLecturerId())
                                .lecturerEmail(lecturerEmail)
                                .lecturerCodeName(lecturerCode)
                                .startDate(scheduler.getStartDate())
                                .endDate(scheduler.getEndDate())
                                .examCodeId(scheduler.getExamCodeId())
                                .roomId(scheduler.getRoomId())
                                .subjectCode(scheduler.getSubjectCode())
                                .roomName(roomRepository.findById(scheduler.getRoomId()).get().getName())
                                .studentId(scheduler.getStudentId())
                                .build();
                    })
                    .collect(Collectors.toList());
        } else {
            List<Scheduler> schedulers = schedulerRepository.findAllBySemesterIdAndRoomIdAndStartDateAfterAndEndDateBeforeAndLecturerId(semesterId, roomId, startDateSearch, endDateSearch, Integer.valueOf(lecturerId));
            return schedulers.stream()
                    .map(scheduler -> {
                        String lecturerEmail = null;
                        String lecturerCode = null;
                        if (scheduler.getLecturerId() != null && lecturerRepository.findById(scheduler.getLecturerId()).isPresent()) {
                            Lecturer l = lecturerRepository.findById(scheduler.getLecturerId()).get();
                            lecturerEmail = l.getEmail();
                            lecturerCode = l.getCodeName();
                        }
                        return SchedulerDetailDto.builder()
                                .id(scheduler.getId())
                                .semesterId(scheduler.getSemesterId())
                                .semesterName(semesterRepository.findById(scheduler.getSemesterId()).get().getName())
                                .lecturerId(scheduler.getLecturerId())
                                .lecturerEmail(lecturerEmail)
                                .lecturerCodeName(lecturerCode)
                                .startDate(scheduler.getStartDate())
                                .endDate(scheduler.getEndDate())
                                .examCodeId(scheduler.getExamCodeId())
                                .subjectCode(scheduler.getSubjectCode())
                                .roomId(scheduler.getRoomId())
                                .roomName(roomRepository.findById(scheduler.getRoomId()).get().getName())
                                .studentId(scheduler.getStudentId())
                                .build();
                    })
                    .collect(Collectors.toList());
        }

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
                    String lecturerCode = null;
                    if (scheduler.getLecturerId() != null && lecturerRepository.findById(scheduler.getLecturerId()).isPresent()) {
                        Lecturer l = lecturerRepository.findById(scheduler.getLecturerId()).get();
                        lecturerEmail = l.getEmail();
                        lecturerCode = l.getCodeName();
                    }
                    return SchedulerDetailDto.builder()
                            .id(scheduler.getId())
                            .semesterId(scheduler.getSemesterId())
                            .semesterName(semesterRepository.findById(scheduler.getSemesterId()).get().getName())
                            .lecturerId(scheduler.getLecturerId())
                            .lecturerEmail(lecturerEmail)
                            .lecturerCodeName(lecturerCode)
                            .startDate(scheduler.getStartDate())
                            .endDate(scheduler.getEndDate())
                            .examCodeId(scheduler.getExamCodeId())
                            .subjectCode(scheduler.getSubjectCode())
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
        Lecturer newLecturer = lecturerRepository.findById(newId).get();
        LocalDateTime newStartDate = schedulerSwap.getStartDate();
        LocalDateTime newEndDate = schedulerSwap.getEndDate();
        String[] subjectCodes = scheduler.getSubjectCode().split(",");
        String subjectMatch = Arrays.stream(subjectCodes)
                .filter(SUBJECT_CODE_SPECIAL::contains)
                .findFirst()
                .orElse("");
        String[] subjectCodesSwap = newLecturer.getExamSubject().split(",");
        String subjectSwapMatch = Arrays.stream(subjectCodesSwap)
                .filter(SUBJECT_CODE_SPECIAL::contains)
                .findFirst()
                .orElse("");

//        if (!Arrays.asList(subjectCodes).contains(subjectSwapMatch) || !Arrays.asList(subjectCodesSwap).contains(subjectMatch)) {
//            throw new Exception("This is room have special subject. Need to choose a teacher who can proctor this subject ");
//        }
        if (!schedulerRepository.findAllBySemesterIdAndStartDateAndEndDateAndIdNotAndLectureId(
                scheduler.getSemesterId(), newStartDate, newEndDate, scheduler.getId(), scheduler.getLecturerId()).isEmpty()
                || !schedulerRepository.findAllBySemesterIdAndStartDateAndEndDateAndIdNotAndLectureId(
                scheduler.getSemesterId(), scheduler.getStartDate(), scheduler.getEndDate(), schedulerSwap.getId(), schedulerSwap.getLecturerId()).isEmpty()) {
            throw new Exception("It is not possible to change teachers because this teacher has an exam scheduled conflict time");
        } else {
            scheduler.setLecturerId(newId);
            schedulerSwap.setLecturerId(oldId);
            schedulerRepository.save(scheduler);
            schedulerRepository.save(schedulerSwap);
        }
        calculateWorking(scheduler.getSemesterId());
    }

    @Override
    public SchedulerDetailDto get(int schedulerId) {
        if (schedulerRepository.findById(schedulerId).isPresent()) {
            Scheduler s = schedulerRepository.findById(schedulerId).get();
            Room room = roomRepository.findById(s.getRoomId()).get();
            String lecturerEmail = null;
            if (s.getLecturerId() != null) {
                Lecturer lecturer = lecturerRepository.findById(s.getLecturerId()).get();
                lecturerEmail = lecturer.getEmail();
            }
            String semesterName = null;
            if (semesterRepository.findById(s.getSemesterId()).isPresent()) {
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
            schedulerDetailDto.setSubjectCode(s.getSubjectCode());
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
        if (semesterRepository.count() == 0 || roomRepository.countAllBySemesterId(semesterId) == 0 ||
                planExamRepository.countAllBySemesterId(semesterId) == 0 ||
                studentSubjectRepository.countAllBySemesterId(semesterId) == 0 ||
                subjectRepository.countAllBySemesterId(semesterId) == 0) {
            throw new Exception("Not have enough data to arrange students");
        }
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
            List<StudentSubject> listBlackList = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndBlackList(semesterId, code);
            List<StudentSubject> listLegit = studentSubjectRepository.findAllBySemesterIdAndSubjectCodeAndNotBlackList(semesterId, code);
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
                int numberOfAvailableLabRooms = availableLabRooms.size();
                int numberOfStudentBlackList = allBlackList.size();
                int numberOfStudentLegit = allLegit.size();

                int numberOfLabRoomNeed = numberOfStudentBlackList / quantityLabRoom;

                if ((numberOfStudentBlackList % quantityLabRoom) != 0) {
                    numberOfLabRoomNeed++;
                }
                if (numberOfLabRoomNeed > numberOfAvailableLabRooms) {
                    numberOfLabRoomNeed = numberOfAvailableLabRooms;
                    int numberOfStudentBlackListToNormalRoom = numberOfStudentBlackList - (numberOfAvailableLabRooms * quantityLabRoom);
                    numberOfStudentLegit += numberOfStudentBlackListToNormalRoom;
                    numberOfStudentBlackList -= numberOfStudentBlackListToNormalRoom;
                    for (int i = 0; i < numberOfStudentBlackListToNormalRoom; i++) {
                        StudentSubject studentToMove = allBlackList.remove(0);
                        allLegit.add(studentToMove);
                    }
                } else if (numberOfStudentBlackList % quantityLabRoom != 0) {
                    int numberOfStudentLegitToLabRoom = quantityLabRoom - (numberOfStudentBlackList % quantityLabRoom);
                    numberOfStudentLegit -= numberOfStudentLegitToLabRoom;
                    numberOfStudentBlackList += numberOfStudentLegitToLabRoom;
                    for (int i = 0; i < numberOfStudentLegitToLabRoom; i++) {
                        StudentSubject studentToMove = allLegit.remove(0);
                        allBlackList.add(studentToMove);
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
//                if (numberOfLabRoomNeed > availableLabRooms.size()) {
//                    throw new Exception("Not enough lab room for " + planExam.getSubjectCode() + " with " + numberOfStudentBlackList + " student." +
//                            "We have only " + availableLabRooms.size() + " lab rooms." +
//                            "We need at least " + numberOfLabRoomNeed + " lab rooms");
//                }
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
                    } else {
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
                    } else {
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
            List<Room> availableCommonRooms = getAvailableRoom(planExam, roomCommon);
            List<Room> availableLabRooms = getAvailableRoom(planExam, labs);
            int numberOfAvailableLabRooms = availableLabRooms.size();

            int numberOfStudentBlackList = allStudentBlackListPerSlot.size();
            int numberOfStudentLegit = allStudentLegitPerSlot.size();
            int numberOfLabRoomNeed = numberOfStudentBlackList / quantityLabRoom;
            if ((numberOfStudentBlackList % quantityLabRoom) != 0) {
                numberOfLabRoomNeed++;
            }
            if (numberOfLabRoomNeed > numberOfAvailableLabRooms) {
                numberOfLabRoomNeed = numberOfAvailableLabRooms;
                int numberOfStudentBlackListToNormalRoom = numberOfStudentBlackList - (numberOfAvailableLabRooms * quantityLabRoom);
                numberOfStudentLegit += numberOfStudentBlackListToNormalRoom;
                numberOfStudentBlackList -= numberOfStudentBlackListToNormalRoom;
                for (int i = 0; i < numberOfStudentBlackListToNormalRoom; i++) {
                    StudentSubject studentToMove = allStudentBlackListPerSlot.remove(0);
                    allStudentLegitPerSlot.add(studentToMove);
                }
            } else if (numberOfStudentBlackList % quantityLabRoom != 0) {
                int numberOfStudentLegitToLabRoom = quantityLabRoom - (numberOfStudentBlackList % quantityLabRoom);
                numberOfStudentLegit -= numberOfStudentLegitToLabRoom;
                numberOfStudentBlackList += numberOfStudentLegitToLabRoom;
                for (int i = 0; i < numberOfStudentLegitToLabRoom; i++) {
                    StudentSubject studentToMove = allStudentLegitPerSlot.remove(0);
                    allStudentBlackListPerSlot.add(studentToMove);
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
//            if (numberOfLabRoomNeed > availableLabRooms.size()) {
//                throw new Exception("Not enough lab room for " + planExam.getSubjectCode() + " with " + numberOfStudentBlackList + " student." +
//                        "We have only " + availableLabRooms.size() + " lab rooms." +
//                        "We need at least " + numberOfLabRoomNeed + " lab rooms");
//            }

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

    public static Date getDateFromLocalDateTime(LocalDateTime localDateTime) {
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
        if (schedulers.isEmpty()){
            throw new Exception("Not enough data to set");
        }
        for (Scheduler s : schedulers) {
            String subjectCodes = s.getSubjectCode();
            String[] subjectCodesArray = subjectCodes.split(",");
            StringBuilder examIdsBuilder = new StringBuilder();

            for (String subjectCode : subjectCodesArray) {
                List<ExamCode> examCodes = examCodeRepository.findBySemesterIdAndSubjectCode(semesterId, subjectCode);
                List<PlanExam> planExamsByCode = planExamRepository.findAllBySemesterIdAndSubjectCode(semesterId, subjectCode);
                if (planExamsByCode.size() > examCodes.size()) {
                    throw new Exception("Not enough exam code for " + planExamsByCode.size() + "slots of subject " + subjectCode);
                }
                String expectedTime = getTimeStringFromLocalDateTime(s.getStartDate()) + "-" + getTimeStringFromLocalDateTime(s.getEndDate());
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
            scheduler.setType(planExam.getTypeExam());
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
    public void arrangeLecturer(int semesterId) throws Exception {
        schedulerRepository.resetLecturerId(semesterId);
        int numberOfScheduler = (int) schedulerRepository.countAllBySemesterId(semesterId);
        int numberOfLecturer = lecturerRepository.countAllBySemesterId(semesterId);
        if (numberOfScheduler == 0 || numberOfLecturer == 0) {
            throw new Exception("We don't have any scheduler or teacher to arrange lecturer");
        }
        int numberSlotPerLecturer = numberOfScheduler / numberOfLecturer;
        AtomicInteger remainderSlots = new AtomicInteger(numberOfScheduler % numberOfLecturer);
        List<Scheduler> schedulersToSave = new ArrayList<>();
        List<Scheduler> schedulerWithSpecialSubject = schedulerRepository.findAllBySemesterIdAndSubjectCodeIn(semesterId, SUBJECT_CODE_SPECIAL);
        List<Scheduler> schedulerWithNormalSubject = schedulerRepository.findAllBySemesterIdAndSubjectCodeNotIn(semesterId, SUBJECT_CODE_SPECIAL);
        List<Lecturer> allLecturers = lecturerRepository.findAllBySemesterId(semesterId);
        List<LecturerToArrangeDto> lecturerToArrange = new ArrayList<>();
        allLecturers.forEach(lecturer -> {
            LecturerToArrangeDto lecturerToArrangeDto = new LecturerToArrangeDto();
            lecturerToArrangeDto.setLecturer(lecturer);
            lecturerToArrangeDto.setCountSlotArrange(lecturer.getTotalSlot());
            if (lecturer.getTotalSlot() > numberSlotPerLecturer && remainderSlots.get() != 0) {
                lecturer.setTotalSlot(lecturer.getTotalSlot() + 1);
                remainderSlots.set(remainderSlots.get() - 1);
            }
            lecturerToArrange.add(lecturerToArrangeDto);
        });

        schedulerWithSpecialSubject.forEach(scheduler -> {
            String[] subjectCodes = scheduler.getSubjectCode().split(",");
            String subjectMatch = Arrays.stream(subjectCodes)
                    .filter(SUBJECT_CODE_SPECIAL::contains)
                    .findFirst()
                    .orElse("");

            for (LecturerToArrangeDto lecturer : lecturerToArrange) {
                if (lecturer.getCountSlotArrange() == 0) {
                    continue;
                }
                if ((Arrays.stream(lecturer.getLecturer().getExamSubject().split(",")).anyMatch(subject -> subject.contains(subjectMatch)) &&
                        isNotHaveSlotExamOfLecturer(semesterId, lecturer.getLecturer().getId(), scheduler))) {
                    scheduler.setLecturerId(lecturer.getLecturer().getId());
                    schedulersToSave.add(scheduler);
                    lecturer.setCountSlotArrange(lecturer.getCountSlotArrange() - 1);
                    break;
                }
            }
        });
        schedulerWithNormalSubject.forEach(scheduler -> {
            for (LecturerToArrangeDto lecturer : lecturerToArrange) {
                if (lecturer.getCountSlotArrange() == 0) {
                    continue;
                }
                if (isNotHaveSlotExamOfLecturer(semesterId, lecturer.getLecturer().getId(), scheduler)) {
                    scheduler.setLecturerId(lecturer.getLecturer().getId());
                    schedulersToSave.add(scheduler);
                    lecturer.setCountSlotArrange(lecturer.getCountSlotArrange() - 1);
                    break;
                }
            }
        });
        schedulerRepository.saveAll(schedulersToSave);
        calculateWorking(semesterId);
    }

    public boolean isNotHaveSlotExamOfLecturer(int semesterId, int lecturerId, Scheduler scheduler) {
        List<Scheduler> schedulers = schedulerRepository.findBySemesterIdAndLecturerIdAvailable(semesterId, lecturerId, scheduler.getStartDate(), scheduler.getEndDate());
        return schedulers.isEmpty();
    }

    @Override
    public void updateLecturer(int schedulerId, int lecturerId) throws Exception {
        Scheduler scheduler = schedulerRepository.findById(schedulerId).get();
        String[] subjectCodes = scheduler.getSubjectCode().split(",");
        String subjectMatch = Arrays.stream(subjectCodes)
                .filter(SUBJECT_CODE_SPECIAL::contains)
                .findFirst()
                .orElse("");
        LocalDateTime startDate = scheduler.getStartDate();
        LocalDateTime endDate = scheduler.getEndDate();
        if (Arrays.stream(lecturerRepository.findById(lecturerId).get().getExamSubject().split(",")).noneMatch(subject -> subject.contains(subjectMatch))) {
            throw new Exception("This is room have special subject (" + subjectMatch + "). Need to choose a teacher who can proctor this subject ");
        }
        if (!schedulerRepository.findAllBySemesterIdAndStartDateAndEndDateAndLectureId(
                scheduler.getSemesterId(), startDate, endDate, scheduler.getId(), lecturerId).isEmpty()) {
            throw new Exception("It is not possible to change teachers because that teacher has conflict time");
        }
        if (scheduler.getLecturerId() == null || lecturerId != scheduler.getLecturerId()) {
            scheduler.setLecturerId(lecturerId);
            schedulerRepository.save(scheduler);
        }
        calculateWorking(scheduler.getSemesterId());
    }

    @Override
    public List<Integer> getIdsByTimeRange(Integer semesterId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Integer> result;
        if (startDate.isBlank() || endDate.isBlank()) {
            throw new InvalidParameterException("Start date and end date cannot be empty");
        } else {
            LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
            LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
            result = schedulerRepository.findAllIdBySemesterIdAndStartDateAfterAndEndDateBefore(semesterId, startDateSearch, endDateSearch);
        }

        return result;
    }

    @Override
    public List<ScheduleToSwapDto> getListByTimeRange(Integer id, Integer semesterId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Scheduler> result;
        if (startDate.isBlank() || endDate.isBlank()) {
            throw new InvalidParameterException("Start date and end date cannot be empty");
        }
        LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
        LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
        Scheduler s = schedulerRepository.findById(id).get();
        result = schedulerRepository.findAllBySemesterIdAndStartDateAfterAndEndDateBeforeAndIdNot(semesterId, startDateSearch, endDateSearch, id, s.getLecturerId());

        return result.stream()
                .map(scheduler -> {
                    String lecturerEmail = null;
                    String lecturerCode = null;
                    if (scheduler.getLecturerId() != null && lecturerRepository.findById(scheduler.getLecturerId()).isPresent()) {
                        Lecturer l = lecturerRepository.findById(scheduler.getLecturerId()).get();
                        lecturerEmail = l.getEmail();
                        lecturerCode = l.getCodeName();
                    }
                    return ScheduleToSwapDto.builder()
                            .id(scheduler.getId())
                            .semesterId(scheduler.getSemesterId())
                            .semesterName(semesterRepository.findById(scheduler.getSemesterId()).get().getName())
                            .lecturerId(scheduler.getLecturerId())
                            .lecturerEmail(lecturerEmail)
                            .lecturerCodeName(lecturerCode)
                            .startDate(scheduler.getStartDate())
                            .endDate(scheduler.getEndDate())
                            .roomId(scheduler.getRoomId())
                            .roomName(roomRepository.findById(scheduler.getRoomId()).get().getName())
                            .subjectCode(scheduler.getSubjectCode())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void decreaseNumberOfRoomsPerSlot(Integer semesterId, String startDate, String endDate, String type, Integer numberDecrease, String subject) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (startDate.isBlank() || endDate.isBlank()) {
            throw new InvalidParameterException("Start time and end time cannot be empty");
        }
        if(numberDecrease == 0){
            throw new Exception("Number decrease invalid");
        }
        LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
        LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
        List<Integer> labs = roomRepository.findAllBySemesterIdAndQuantityStudentGreaterThanAndNameContainingIgnoreCase(semesterId, 1, "Lab")
                .stream()
                .map(Room::getId)
                .toList();
        List<Scheduler> schedulers;
        schedulers = schedulerRepository.findAllBySemesterIdAndStartDateAndEndDateAndTypeAndRoomIdNotIn(semesterId, startDateSearch, endDateSearch, type, labs);
        Map<Integer, Integer> roomStudentCount = new HashMap<>();
        List<String> allStudentIds = new ArrayList<>();
        if (!subject.isBlank()) {
            for (Scheduler scheduler : schedulers) {
                if (scheduler.getSubjectCode().equals(subject)) {
                    int numberOfStudents = scheduler.getStudentId().split(",").length;
                    roomStudentCount.put(scheduler.getId(), numberOfStudents);
                }
            }
        } else {
            for (Scheduler scheduler : schedulers) {
                String subjectCodes = scheduler.getSubjectCode();
                if (subjectCodes.split(",").length > 1 || subjectRepository.findBySemesterIdAndSubjectCode(semesterId, subjectCodes).getDontMix() == null) {
                    int numberOfStudents = scheduler.getStudentId().split(",").length;
                    roomStudentCount.put(scheduler.getId(), numberOfStudents);
                }
            }
        }
        if (roomStudentCount.isEmpty() || roomStudentCount.size() < numberDecrease) {
            throw new Exception("No valid data to decrease room");
        }
        List<Integer> schedulesWithSmallestCount = roomStudentCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue())
                .limit(numberDecrease)
                .map(Map.Entry::getKey)
                .toList();

        for (Integer scheduleId : schedulesWithSmallestCount) {
            Scheduler scheduler = schedulerRepository.findById(scheduleId).get();
            allStudentIds.addAll(List.of(scheduler.getStudentId().split(",")));
        }
        List<Scheduler> schedulersToAddStudent = schedulerRepository.findAllBySemesterIdAndStartDateAndEndDateAndIdNotIn(semesterId, startDateSearch, endDateSearch, schedulesWithSmallestCount);
        schedulersToAddStudent.sort(Comparator.comparingInt(scheduler -> scheduler.getStudentId().split(",").length));
        Iterator<String> studentIterator = allStudentIds.iterator();
        while (studentIterator.hasNext()) {
            for (Scheduler scheduler : schedulersToAddStudent) {
                if (!studentIterator.hasNext()) {
                    break; // No more students to add
                }
                String studentIdToAdd = studentIterator.next();
                String updatedStudentId = scheduler.getStudentId() + "," + studentIdToAdd;
                StudentSubject studentSubject = studentSubjectRepository.findById(Integer.valueOf(studentIdToAdd)).get();
                if (!scheduler.getSubjectCode().contains(studentSubject.getSubjectCode())) {
                    String subjectCodes = studentSubject.getSubjectCode() + "," + studentSubject.getSubjectCode();
                    scheduler.setSubjectCode(subjectCodes);
                }
                scheduler.setStudentId(updatedStudentId);
                schedulerRepository.save(scheduler);
            }
        }
        schedulerRepository.deleteAllById(schedulesWithSmallestCount);
        calculateWorking(semesterId);
    }

    @Override
    public void increaseNumberOfRoomsPerSlot(Integer semesterId, String startDate, String endDate, String type, Integer numberIncrease, String subject) throws Exception {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (startDate.isBlank() || endDate.isBlank()) {
            throw new InvalidParameterException("Start date and end date cannot be empty");
        }
        LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
        LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
        List<Integer> labs = roomRepository.findAllBySemesterIdAndQuantityStudentGreaterThanAndNameContainingIgnoreCase(semesterId, 1, "Lab")
                .stream()
                .map(Room::getId)
                .toList();
        List<Scheduler> schedulers = schedulerRepository.findAllBySemesterIdAndStartDateAndEndDateAndTypeAndRoomIdNotIn(semesterId, startDateSearch, endDateSearch, type, labs);
        List<Integer> roomUsed = schedulerRepository.findAllRoomIdBySemesterIdAndStartDateAndEndDate(semesterId, startDateSearch, endDateSearch);
        List<Scheduler> schedulersDegreeStudent = new ArrayList<>();
        if (!subject.isBlank()) {
            for (Scheduler scheduler : schedulers) {
                if (scheduler.getSubjectCode().equals(subject)) {
                    schedulersDegreeStudent.add(scheduler);
                }
            }
        } else {
            for (Scheduler scheduler : schedulers) {
                if (subjectRepository.findBySemesterIdAndSubjectCode(semesterId, scheduler.getSubjectCode()) == null ||
                        subjectRepository.findBySemesterIdAndSubjectCode(semesterId, scheduler.getSubjectCode()).getDontMix() == null) {
                    schedulersDegreeStudent.add(scheduler);
                }
            }
        }
        List<Integer> roomIdsAvailable = roomRepository.findAllBySemesterIdAndIdNotIn(semesterId, roomUsed);
        if (roomIdsAvailable.size() < numberIncrease || schedulersDegreeStudent.isEmpty()) {
            throw new Exception("Not enough data to increase");
        }
        Comparator<Scheduler> studentCountComparator = Comparator.comparingInt(scheduler -> scheduler.getStudentId().split(",").length);
        schedulersDegreeStudent.sort(studentCountComparator.reversed());
        int totalRoomSchedule = schedulersDegreeStudent.size();
        int quantityOfNewRoom = totalRoomSchedule - numberIncrease;
        int numberOfStudentToNewRoom = quantityOfNewRoom * numberIncrease;
        Map<Integer, String> roomIdWithStudent = new HashMap<>();
        Map<Integer, String> roomSubject = new HashMap<>();
        int number = 0;
        while (number <= numberOfStudentToNewRoom) {
            // Iterate over available room IDs
            for (int i = 0; i < numberIncrease; i++) {
                String sIds = "";
                String subjectCodes = "";
                int numberStudentInRoom = 0;
                // Iterate over schedulers until the room reaches its capacity or the total number of students assigned is reached
                for (Scheduler schedule : schedulersDegreeStudent) {
                    number++;
                    numberStudentInRoom++;
                    if (numberStudentInRoom >= quantityOfNewRoom || number >= numberOfStudentToNewRoom) {
                        break;
                    }
                    String[] studentIds = schedule.getStudentId().split(",");
                    String lastStudentId = studentIds[studentIds.length - 1];
                    StudentSubject s = studentSubjectRepository.findById(Integer.valueOf(lastStudentId)).get();
                    if (!subjectCodes.contains(s.getSubjectCode())) {
                        subjectCodes += s.getSubjectCode() + ",";
                    }
                    // Remove the last student ID from the scheduler
                    schedule.setStudentId(String.join(",", Arrays.copyOf(studentIds, studentIds.length - 1)));

                    // Add the last student ID to the current room's student list
                    sIds += lastStudentId + ",";
                }
                // Add the student IDs assigned to the current room to the map
                if (roomIdWithStudent.containsKey(roomIdsAvailable.get(i))) {
                    roomIdWithStudent.put(roomIdsAvailable.get(i), roomIdWithStudent.get(roomIdsAvailable.get(i)) + sIds);
                } else {
                    roomIdWithStudent.put(roomIdsAvailable.get(i), sIds);
                }
                if (roomSubject.containsKey(roomIdsAvailable.get(i))) {
                    roomSubject.put(roomIdsAvailable.get(i), roomSubject.get(roomIdsAvailable.get(i)) + subjectCodes);
                } else {
                    roomSubject.put(roomIdsAvailable.get(i), subjectCodes);
                }
            }
        }
        schedulerRepository.saveAll(schedulersDegreeStudent);
        for (Map.Entry<Integer, String> entry : roomIdWithStudent.entrySet()) {
            Scheduler scheduler = new Scheduler();
            scheduler.setRoomId(entry.getKey());
            scheduler.setStartDate(startDateSearch);
            scheduler.setEndDate(endDateSearch);
            scheduler.setStudentId(entry.getValue().substring(0, entry.getValue().length() - 1));
            scheduler.setSemesterId(semesterId);
            scheduler.setType(type);
            scheduler.setSubjectCode(roomSubject.get(entry.getKey()).substring(0, roomSubject.get(entry.getKey()).length() - 1));
            schedulerRepository.save(scheduler);
        }
    }

    @Override
    public void calculateWorking(int semesterId) {
        List<Scheduler> allSchedulers = schedulerRepository.findAllBySemesterId(semesterId);
        Map<Integer, Duration> lecturerMapHour = new HashMap<>();
        Map<Integer, Integer> lecturerMapSlot = new HashMap<>();
        for (Scheduler scheduler : allSchedulers) {
            Duration time = Duration.between(scheduler.getStartDate(), scheduler.getEndDate());
            if (scheduler.getLecturerId() != null) {
                Integer lecturerId = scheduler.getLecturerId();
                if (lecturerMapHour.containsKey(lecturerId)) {
                    lecturerMapHour.put(lecturerId, lecturerMapHour.get(lecturerId).plus(time));
                } else {
                    lecturerMapHour.put(lecturerId, time);
                }
                if (lecturerMapSlot.containsKey(lecturerId)) {
                    lecturerMapSlot.put(lecturerId, lecturerMapSlot.get(lecturerId) + 1);
                } else {
                    lecturerMapSlot.put(lecturerId, 1);
                }
            }
        }
        for (Map.Entry<Integer, Duration> entry : lecturerMapHour.entrySet()) {
            Lecturer lecturer = lecturerRepository.findById(entry.getKey()).get();
            lecturer.setTotalSlotActual(lecturerMapSlot.get(entry.getKey()));
            long hours = entry.getValue().toHours();
            long minutes = entry.getValue().minusHours(hours).toMinutes();
            String totalTime = String.format("%dH%dM", hours, minutes);
            lecturer.setTotalHour(totalTime);
            lecturerRepository.save(lecturer);
        }
    }

    @Override
    public List<String> listDontMix(int semesterId, String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (startDate.isBlank() || endDate.isBlank()) {
            throw new InvalidParameterException("Start time and end time cannot be empty");
        }
        LocalDateTime endDateSearch = LocalDateTime.parse(endDate, formatter);
        LocalDateTime startDateSearch = LocalDateTime.parse(startDate, formatter);
        List<Scheduler> schedulerList = schedulerRepository.findAllBySemesterIdAndStartDateAndEndDate(semesterId, startDateSearch, endDateSearch);
        List<String> listDontMix = new ArrayList<>();
        for (Scheduler scheduler : schedulerList) {
            if (scheduler.getSubjectCode().split(",").length == 1) {
            String subjectCode = scheduler.getSubjectCode();
            if(subjectRepository.findBySemesterIdAndSubjectCode(semesterId, scheduler.getSubjectCode()) != null
                    && subjectRepository.findBySemesterIdAndSubjectCode(semesterId, scheduler.getSubjectCode()).getDontMix() == 1
                    && !listDontMix.contains(subjectCode)) {
                    listDontMix.add(scheduler.getSubjectCode());
            }
            }
        }

        return listDontMix;
    }

    @Override
    public List<String> getTimeSchedule(int semesterId) {
        List<PlanExam> planExamList = planExamRepository.findAllBySemesterId(semesterId);
        List<Date> list = new ArrayList<>();
        for (PlanExam planExam : planExamList) {
            list.add(planExam.getExpectedDate());
        }
        Collections.sort(list);
        Date earliestDate = list.get(0);
        Date latestDate = list.get(list.size() - 1);
        List<String> result = new ArrayList<>();
        result.add(earliestDate.toString());
        result.add(latestDate.toString());
        return result;
    }

    public Page<Scheduler> getListSlot(Integer page, Integer limit, Integer semesterId, String expectedDate, String expectedTime) throws ParseException {
        // Ngày đầu vào
        LocalDate date = LocalDate.parse(expectedDate);

        // Tách thời gian thành thời gian bắt đầu và kết thúc
        String[] timeParts = expectedTime.split("-");
        String startTimeString = timeParts[0].replace("H", ":");
        String endTimeString = timeParts[1].replace("H", ":");

        // Chuyển đổi chuỗi thời gian thành LocalTime
        LocalTime startTime = LocalTime.parse(startTimeString, DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime endTime = LocalTime.parse(endTimeString, DateTimeFormatter.ofPattern("HH:mm"));

        // Kết hợp ngày và thời gian thành LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.of(date, startTime);
        LocalDateTime endDateTime = LocalDateTime.of(date, endTime);

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by("id").descending());
        return schedulerRepository.findBySemesterIdAndStartDateAndEndDate(semesterId, startDateTime, endDateTime, pageable);
    }
}