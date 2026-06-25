package lk.ijse.sams.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblTitle;

    @FXML
    public void initialize() {
        loadView("StudentView", "Students");
    }

    @FXML
    private void loadStudents() {
        loadView("StudentView", "Students");
    }

    @FXML
    private void loadCourses() {
        loadView("CourseView", "Courses");
    }

    @FXML
    private void loadLecturers() {
        loadView("LecturerView", "Lecturers");
    }

    @FXML
    private void loadSchedule() {
        loadView("ScheduleView", "Class Schedule");
    }

    @FXML
    private void loadAttendance() {
        loadView("AttendanceView", "Attendance");
    }

    @FXML
    private void loadReports() {
        loadView("ReportView", "Attendance Reports");
    }

    private void loadView(String fxmlName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/lk/ijse/sams/" + fxmlName + ".fxml"));
            Parent root = loader.load();
            contentPane.getChildren().clear();
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            contentPane.getChildren().add(root);
            lblTitle.setText(title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent root = FXMLLoader.load(
                getClass().getResource("/lk/ijse/sams/LoginView.fxml"));
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SAMS - Login");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
