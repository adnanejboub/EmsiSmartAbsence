package com.example.emsismartpresence;

public class Student {
    private String studentId;
    private String name;
    private boolean isAbsent;

    public Student() {}

    public Student(String studentId, String name, boolean isAbsent) {
        this.studentId = studentId;
        this.name = name;
        this.isAbsent = isAbsent;
    }

    public String getStudentId() { return studentId; }
    public String getName() { return name; }
    public boolean isAbsent() { return isAbsent; }
    public void setAbsent(boolean absent) { isAbsent = absent; }
}