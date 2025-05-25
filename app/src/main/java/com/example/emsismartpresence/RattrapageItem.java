package com.example.emsismartpresence;

public class RattrapageItem {
    private String courseName;
    private String groupId;
    private String date;
    private String startTime;
    private String endTime;
    private String room;
    private String siteId;
    private String level;

    public RattrapageItem() {}

    public RattrapageItem(String courseName, String groupId, String date, String startTime, String endTime, String room, String siteId, String level) {
        this.courseName = courseName;
        this.groupId = groupId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
        this.siteId = siteId;
        this.level = level;
    }

    public String getCourseName() { return courseName; }
    public String getGroupId() { return groupId; }
    public String getDate() { return date; }
    public String getStartTime() { return startTime; }
    public String getEndTime() { return endTime; }
    public String getRoom() { return room; }
    public String getSiteId() { return siteId; }
    public String getLevel() { return level; }

    public void setCourseName(String courseName) { this.courseName = courseName; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public void setDate(String date) { this.date = date; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public void setRoom(String room) { this.room = room; }
    public void setSiteId(String siteId) { this.siteId = siteId; }
    public void setLevel(String level) { this.level = level; }
}