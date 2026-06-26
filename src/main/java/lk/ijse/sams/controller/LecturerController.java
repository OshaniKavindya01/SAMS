package lk.ijse.sams.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.db.DBConnection;
import java.sql.*;

public class LecturerController {

    @FXML private TextField txtFullName, txtEmail, txtPhone, txtSearch;
    @FXML private TableView<ObservableList<String>> tblLecturers;
    @FXML private TableColumn<ObservableList<String>, String> colId, colName, colEmail, colPhone;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(0)));
        colName.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(1)));
        colEmail.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(2)));
        colPhone.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().get(3)));

        loadLecturers();

        tblLecturers.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                txtFullName.setText(selected.get(1));
                txtEmail.setText(selected.get(2));
                txtPhone.setText(selected.get(3));
            }
        });

        txtSearch.textProperty().addListener((obs, old, val) -> loadLecturersFiltered(val));
    }

    private void loadLecturers() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM lecturers");
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("lecturer_id")));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                data.add(row);
            }
            tblLecturers.setItems(data);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void loadLecturersFiltered(String keyword) {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "SELECT * FROM lecturers WHERE full_name LIKE ? OR email LIKE ?");
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("lecturer_id")));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                data.add(row);
            }
            tblLecturers.setItems(data);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleAdd() {
        if (txtFullName.getText().isEmpty()) { showAlert("Error", "Full name is required!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO lecturers (full_name, email, phone) VALUES (?,?,?)");
            pst.setString(1, txtFullName.getText());
            pst.setString(2, txtEmail.getText());
            pst.setString(3, txtPhone.getText());
            pst.executeUpdate();
            showAlert("Success", "Lecturer added!");
            loadLecturers(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleUpdate() {
        ObservableList<String> selected = tblLecturers.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a lecturer!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "UPDATE lecturers SET full_name=?, email=?, phone=? WHERE lecturer_id=?");
            pst.setString(1, txtFullName.getText());
            pst.setString(2, txtEmail.getText());
            pst.setString(3, txtPhone.getText());
            pst.setInt(4, Integer.parseInt(selected.get(0)));
            pst.executeUpdate();
            showAlert("Success", "Lecturer updated!");
            loadLecturers(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleDelete() {
        ObservableList<String> selected = tblLecturers.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a lecturer!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "DELETE FROM lecturers WHERE lecturer_id=?");
            pst.setInt(1, Integer.parseInt(selected.get(0)));
            pst.executeUpdate();
            showAlert("Success", "Lecturer deleted!");
            loadLecturers(); handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleClear() {
        txtFullName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtSearch.clear();
        tblLecturers.getSelectionModel().clearSelection();
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
