CREATE TABLE `Account` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `active` int(11) DEFAULT null,
  `email` varchar(64) UNIQUE NOT NULL,
  `password` varchar(256) NOT NULL,
  `role` enum('ADMIN','MANAGER','USER') DEFAULT NULL,
  `since` int(11) DEFAULT null,
  `username` varchar(64) UNIQUE NOT NULL
);

CREATE TABLE `Token` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `expired` int(5),
  `revoked` int(5),
  `token` varchar(1024),
  `token_type` varchar(64),
  `account_id` int(11)
);

CREATE TABLE `Room` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `type` varchar(32),
  `quantity_student` int(3),
  `semester_id` int(11)
);

CREATE TABLE `Semester` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(64) UNIQUE NOT NULL,
  `code` varchar(64) NOT NULL,
  `from_date` date,
  `to_date` date,
  `creator_id` int(11) NOT NULL
);

CREATE TABLE `Student` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `roll_number` varchar(128) UNIQUE NOT NULL,
  `member_code` varchar(128) UNIQUE NOT NULL,
  `full_name` varchar(128),
  `CMTND` varchar(64) UNIQUE NULL
);

CREATE TABLE `Subject` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int(11) NOT NULL,
  `subject_code` longtext,
  `old_subject_code` varchar(255),
  `short_name` varchar(255),
  `subject_name` varchar(255),
  `no_lab` int DEFAULT '0',
  `dont_mix` int DEFAULT '0',
  `replaced_by` varchar(255)
);

CREATE TABLE `StudentSubject` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int,
  `roll_number` varchar(128),
  `subject_code` varchar(128),
  `group_name` varchar(128),
  `black_list` int
);

CREATE TABLE `Scheduler` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int(11) NOT NULL,
  `slot_id` int(11),
  `room_id` int(11) NOT NULL,
  `lecturer_id` int(11),
  `subject_code` longtext,
  `exam_code_id` longtext,
  `student_id` longtext,
  `start_date` datetime,
  `end_date` datetime,
  `type` varchar(255)
);

CREATE TABLE `ExamCode` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int,
  `subject_code` varchar(255),
  `type` varchar(255),
  `exam` varchar(64),
  `exam_code` varchar(64),
  `slot_id` int,
  INDEX semester (semester_id)
);

CREATE TABLE `Slot` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(64),
  `start_time` datetime,
  `end_time` datetime
);

CREATE TABLE `PlanExam` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int,
  `expected_date` datetime,
  `expected_time` varchar(64),
  `type_exam` varchar(64),
  `total_student` int DEFAULT '0' NULL,
  `subject_code` varchar(64)
);

CREATE TABLE `Lecturer` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int,
  `code_name` varchar(64),
  `email` varchar(64),
  `exam_subject` varchar(124),
  `total_slot` int,
  `total_slot_actual` int,
  `total_hour` varchar(64)

);

CREATE TABLE `Status` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int unique ,
  `plan_exam` int,
  `subject` int,
  `room` int,
  `lecturer` int,
  `student` int
);

CREATE TABLE `ActionLog` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int,
  `user_name` varchar(64),
  `log_table` varchar(128),
  `log_action` varchar(128),
  `log_content` longtext,
  `since` datetime
);


CREATE INDEX semester_index ON Subject(semester_id);
CREATE INDEX semester_index1 ON StudentSubject(semester_id);
create index student_roll_number on Student(roll_number);

INSERT INTO Account (active, email, password, role, username)
VALUES (1, 'Admin@gmail.com', '$2a$10$QdprJun5c1PkBUDQKwGWq.CL8XJLC61MvUgmV5Tpyev/F9pfqjNgq', 'ADMIN', 'ADMIN');
-- CREATE INDEX semester_index2 ON ExamCode(semester_id);