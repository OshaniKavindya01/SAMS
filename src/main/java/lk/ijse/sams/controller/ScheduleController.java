package lk.ijse.sams.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.sams.db.DBConnection;
import lk.ijse.sams.model.Course;
import lk.ijse.sams.model.Lecturer;
import lk.ijse.sams.model.Subject;
import java.sql.*;
import java.time.LocalDate;

public class ScheduleController {

    @FXML private ComboBox<Course> cmbCourse;
    @FXML private ComboBox<Subject> cmbSubject;
    @FXML private ComboBox<Lecturer> cmbLecturer;
    @FXML private DatePicker dpDate;
    @FXML private TextField txtStartTime, txtEndTime;
    @FXML private TableView<ObservableList<String>> tblSchedule;
    @FXML private TableColumn<ObservableList<String>, String> colId, colCourse, colSubject, colLecturer, colDate, colStart, colEnd;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        colCourse.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        colSubject.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        colLecturer.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(3)));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(4)));
        colStart.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(5)));
        colEnd.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(6)));

        loadCourses();
        loadLecturers();
        loadSchedule();

        cmbCourse.valueProperty().addListener((obs, old, val) -> {
            if (val != null) loadSubjects(val.getCourseId());
        });
    }

    private void loadCourses() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM courses");
            ObservableList<Course> list = FXCollections.observableArrayList();
            while (rs.next()) list.add(new Course(rs.getInt("course_id"), rs.getString("course_name"), rs.getString("course_code")));
            cmbCourse.setItems(list);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void loadSubjects(int courseId) {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement("SELECT * FROM subjects WHERE course_id=?");
            pst.setInt(1, courseId);
            ResultSet rs = pst.executeQuery();
            ObservableList<Subject> list = FXCollections.observableArrayList();
            while (rs.next()) list.add(new Subject(rs.getInt("subject_id"), rs.getString("subject_name"), rs.getString("subject_code"), courseId));
            cmbSubject.setItems(list);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void loadLecturers() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM lecturers");
            ObservableList<Lecturer> list = FXCollections.observableArrayList();
            while (rs.next()) list.add(new Lecturer(rs.getInt("lecturer_id"), rs.getString("full_name"), rs.getString("email"), rs.getString("phone")));
            cmbLecturer.setItems(list);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void loadSchedule() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "SELECT cs.session_id, c.course_name, s.subject_name, l.full_name, cs.session_date, cs.start_time, cs.end_time " +
                         "FROM class_sessions cs JOIN courses c ON cs.course_id=c.course_id " +
                         "JOIN subjects s ON cs.subject_id=s.subject_id " +
                         "JOIN lecturers l ON cs.lecturer_id=l.lecturer_id";
            ResultSet rs = con.createStatement().executeQuery(sql);
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("session_id")));
                row.add(rs.getString("course_name"));
                row.add(rs.getString("subject_name"));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("session_date"));
                row.add(rs.getString("start_time"));
                row.add(rs.getString("end_time"));
                data.add(row);
            }
            tblSchedule.setItems(data);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleAdd() {
        if (cmbCourse.getValue() == null || cmbSubject.getValue() == null ||
            cmbLecturer.getValue() == null || dpDate.getValue() == null) {
            showAlert("Error", "Please fill all fields!"); return;
        }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO class_sessions (course_id, subject_id, lecturer_id, session_date, start_time, end_time) VALUES (?,?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, cmbCourse.getValue().getCourseId());
            pst.setInt(2, cmbSubject.getValue().getSubjectId());
            pst.setInt(3, cmbLecturer.getValue().getLecturerId());
            pst.setDate(4, Date.valueOf(dpDate.getValue()));
            pst.setString(5, txtStartTime.getText());
            pst.setString(6, txtEndTime.getText());
            pst.executeUpdate();
            showAlert("Success", "Session scheduled!");
            loadSchedule(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleDelete() {
        ObservableList<String> selected = tblSchedule.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a session!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement("DELETE FROM class_sessions WHERE session_id=?");
            pst.setInt(1, Integer.parseInt(selected.get(0)));
            pst.executeUpdate();
            showAlert("Success", "Session deleted!");
            loadSchedule();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleClear() {
        cmbCourse.setValue(null);
        cmbSubject.setValue(null);
        cmbLecturer.setValue(null);
        dpDate.setValue(null);
        txtStartTime.clear();
        txtEndTime.clear();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
