package lk.ijse.sams.model;

public class Subject {
    private int subjectId;
    private String subjectName;
    private String subjectCode;
    private int courseId;

    public Subject() {}

    public Subject(int subjectId, String subjectName, String subjectCode, int courseId) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.courseId = courseId;
    }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    
    @Override
    public String toString() { return subjectName; }
}