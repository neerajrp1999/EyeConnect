package com.neer.eyeconnect.ui.Lost;

public class ReportPerson {
    private String name;
    private String userId;
    private String reportId;
    private String dateLost;
    private String location;
    private String email;
    private Long phnum;
    private String description;
    private String timeLost;
    private String imageURI;
    private String tag = "Missing Person";


    public String getEmail() {
        return email;
    }

    public Long getPhnum() {
        return phnum;
    }

    public void setPhnum(Long phnum) {
        this.phnum = phnum;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getDescription() {
        return description;
    }

    public String getTag(){return tag;};
    public void setDescription(String description) {
        this.description = description;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }



    public String getDateLost() {
        return dateLost;
    }

    public String getTimeLost() {
        return timeLost;
    }

    public void setTimeLost(String timeLost) {
        this.timeLost = timeLost;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public void setDateLost(String dateLost) {
        this.dateLost = dateLost;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public ReportPerson() {
    }
}
