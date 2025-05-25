package com.example.emsismartpresence;

import java.util.List;

public class AbsenceEntry {
    private String groupId;
    private String siteId;
    private String date;
    private List<Student> students;
    private String remarks;

    public AbsenceEntry() {}

    public AbsenceEntry(String groupId, String siteId, String date, List<Student> students, String remarks) {
        this.groupId = groupId;
        this.siteId = siteId;
        this.date = date;
        this.students = students;
        this.remarks = remarks;
    }

    public String getGroupId() { return groupId; }
    public String getSiteId() { return siteId; }
    public String getDate() { return date; }
    public List<Student> getStudents() { return students; }
    public String getRemarks() { return remarks; }

    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }
    public void setDate(String date) { this.date = date; }
    public void setStudents(List<Student> students) { this.students = students; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
}