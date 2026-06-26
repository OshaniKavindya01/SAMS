package lk.ijse.sams.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.db.DBConnection;
import lk.ijse.sams.model.SessionManager;
import java.sql.*;

public class MyScheduleController {

    @FXML private TableView<ObservableList<String>> tblSchedule;
    @FXML private TableColumn<ObservableList<String>, String> colId, colSubject, colCourse, colDate, colStart, colEnd;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colSubject.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colCourse.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colDate.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colStart.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));
        colEnd.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(5)));
        loadSchedule();
    }

    private void loadSchedule() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            int lecturerId = SessionManager.getInstance().getLecturerId();
            String sql = "SELECT cs.session_id, s.subject_name, c.course_name, " +
                         "cs.session_date, cs.start_time, cs.end_time " +
                         "FROM class_sessions cs " +
                         "JOIN subjects s ON cs.subject_id = s.subject_id " +
                         "JOIN courses c ON cs.course_id = c.course_id " +
                         "WHERE cs.lecturer_id = ? ORDER BY cs.session_date";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setInt(1, lecturerId);
            ResultSet rs = pst.executeQuery();
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("session_id")));
                row.add(rs.getString("subject_name"));
                row.add(rs.getString("course_name"));
                row.add(rs.getString("session_date"));
                row.add(rs.getString("start_time"));
                row.add(rs.getString("end_time"));
                data.add(row);
            }
            tblSchedule.setItems(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
