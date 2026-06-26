package lk.ijse.sams.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.db.DBConnection;
import lk.ijse.sams.model.Student;
import lk.ijse.sams.model.Subject;
import java.sql.*;
import java.time.LocalDate;

public class ReportController {

    @FXML private ComboBox<Student> cmbStudent;
    @FXML private ComboBox<Subject> cmbSubject;
    @FXML private DatePicker dpFrom, dpTo;
    @FXML private TableView<ObservableList<String>> tblReport;
    @FXML private TableColumn<ObservableList<String>, String> colStudent, colSubject, colDate, colStatus;

    @FXML
    public void initialize() {
        colStudent.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        colSubject.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        colDate.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(3)));
        loadStudents();
        loadSubjects();
    }

    private void loadStudents() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM students");
            ObservableList<Student> list = FXCollections.observableArrayList();
            while (rs.next()) list.add(new Student(rs.getInt("student_id"), rs.getString("full_name"),
                    rs.getString("reg_number"), rs.getInt("course_id"), rs.getString("email"), rs.getString("phone")));
            cmbStudent.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void loadSubjects() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM subjects");
            ObservableList<Subject> list = FXCollections.observableArrayList();
            while (rs.next()) list.add(new Subject(rs.getInt("subject_id"), rs.getString("subject_name"),
                    rs.getString("subject_code"), rs.getInt("course_id")));
            cmbSubject.setItems(list);
        } catch (Exception e) { e.printStackTrace(); }
    }

    @FXML
    private void handleSearch() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            StringBuilder sql = new StringBuilder(
                "SELECT st.full_name, s.subject_name, cs.session_date, a.status " +
                "FROM attendance a " +
                "JOIN students st ON a.student_id=st.student_id " +
                "JOIN class_sessions cs ON a.session_id=cs.session_id " +
                "JOIN subjects s ON cs.subject_id=s.subject_id WHERE 1=1");

            if (cmbStudent.getValue() != null)
                sql.append(" AND a.student_id=").append(cmbStudent.getValue().getStudentId());
            if (cmbSubject.getValue() != null)
                sql.append(" AND s.subject_id=").append(cmbSubject.getValue().getSubjectId());
            if (dpFrom.getValue() != null)
                sql.append(" AND cs.session_date >= '").append(dpFrom.getValue()).append("'");
            if (dpTo.getValue() != null)
                sql.append(" AND cs.session_date <= '").append(dpTo.getValue()).append("'");

            ResultSet rs = con.createStatement().executeQuery(sql.toString());
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(rs.getString("full_name"));
                row.add(rs.getString("subject_name"));
                row.add(rs.getString("session_date"));
                row.add(rs.getString("status"));
                data.add(row);
            }
            tblReport.setItems(data);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleReset() {
        cmbStudent.setValue(null);
        cmbSubject.setValue(null);
        dpFrom.setValue(null);
        dpTo.setValue(null);
        tblReport.getItems().clear();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
