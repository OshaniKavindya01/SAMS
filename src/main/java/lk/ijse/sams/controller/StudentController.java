package lk.ijse.sams.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import lk.ijse.sams.db.DBConnection;
import lk.ijse.sams.model.Course;
import lk.ijse.sams.model.Student;
import java.sql.*;

public class StudentController {

    @FXML private TextField txtFullName, txtRegNumber, txtEmail, txtPhone, txtSearch;
    @FXML private ComboBox<Course> cmbCourse;
    @FXML private TableView<ObservableList<String>> tblStudents;
    @FXML private TableColumn<ObservableList<String>, String> colId, colName, colReg, colCourse, colEmail, colPhone;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        colReg.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        colCourse.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(3)));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(4)));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(5)));

        loadCourses();
        loadStudents();

        tblStudents.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtFullName.setText(selected.get(1));
                txtRegNumber.setText(selected.get(2));
                txtEmail.setText(selected.get(4));
                txtPhone.setText(selected.get(5));
                for (Course c : cmbCourse.getItems()) {
                    if (c.getCourseName().equals(selected.get(3))) {
                        cmbCourse.setValue(c);
                        break;
                    }
                }
            }
        });

        txtSearch.textProperty().addListener((obs, old, val) -> {
            try {
                loadStudentsFiltered(val);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void loadStudents() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "SELECT s.student_id, s.full_name, s.reg_number, c.course_name, s.email, s.phone " +
                         "FROM students s JOIN courses c ON s.course_id = c.course_id";
            ResultSet rs = con.createStatement().executeQuery(sql);
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("student_id")));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("reg_number"));
                row.add(rs.getString("course_name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                data.add(row);
            }
            tblStudents.setItems(data);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void loadStudentsFiltered(String keyword) {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "SELECT s.student_id, s.full_name, s.reg_number, c.course_name, s.email, s.phone " +
                         "FROM students s JOIN courses c ON s.course_id = c.course_id " +
                         "WHERE s.full_name LIKE ? OR s.reg_number LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("student_id")));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("reg_number"));
                row.add(rs.getString("course_name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                data.add(row);
            }
            tblStudents.setItems(data);
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleAdd() {
        if (txtFullName.getText().isEmpty() || txtRegNumber.getText().isEmpty() || cmbCourse.getValue() == null) {
            showAlert("Error", "Full name, reg number and course are required!");
            return;
        }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "INSERT INTO students (full_name, reg_number, course_id, email, phone) VALUES (?,?,?,?,?)";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtFullName.getText());
            pst.setString(2, txtRegNumber.getText());
            pst.setInt(3, cmbCourse.getValue().getCourseId());
            pst.setString(4, txtEmail.getText());
            pst.setString(5, txtPhone.getText());
            pst.executeUpdate();
            showAlert("Success", "Student added successfully!");
            loadStudents();
            handleClear();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        ObservableList<String> selected = tblStudents.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a student!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "UPDATE students SET full_name=?, reg_number=?, course_id=?, email=?, phone=? WHERE student_id=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, txtFullName.getText());
            pst.setString(2, txtRegNumber.getText());
            pst.setInt(3, cmbCourse.getValue().getCourseId());
            pst.setString(4, txtEmail.getText());
            pst.setString(5, txtPhone.getText());
            pst.setInt(6, Integer.parseInt(selected.get(0)));
            pst.executeUpdate();
            showAlert("Success", "Student updated!");
            loadStudents();
            handleClear();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleDelete() {
        ObservableList<String> selected = tblStudents.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a student!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement("DELETE FROM students WHERE student_id=?");
            pst.setInt(1, Integer.parseInt(selected.get(0)));
            pst.executeUpdate();
            showAlert("Success", "Student deleted!");
            loadStudents();
            handleClear();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleClear() {
        txtFullName.clear();
        txtRegNumber.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtSearch.clear();
        cmbCourse.setValue(null);
        tblStudents.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
