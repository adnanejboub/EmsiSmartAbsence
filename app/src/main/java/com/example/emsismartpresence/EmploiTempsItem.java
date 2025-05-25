package com.example.emsismartpresence;

public class EmploiTempsItem {
    private String courseName;
    private String groupId;
    private String day;
    private String startTime;
    private String endTime;
    private String room;
    private String siteId;
    private String level;

    public EmploiTempsItem() {}

    public EmploiTempsItem(String courseName, String groupId, String day, String startTime,
                           String endTime, String room, String siteId, String level) {
        this.courseName = courseName;
        this.groupId = groupId;
        this.day = day;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.siteId = siteId;
        this.level = level;
    }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }
    public String getSiteId() { return siteId; }
    public void setSiteId(String siteId) { this.siteId = siteId; }
    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }
}