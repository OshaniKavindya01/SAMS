-- ============================================
-- SAMS - Student Attendance Management System
-- Database Schema and Sample Data
-- ============================================

CREATE DATABASE IF NOT EXISTS sams_db;
USE sams_db;

CREATE TABLE users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(50) NOT NULL,
    role ENUM('ADMIN','LECTURER') NOT NULL
);

CREATE TABLE courses (
    course_id INT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(100) NOT NULL,
    course_code VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE subjects (
    subject_id INT AUTO_INCREMENT PRIMARY KEY,
    subject_name VARCHAR(100) NOT NULL,
    subject_code VARCHAR(20) NOT NULL UNIQUE,
    course_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    reg_number VARCHAR(50) NOT NULL UNIQUE,
    course_id INT NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    FOREIGN KEY (course_id) REFERENCES courses(course_id)
);

CREATE TABLE lecturers (
    lecturer_id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20)
);

CREATE TABLE lecturer_subjects (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    lecturer_id INT NOT NULL,
    subject_id INT NOT NULL,
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id)
);

CREATE TABLE class_sessions (
    session_id INT AUTO_INCREMENT PRIMARY KEY,
    course_id INT NOT NULL,
    subject_id INT NOT NULL,
    lecturer_id INT NOT NULL,
    session_date DATE NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    FOREIGN KEY (course_id) REFERENCES courses(course_id),
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id),
    FOREIGN KEY (lecturer_id) REFERENCES lecturers(lecturer_id)
);

CREATE TABLE attendance (
    attendance_id INT AUTO_INCREMENT PRIMARY KEY,
    session_id INT NOT NULL,
    student_id INT NOT NULL,
    status ENUM('PRESENT','ABSENT','LATE') NOT NULL,
    FOREIGN KEY (session_id) REFERENCES class_sessions(session_id),
    FOREIGN KEY (student_id) REFERENCES students(student_id),
    UNIQUE(session_id, student_id)
);

-- Sample Data
INSERT INTO users VALUES (1, 'admin', 'admin123', 'ADMIN');
INSERT INTO users VALUES (2, 'lecturer1', 'lec123', 'LECTURER');

INSERT INTO courses VALUES
(1, 'Diploma in Information Technology', 'DIT001'),
(2, 'Higher Diploma in Software Engineering', 'HDSE001'),
(3, 'Higher Diploma in Network Engineering', 'HDNE001');

INSERT INTO subjects VALUES
(1, 'Programming Fundamentals', 'PF101', 1),
(2, 'Database Systems', 'DB101', 1),
(3, 'Web Development', 'WD101', 1),
(4, 'Object Oriented Programming', 'OOP201', 2),
(5, 'Software Engineering', 'SE201', 2),
(6, 'Data Structures and Algorithms', 'DSA201', 2),
(7, 'Computer Networks', 'CN301', 3),
(8, 'Network Security', 'NS301', 3),
(9, 'Cloud Computing', 'CC301', 3);

INSERT INTO lecturers VALUES
(1, 'Dr. Nimal Jayawardena', 'nimal@ijse.lk', '0112345678'),
(2, 'Ms. Dilani Rathnayake', 'dilani@ijse.lk', '0119876543'),
(3, 'Mr. Kasun Perera', 'kasun@ijse.lk', '0115678901');

INSERT INTO lecturer_subjects VALUES
(1, 1, 1), (2, 1, 2), (3, 1, 3),
(4, 2, 4), (5, 2, 5), (6, 2, 6),
(7, 3, 7), (8, 3, 8), (9, 3, 9);

INSERT INTO students VALUES
(1, 'Amal Perera', 'DIT001-001', 1, 'amal@email.com', '0771234567'),
(2, 'Nimali Silva', 'DIT001-002', 1, 'nimali@email.com', '0772345678'),
(3, 'Kasun Fernando', 'DIT001-003', 1, 'kasun@email.com', '0773456789'),
(4, 'Sachini Dissanayake', 'HDSE001-001', 2, 'sachini@email.com', '0774567890'),
(5, 'Ruwan Bandara', 'HDSE001-002', 2, 'ruwan@email.com', '0775678901'),
(6, 'Tharaka Jayasena', 'HDNE001-001', 3, 'tharaka@email.com', '0776789012'),
(7, 'Chamari Wickramasinghe', 'HDNE001-002', 3, 'chamari@email.com', '0777890123');
