package lk.ijse.sams.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.util.Arrays;
import java.util.List;

public class DashboardController {

    @FXML private AnchorPane contentPane;
    @FXML private Label lblTitle;
    @FXML private Button btnStudents, btnCourses, btnSubjects, btnLecturers, btnSchedule, btnAttendance, btnReports, btnProfile;

    private List<Button> menuButtons;

    @FXML
    public void initialize() {
        menuButtons = Arrays.asList(btnStudents, btnCourses, btnSubjects, btnLecturers, btnSchedule, btnAttendance, btnReports, btnProfile);
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
    @FXML
    private void loadProfile() { loadView("ProfileView", "My Profile", btnProfile); }

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
            btn.setStyle("-fx-background-color: transparent; -fx-padding: 10 15; -fx-alignment: CENTER-LEFT; -fx-cursor: hand; -fx-background-radius: 6;");
            updateLabelColor(btn, "#e8daef");
        }
        activeBtn.setStyle("-fx-background-color: #e8d5f5; -fx-padding: 10 15; -fx-alignment: CENTER-LEFT; -fx-cursor: hand; -fx-background-radius: 6;");
        updateLabelColor(activeBtn, "#6c3483");
    }

    private void updateLabelColor(Button btn, String color) {
        if (btn.getGraphic() instanceof HBox) {
            HBox hbox = (HBox) btn.getGraphic();
            hbox.getChildren().forEach(node -> {
                if (node instanceof Label) {
                    ((Label) node).setStyle("-fx-text-fill: " + color + "; -fx-font-size: 13;");
                }
            });
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
