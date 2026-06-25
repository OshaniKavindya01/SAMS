package lk.ijse.sams.model;

public class Student {
    private int studentId;
    private String fullName;
    private String regNumber;
    private int courseId;
    private String email;
    private String phone;

    public Student() {}

    public Student(int studentId, String fullName, String regNumber, int courseId, String email, String phone) {
        this.studentId = studentId;
        this.fullName = fullName;
        this.regNumber = regNumber;
        this.courseId = courseId;
        this.email = email;
        this.phone = phone;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getRegNumber() { return regNumber; }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}