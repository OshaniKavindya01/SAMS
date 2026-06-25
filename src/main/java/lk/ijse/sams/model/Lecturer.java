package lk.ijse.sams.model;

public class Lecturer {
    private int lecturerId;
    private String fullName;
    private String email;
    private String phone;

    public Lecturer() {}

    public Lecturer(int lecturerId, String fullName, String email, String phone) {
        this.lecturerId = lecturerId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
    }

    public int getLecturerId() { return lecturerId; }
    public void setLecturerId(int lecturerId) { this.lecturerId = lecturerId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    @Override
    public String toString() { return fullName; }
}