package lk.ijse.sams.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import lk.ijse.sams.db.DBConnection;
import lk.ijse.sams.model.SessionManager;
import java.sql.*;

public class ProfileController {

    @FXML private TextField txtUsername, txtRole, txtEmail;
    @FXML private PasswordField txtCurrentPassword, txtNewPassword, txtConfirmPassword;

    @FXML
    public void initialize() {
        loadProfile();
    }

    private void loadProfile() {
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "SELECT * FROM users WHERE user_id=?");
            pst.setInt(1, SessionManager.getInstance().getUserId());
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                txtUsername.setText(rs.getString("username"));
                txtRole.setText(rs.getString("role"));
                txtEmail.setText(rs.getString("email") != null ? rs.getString("email") : "");
            }
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdate() {
        if (txtEmail.getText().isEmpty()) {
            showAlert("Error", "Email cannot be empty!"); return;
        }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement pst = con.prepareStatement(
                "UPDATE users SET email=? WHERE user_id=?");
            pst.setString(1, txtEmail.getText());
            pst.setInt(2, SessionManager.getInstance().getUserId());
            pst.executeUpdate();
            showAlert("Success", "Profile updated!");
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    @FXML
    private void handleChangePassword() {
        if (txtCurrentPassword.getText().isEmpty() ||
            txtNewPassword.getText().isEmpty() ||
            txtConfirmPassword.getText().isEmpty()) {
            showAlert("Error", "Please fill all password fields!"); return;
        }
        if (!txtNewPassword.getText().equals(txtConfirmPassword.getText())) {
            showAlert("Error", "New passwords do not match!"); return;
        }
        try {
            Connection con = DBConnection.getInstance().getConnection();
            PreparedStatement check = con.prepareStatement(
                "SELECT * FROM users WHERE user_id=? AND password=?");
            check.setInt(1, SessionManager.getInstance().getUserId());
            check.setString(2, txtCurrentPassword.getText());
            ResultSet rs = check.executeQuery();
            if (!rs.next()) {
                showAlert("Error", "Current password is incorrect!"); return;
            }
            PreparedStatement pst = con.prepareStatement(
                "UPDATE users SET password=? WHERE user_id=?");
            pst.setString(1, txtNewPassword.getText());
            pst.setInt(2, SessionManager.getInstance().getUserId());
            pst.executeUpdate();
            showAlert("Success", "Password changed successfully!");
            txtCurrentPassword.clear();
            txtNewPassword.clear();
            txtConfirmPassword.clear();
        } catch (Exception e) {
            showAlert("Error", e.getMessage());
        }
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
