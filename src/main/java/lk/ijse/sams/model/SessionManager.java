package lk.ijse.sams.model;

public class SessionManager {
    private static SessionManager instance;
    private int userId;
    private String username;
    private String role;
    private int lecturerId;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }

    public void clear() {
        userId = 0;
        username = null;
        role = null;
        lecturerId = 0;
    }
}