package com.example.smartreminderapp;

/**
 * Model class for Reminder
 * Table: reminder_41
 * Fields: id, title, description, time, location, status (41 % 3 = 2 → status)
 */
public class Reminder {
    private int id;
    private String title;
    private String description;
    private String time;
    private String location;
    private String status; // Extra field for Roll No 41 (41 % 3 = 2 → status)

    // Default constructor
    public Reminder() {
    }

    // Parameterized constructor
    public Reminder(int id, String title, String description, String time, String location, String status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.time = time;
        this.location = location;
        this.status = status;
    }

    // Constructor without id (for inserting)
    public Reminder(String title, String description, String time, String location, String status) {
        this.title = title;
        this.description = description;
        this.time = time;
        this.location = location;
        this.status = status;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
