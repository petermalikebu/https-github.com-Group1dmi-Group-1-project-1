package com.example.dmitimetable;

public class TimetableEntry {
    private int id; // ID field for the database
    private final String date;
    private final String time;
    private final String module;
    private final String lecturer;
    private final String department;
    private final String year;
    private final String semester;
    private final String classroom; // Classroom field

    // Constructor without id (for creating new entries)
    public TimetableEntry(String date, String time, String module, String lecturer, String department, String year, String semester, String classroom) {
        this.date = date;
        this.time = time;
        this.module = module;
        this.lecturer = lecturer;
        this.department = department;
        this.year = year;
        this.semester = semester;
        this.classroom = classroom; // Assign classroom
    }

    // Constructor with id (for retrieving entries from the database)
    public TimetableEntry(int id, String date, String time, String module, String lecturer, String department, String year, String semester, String classroom) {
        this.id = id; // Initialize ID
        this.date = date;
        this.time = time;
        this.module = module;
        this.lecturer = lecturer;
        this.department = department;
        this.year = year;
        this.semester = semester;
        this.classroom = classroom; // Assign classroom
    }

    // Getters
    public int getId() { return id; } // Getter for ID
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getModule() { return module; }
    public String getLecturer() { return lecturer; }
    public String getDepartment() { return department; }
    public String getYear() { return year; }
    public String getSemester() { return semester; }
    public String getClassroom() { return classroom; } // Getter for classroom
}