package lk.ijse.sams.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.sams.db.DBConnection;
import java.sql.*;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRole;

    @FXML
    public void initialize() {
        cmbRole.setItems(FXCollections.observableArrayList("ADMIN", "LECTURER"));
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();
        String role = cmbRole.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Please fill all fields!");
            return;
        }

        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "SELECT * FROM users WHERE username=? AND password=? AND role=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                showAlert("Success", "Welcome " + username + "!");
                loadDashboard(role);
            } else {
                showAlert("Error", "Invalid credentials!");
            }
        } catch (Exception e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void loadDashboard(String role) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/lk/ijse/sams/DashboardView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("SAMS - Dashboard");
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Could not load dashboard: " + e.getMessage());
        }
    }

    @FXML private Button btnLogin;

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}