package lk.ijse.sams.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.db.DBConnection;
import java.sql.*;
import java.util.Arrays;
import java.util.List;

public class LecturerController {

    @FXML private TextField txtFullName, txtEmail, txtPhone, txtSearch;
    @FXML private TableView<ObservableList<String>> tblLecturers;
    @FXML private TableColumn<ObservableList<String>, String> colId, colName, colEmail, colPhone, colUsername;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(0)));
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(1)));
        colEmail.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(2)));
        colPhone.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(3)));
        colUsername.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().get(4)));

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
            String sql = "SELECT l.lecturer_id, l.full_name, l.email, l.phone, u.username " +
                         "FROM lecturers l LEFT JOIN users u ON u.lecturer_id = l.lecturer_id";
            ResultSet rs = con.createStatement().executeQuery(sql);
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("lecturer_id")));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("email") != null ? rs.getString("email") : "");
                row.add(rs.getString("phone") != null ? rs.getString("phone") : "");
                row.add(rs.getString("username") != null ? rs.getString("username") : "");
                data.add(row);
            }
            tblLecturers.setItems(data);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private void loadLecturersFiltered(String keyword) {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "SELECT l.lecturer_id, l.full_name, l.email, l.phone, u.username " +
                         "FROM lecturers l LEFT JOIN users u ON u.lecturer_id = l.lecturer_id " +
                         "WHERE l.full_name LIKE ? OR l.email LIKE ?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();
            while (rs.next()) {
                ObservableList<String> row = FXCollections.observableArrayList();
                row.add(String.valueOf(rs.getInt("lecturer_id")));
                row.add(rs.getString("full_name"));
                row.add(rs.getString("email") != null ? rs.getString("email") : "");
                row.add(rs.getString("phone") != null ? rs.getString("phone") : "");
                row.add(rs.getString("username") != null ? rs.getString("username") : "");
                data.add(row);
            }
            tblLecturers.setItems(data);
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    private String generateUsername(String fullName) {
        // Remove titles like Dr., Mr., Ms., Prof.
        List<String> titles = Arrays.asList("dr.", "mr.", "mrs.", "ms.", "prof.", "rev.");
        String[] parts = fullName.toLowerCase().replaceAll("[^a-z ]", "").trim().split("\\s+");
        
        // Filter out titles
        java.util.List<String> nameParts = new java.util.ArrayList<>();
        for (String part : parts) {
            if (!titles.contains(part + ".") && !titles.contains(part)) {
                nameParts.add(part);
            }
        }
        
        if (nameParts.size() >= 2) {
            return nameParts.get(0) + "." + nameParts.get(nameParts.size() - 1) + ".lec";
        } else if (nameParts.size() == 1) {
            return nameParts.get(0) + ".lec";
        }
        return fullName.toLowerCase().replaceAll(" ", ".") + ".lec";
    }

    private String generatePassword(String fullName) {
        List<String> titles = Arrays.asList("dr.", "mr.", "mrs.", "ms.", "prof.", "rev.");
        String[] parts = fullName.toLowerCase().replaceAll("[^a-z ]", "").trim().split("\\s+");
        for (String part : parts) {
            if (!titles.contains(part + ".") && !titles.contains(part)) {
                return part + "123";
            }
        }
        return "lec123";
    }

    @FXML
    private void handleAdd() {
        if (txtFullName.getText().isEmpty()) {
            showAlert("Error", "Full name is required!"); return;
        }
        try {
            Connection con = DBConnection.getInstance().getConnection();

            PreparedStatement pst = con.prepareStatement(
                "INSERT INTO lecturers (full_name, email, phone) VALUES (?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, txtFullName.getText());
            pst.setString(2, txtEmail.getText());
            pst.setString(3, txtPhone.getText());
            pst.executeUpdate();

            ResultSet keys = pst.getGeneratedKeys();
            int lecturerId = 0;
            if (keys.next()) lecturerId = keys.getInt(1);

            String username = generateUsername(txtFullName.getText());
            String password = generatePassword(txtFullName.getText());

            PreparedStatement pst2 = con.prepareStatement(
                "INSERT INTO users (username, password, role, lecturer_id) VALUES (?,?,?,?)");
            pst2.setString(1, username);
            pst2.setString(2, password);
            pst2.setString(3, "LECTURER");
            pst2.setInt(4, lecturerId);
            pst2.executeUpdate();

            showAlert("Success", "Lecturer added!\nUsername: " + username + "\nDefault Password: " + password);
            loadLecturers();
            handleClear();
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
            loadLecturers();
            handleClear();
        } catch (Exception e) { showAlert("Error", e.getMessage()); }
    }

    @FXML
    private void handleDelete() {
        ObservableList<String> selected = tblLecturers.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert("Error", "Please select a lecturer!"); return; }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst1 = con.prepareStatement(
                "DELETE FROM users WHERE lecturer_id=?");
            pst1.setInt(1, Integer.parseInt(selected.get(0)));
            pst1.executeUpdate();
            PreparedStatement pst2 = con.prepareStatement(
                "DELETE FROM lecturers WHERE lecturer_id=?");
            pst2.setInt(1, Integer.parseInt(selected.get(0)));
            pst2.executeUpdate();
            showAlert("Success", "Lecturer deleted!");
            loadLecturers();
            handleClear();
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
