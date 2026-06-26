package lk.ijse.sams.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import lk.ijse.sams.db.DBConnection;
import java.sql.*;

public class AttendanceController {

    @FXML private ComboBox<String> cmbSession;
    @FXML private TableView<ObservableList<String>> tblAttendance;
    @FXML private TableColumn<ObservableList<String>, String> colStudentId, colStudentName, colRegNo, colStatus;

    private ObservableList<Integer> sessionIds = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colStudentId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        colStudentName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        colRegNo.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        colStatus.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(3)));

        tblAttendance.setEditable(true);
        colStatus.setCellFactory(ComboBoxTableCell.forTableColumn(
            FXCollections.observableArrayList("PRESENT", "ABSENT", "LATE")));
        colStatus.setOnEditCommit(event -> {
            event.getRowValue().set(3, event.getNewValue());
        });

        loadSessions();
    }

    private void loadSessions() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "SELECT cs.session_id, s.subject_name, cs.session_date " +
                         "FROM class_sessions cs JOIN subjects s ON cs.subject_id=s.subject_id " +
                         "ORDER BY cs.session_date DESC";
            ResultSet rs = con.createStatement().executeQuery(sql);
            ObservableList<String> sessions = FXCollections.observableArrayList();
            sessionIds.clear();
            while (rs.next()) {
                sessions.add(rs.getString("subject_name") + " — " + rs.getString("session_date"));
                sessionIds.add(rs.getInt("session_id"));
            }
            cmbSession.setItems(sessions);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleLoadStudents() {
        int idx = cmbSession.getSelectionModel().getSelectedIndex();
        if (idx < 0) { showAlert("Error", "Please select a session!"); return; }
        int sessionId = sessionIds.get(idx);
        try {
            Connection con = DBConnection.getInstance().getConnection();

            PreparedStatement pst = con.prepareStatement(
                "SELECT course_id FROM class_sessions WHERE session_id=?");
            pst.setInt(1, sessionId);
            ResultSet rs = pst.executeQuery();
            int courseId = 0;
            if (rs.next()) courseId = rs.getInt("course_id");

            PreparedStatement pst2 = con.prepareStatement(
                "SELECT * FROM students WHERE course_id=?");
            pst2.setInt(1, courseId);
            ResultSet rs2 = pst2.executeQuery();

            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs2.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs2.getInt("student_id")));
                row.add(rs2.getString("full_name"));
                row.add(rs2.getString("reg_number"));

                PreparedStatement pst3 = con.prepareStatement(
                    "SELECT status FROM attendance WHERE session_id=? AND student_id=?");
                pst3.setInt(1, sessionId);
                pst3.setInt(2, rs2.getInt("student_id"));
                ResultSet rs3 = pst3.executeQuery();
                row.add(rs3.next() ? rs3.getString("status") : "ABSENT");
                data.add(row);
            }
            tblAttendance.setItems(data);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleSave() {
        int idx = cmbSession.getSelectionModel().getSelectedIndex();
        if (idx < 0) { showAlert("Error", "Please select a session!"); return; }
        if (tblAttendance.getItems().isEmpty()) { showAlert("Error", "No students loaded!"); return; }
        int sessionId = sessionIds.get(idx);
        try {
            Connection con = DBConnection.getInstance().getConnection();
            for (ObservableList<String> row : tblAttendance.getItems()) {
                int studentId = Integer.parseInt(row.get(0));
                String status = row.get(3);

                PreparedStatement check = con.prepareStatement(
                    "SELECT * FROM attendance WHERE session_id=? AND student_id=?");
                check.setInt(1, sessionId);
                check.setInt(2, studentId);
                ResultSet rs = check.executeQuery();

                if (rs.next()) {
                    PreparedStatement upd = con.prepareStatement(
                        "UPDATE attendance SET status=? WHERE session_id=? AND student_id=?");
                    upd.setString(1, status);
                    upd.setInt(2, sessionId);
                    upd.setInt(3, studentId);
                    upd.executeUpdate();
                } else {
                    PreparedStatement ins = con.prepareStatement(
                        "INSERT INTO attendance (session_id, student_id, status) VALUES (?,?,?)");
                    ins.setInt(1, sessionId);
                    ins.setInt(2, studentId);
                    ins.setString(3, status);
                    ins.executeUpdate();
                }
            }
            showAlert("Success", "Attendance saved successfully!");
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
