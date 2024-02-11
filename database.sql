CREATE TABLE `Account` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `active` int(11) DEFAULT null,
  `email` varchar(64) NOT NULL,
  `password` varchar(256) NOT NULL,
  `role` varchar(64) NOT NULL,
  `since` int(11) DEFAULT null,
  `username` varchar(64) NOT NULL
);

CREATE TABLE `Token` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `expired` int(5),
  `revoked` int(5),
  `token` varchar(128),
  `token_type` varchar(64),
  `account_id` int(11)
);

CREATE TABLE `Room` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `type` varchar(32),
  `quantity_student` int(3)
);

CREATE TABLE `Semester` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(64) NOT NULL,
  `code` varchar(64) NOT NULL,
  `from_date` datetime,
  `to_date` datetime,
  `creator_id` int(11) NOT NULL
);

CREATE TABLE `Student` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `SubjectCode` varchar(128),
  `RollNumber` varchar(128),
  `MemberCode` varchar(128),
  `FullName` varchar(128),
  `CMTND` varchar(64),
  `semester_id` int(11) NOT NULL,
  `black_list` longtext
);

CREATE TABLE `Subject` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int(11) NOT NULL,
  `Subject_Code` varchar(255),
  `Old_Subject_Code` varchar(255),
  `Short_Name` varchar(255),
  `Subject_Name` varchar(255),
  `No_Lab` int,
  `Dont_mix` int,
  `Replaced_by` varchar(255)
);

CREATE TABLE `Scheduler` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `semester_id` int(11) NOT NULL,
  `slot_id` int(11),
  `room_id` int(11) NOT NULL,
  `exam_code_id` varchar(255),
  `student_id` varchar(255),
  `start_date` date
);

CREATE TABLE `Exam_Code` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `Subject_id` varchar(255),
  `type` varchar(255),
  `Semester_id` int,
  `Slot_id` int
);

CREATE TABLE `Slot` (
  `id` int(11) PRIMARY KEY AUTO_INCREMENT,
  `name` varchar(64),
  `start_time` varchar(64),
  `end_time` varchar(64)
);
