package lk.ijse.sams.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;

public class DashboardController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblTitle;
    @FXML private Button btnStudents, btnCourses, btnSubjects, btnLecturers, btnSchedule, btnAttendance, btnReports;

    private static final String ACTIVE_STYLE = "-fx-background-color: white; -fx-text-fill: #8e44ad; -fx-padding: 10 15; -fx-alignment: CENTER-LEFT; -fx-cursor: hand; -fx-background-radius: 6;";
    private static final String NORMAL_STYLE = "-fx-background-color: transparent; -fx-text-fill: #e8daef; -fx-padding: 10 15; -fx-alignment: CENTER-LEFT; -fx-cursor: hand; -fx-background-radius: 6;";

    private List<Button> menuButtons;

    @FXML
    public void initialize() {
        menuButtons = Arrays.asList(btnStudents, btnCourses, btnSubjects, btnLecturers, btnSchedule, btnAttendance, btnReports);
        loadView("StudentView", "Students", btnStudents);
    }

    @FXML
    private void loadStudents() { loadView("StudentView", "Students", btnStudents); }

    @FXML
    private void loadCourses() { loadView("CourseView", "Courses", btnCourses); }

    @FXML
    private void loadSubjects() { loadView("SubjectView", "Subjects", btnSubjects); }

    @FXML
    private void loadLecturers() { loadView("LecturerView", "Lecturers", btnLecturers); }

    @FXML
    private void loadSchedule() { loadView("ScheduleView", "Class Schedule", btnSchedule); }

    @FXML
    private void loadAttendance() { loadView("AttendanceView", "Attendance", btnAttendance); }

    @FXML
    private void loadReports() { loadView("ReportView", "Attendance Reports", btnReports); }

    private void loadView(String fxmlName, String title, Button activeBtn) {
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
            setActiveButton(activeBtn);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setActiveButton(Button activeBtn) {
        for (Button btn : menuButtons) {
            btn.setStyle(NORMAL_STYLE);
        }
        activeBtn.setStyle(ACTIVE_STYLE);
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
