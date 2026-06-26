package lk.ijse.sams.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.db.DBConnection;
import lk.ijse.sams.model.Course;
import java.sql.*;

public class SubjectController {

    @FXML private TextField txtSubjectName, txtSubjectCode, txtSearch;
    @FXML private ComboBox<Course> cmbCourse;
    @FXML private TableView<ObservableList<String>> tblSubjects;
    @FXML private TableColumn<ObservableList<String>, String> colId, colName, colCode, colCourse;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colCode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colCourse.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));

        loadCourses();
        loadSubjects();

        tblSubjects.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtSubjectName.setText(selected.get(1));
                txtSubjectCode.setText(selected.get(2));
                for (Course c : cmbCourse.getItems()) {
                    if (c.getCourseName().equals(selected.get(3))) {
                        cmbCourse.setValue(c);
                        break;
                    }
                }
            }
        });

        txtSearch.textProperty().addListener((obs, old, val) -> loadSubjectsFiltered(val));
    }

    private void loadCourses() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM courses");
            ObservableList<Course> courses = FXCollections.observableArrayList();
            while (rs.next()) {
                courses.add(new Course(rs.getInt("course_id"),
                        rs.getString("course_name"), rs.getString("course_code")));
            }
            cmbCourse.setItems(courses);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void loadSubjects() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "SELECT s.subject_id, s.subject_name, s.subject_code, c.course_name " +
                         "FROM subjects s JOIN courses c ON s.course_id = c.course_id";
            ResultSet rs = con.createStatement().executeQuery(sql);
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
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void loadSubjectsFiltered(String keyword) {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "SELECT s.subject_id, s.subject_name, s.subject_code, c.course_name " +
                         "FROM subjects s JOIN courses c ON s.course_id = c.course_id " +
                         "WHERE s.subject_name LIKE ? OR s.subject_code LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
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
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleAdd() {
        if (txtSubjectName.getText().isEmpty() || txtSubjectCode.getText().isEmpty() || cmbCourse.getValue() == null) {
            showAlert("Error", "All fields are required!"); return;
        }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO subjects (subject_name, subject_code, course_id) VALUES (?,?,?)");
            pst.setString(1, txtSubjectName.getText());
            pst.setString(2, txtSubjectCode.getText());
            pst.setInt(3, cmbCourse.getValue().getCourseId());
            pst.executeUpdate();
            showAlert("Success", "Subject added!");
            loadSubjects(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleUpdate() {
        ObservableList<String> selected = tblSubjects.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a subject!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "UPDATE subjects SET subject_name=?, subject_code=?, course_id=? WHERE subject_id=?");
            pst.setString(1, txtSubjectName.getText());
            pst.setString(2, txtSubjectCode.getText());
            pst.setInt(3, cmbCourse.getValue().getCourseId());
            pst.setInt(4, Integer.parseInt(selected.get(0)));
            pst.executeUpdate();
            showAlert("Success", "Subject updated!");
            loadSubjects(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleDelete() {
        ObservableList<String> selected = tblSubjects.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a subject!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "DELETE FROM subjects WHERE subject_id=?");
            pst.setInt(1, Integer.parseInt(selected.get(0)));
            pst.executeUpdate();
            showAlert("Success", "Subject deleted!");
            loadSubjects(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleClear() {
        txtSubjectName.clear();
        txtSubjectCode.clear();
        txtSearch.clear();
        cmbCourse.setValue(null);
        tblSubjects.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
