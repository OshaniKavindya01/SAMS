package lk.ijse.sams.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.db.DBConnection;
import lk.ijse.sams.model.Course;
import java.sql.*;

public class CourseController {

    @FXML private TextField txtCourseName, txtCourseCode, txtSearch;
    @FXML private TableView<ObservableList<String>> tblCourses;
    @FXML private TableColumn<ObservableList<String>, String> colId, colName, colCode;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        colCode.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));

        loadCourses();

        tblCourses.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtCourseName.setText(selected.get(1));
                txtCourseCode.setText(selected.get(2));
            }
        });

        txtSearch.textProperty().addListener((obs, old, val) -> loadCoursesFiltered(val));
    }

    private void loadCourses() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM courses");
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("course_id")));
                row.add(rs.getString("course_name"));
                row.add(rs.getString("course_code"));
                data.add(row);
            }
            tblCourses.setItems(data);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void loadCoursesFiltered(String keyword) {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "SELECT * FROM courses WHERE course_name LIKE ? OR course_code LIKE ?");
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("course_id")));
                row.add(rs.getString("course_name"));
                row.add(rs.getString("course_code"));
                data.add(row);
            }
            tblCourses.setItems(data);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (txtCourseName.getText().isEmpty() || txtCourseCode.getText().isEmpty()) {
            showAlert("Error", "All fields are required!"); return;
        }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO courses (course_name, course_code) VALUES (?,?)");
            pst.setString(1, txtCourseName.getText());
            pst.setString(2, txtCourseCode.getText());
            pst.executeUpdate();
            showAlert("Success", "Course added!");
            loadCourses(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleUpdate() {
        ObservableList<String> selected = tblCourses.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a course!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "UPDATE courses SET course_name=?, course_code=? WHERE course_id=?");
            pst.setString(1, txtCourseName.getText());
            pst.setString(2, txtCourseCode.getText());
            pst.setInt(3, Integer.parseInt(selected.get(0)));
            pst.executeUpdate();
            showAlert("Success", "Course updated!");
            loadCourses(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleDelete() {
        ObservableList<String> selected = tblCourses.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a course!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "DELETE FROM courses WHERE course_id=?");
            pst.setInt(1, Integer.parseInt(selected.get(0)));
            pst.executeUpdate();
            showAlert("Success", "Course deleted!");
            loadCourses(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleClear() {
        txtCourseName.clear();
        txtCourseCode.clear();
        txtSearch.clear();
        tblCourses.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
