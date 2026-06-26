package lk.ijse.sams.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lk.ijse.sams.db.DBConnection;
import lk.ijse.sams.model.SessionManager;
import java.sql.*;
import org.kordamp.ikonli.javafx.FontIcon;

public class LoginController {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtPasswordVisible;
    @FXML private ComboBox<String> cmbRole;
    @FXML private Button btnLogin;
    @FXML private Button btnTogglePassword;
    @FXML private FontIcon eyeIcon;

    private boolean passwordVisible = false;

    @FXML
    public void initialize() {
        cmbRole.setItems(FXCollections.observableArrayList("ADMIN", "LECTURER"));
        txtPasswordVisible.managedProperty().bind(txtPasswordVisible.visibleProperty());
        txtPassword.managedProperty().bind(txtPassword.visibleProperty());
    }

    @FXML
    private void togglePassword() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            txtPasswordVisible.setText(txtPassword.getText());
            txtPasswordVisible.setVisible(true);
            txtPassword.setVisible(false);
            eyeIcon.setIconLiteral("fas-eye");
        } else {
            txtPassword.setText(txtPasswordVisible.getText());
            txtPassword.setVisible(true);
            txtPasswordVisible.setVisible(false);
            eyeIcon.setIconLiteral("fas-eye-slash");
        }
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = passwordVisible ? txtPasswordVisible.getText().trim() : txtPassword.getText().trim();
        String role = cmbRole.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null) {
            showAlert("Error", "Please fill all fields!");
            return;
        }

        try {
            Connection con = DBConnection.getInstance().getConnection();
            String sql = "SELECT u.*, l.lecturer_id as lid FROM users u " +
                         "LEFT JOIN lecturers l ON u.lecturer_id = l.lecturer_id " +
                         "WHERE u.username=? AND u.password=? AND u.role=?";
            PreparedStatement pst = con.prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, role);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                SessionManager.getInstance().setUserId(rs.getInt("user_id"));
                SessionManager.getInstance().setUsername(username);
                SessionManager.getInstance().setRole(role);

                if (role.equals("LECTURER")) {
                    int lecId = rs.getInt("lecturer_id");
                    SessionManager.getInstance().setLecturerId(lecId);
                }

                if (role.equals("ADMIN")) {
                    loadView("/lk/ijse/sams/DashboardView.fxml", "SAMS - Admin Dashboard");
                } else {
                    loadView("/lk/ijse/sams/LecturerDashboardView.fxml", "SAMS - Lecturer Portal");
                }
            } else {
                showAlert("Error", "Invalid credentials!");
            }
        } catch (Exception e) {
            showAlert("Error", "Database error: " + e.getMessage());
        }
    }

    private void loadView(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (Exception e) {
            showAlert("Error", "Could not load view: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
