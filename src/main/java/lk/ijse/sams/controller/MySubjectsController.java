package lk.ijse.sams.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.db.DBConnection;
import lk.ijse.sams.model.SessionManager;
import java.sql.*;

public class MySubjectsController {

    @FXML private TableView<ObservableList<String>> tblSubjects;
    @FXML private TableColumn<ObservableList<String>, String> colId, colSubject, colCode, colCourse;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colSubject.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colCode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colCourse.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        loadSubjects();
    }

    private void loadSubjects() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            int lecturerId = SessionManager.getInstance().getLecturerId();
            String sql = "SELECT s.subject_id, s.subject_name, s.subject_code, c.course_name " +
                         "FROM lecturer_subjects ls " +
                         "JOIN subjects s ON ls.subject_id = s.subject_id " +
                         "JOIN courses c ON s.course_id = c.course_id " +
                         "WHERE ls.lecturer_id = ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, lecturerId);
            ResultSet rs = pst.executeQuery();
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("subject_id")));
                row.add(rs.getString("subject_name"));
                row.add(rs.getString("subject_code"));
                row.add(rs.getString("course_name"));
                data.add(row);
            }
            tblSubjects.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
