# SAMS - Student Attendance Management System

A Java-based desktop application for managing student attendance in educational institutions, developed as part of the Object-Oriented Programming coursework at IJSE.

## Project Overview

SAMS enables administrative staff and lecturers to manage courses, students, class schedules, and attendance records. The system provides role-based access control and attendance reporting with filtering capabilities.

## Technologies Used

| Technology | Version |
|---|---|
| Java | 21 |
| JavaFX | 21 |
| MySQL | 8.0 |
| JDBC | MySQL Connector 8.3.0 |
| Maven | 3.11.0 |
| Ikonli Icons | 12.3.1 |
| NetBeans IDE | 29 |

## Architecture

The application follows a Layered (N-Tier) Architecture:

- Presentation Layer - JavaFX FXML views and controllers
- Service Layer - Business logic in controller classes
- Data Access Layer - JDBC-based database communication
- Database Layer - MySQL relational database

## Setup Instructions

### Prerequisites
- Java JDK 21 or higher
- MySQL Server 8.0
- Maven 3.6+
- NetBeans IDE 29

### Database Setup
1. Open MySQL Workbench
2. Run the sams_db.sql file in the root of this project
3. This creates the database, all tables, and inserts sample data

### Application Setup
1. Clone the repository:
   git clone https://github.com/OshaniKavindya01/SAMS.git
2. Open the project in NetBeans
3. Update database credentials in:
   src/main/java/lk/ijse/sams/db/DBConnection.java
4. Right-click the project and select Clean and Build
5. Click Run

## Login Credentials

| Role | Username | Password |
|---|---|---|
| Admin | admin | admin123 |
| Lecturer | nimal.lec | nimal123 |
| Lecturer | dilani.lec | dilani123 |
| Lecturer | kasun.lec | kasun123 |

## Features

### Admin Portal
- Course management - add, update, delete courses
- Subject management - manage subjects per course
- Student management - register and manage students
- Lecturer management - manage lecturer profiles
- Class scheduling - create sessions with course, subject, lecturer, date and time
- Attendance marking - mark Present, Absent or Late per session
- Attendance reports - filter by student, subject and date range

### Lecturer Portal
- View assigned subjects only
- View personal class schedule only
- Mark and update attendance for their sessions only

## Database Schema

The system uses 8 relational tables:
users, courses, subjects, students, lecturers, lecturer_subjects, class_sessions, attendance

## Author

Oshani Kavindya
IJSE - Object-Oriented Programming Coursework 2026
