package com.example.dmitimetable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class LecturerDashboardActivity extends AppCompatActivity {

    private Spinner departmentsSpinner, yearSpinner, semesterSpinner, classroomSpinner;
    private TableLayout timetableTable;
    private ArrayList<TimetableEntry> timetableEntries;
    private ProgressBar progressBar;
    private Button logoutButton; // Logout button

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_dashboard);

        // Initialize UI components
        departmentsSpinner = findViewById(R.id.spinnerDepartments);
        yearSpinner = findViewById(R.id.spinnerYear);
        semesterSpinner = findViewById(R.id.spinnerSemester);
        classroomSpinner = findViewById(R.id.spinnerClassroom); // Added Classroom spinner
        timetableTable = findViewById(R.id.timetableTable);
        Button viewTimetableButton = findViewById(R.id.buttonViewTimetable);
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar
        logoutButton = findViewById(R.id.buttonLogout); // Initialize Logout button

        setupSpinners();
        timetableEntries = new ArrayList<>();

        // Load the entries immediately when the dashboard loads
        loadTimetableEntries();

        // Handle button click to view timetable
        viewTimetableButton.setOnClickListener(v -> {
            if (timetableEntries.isEmpty()) {
                Toast.makeText(LecturerDashboardActivity.this, "No timetable entries found.", Toast.LENGTH_SHORT).show();
            } else {
                displayFilteredTimetable();
            }
        });

        // Handle logout button click
        logoutButton.setOnClickListener(v -> logout());
    }

    private void logout() {
        // Clear session or shared preferences if applicable (if you're using any)
        // For demonstration, we redirect to the login screen
        Intent intent = new Intent(LecturerDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // Clear back stack
        startActivity(intent);
        finish(); // Close current activity
    }

    private void loadTimetableEntries() {
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        progressBar.setVisibility(View.VISIBLE); // Show progress bar while loading

        try {
            timetableEntries.clear(); // Clear the old entries
            timetableEntries.addAll(databaseHelper.getAllTimetableEntries()); // Load from the database
        } catch (Exception e) {
            Toast.makeText(this, "Error loading timetable entries: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            progressBar.setVisibility(View.GONE); // Hide progress bar after loading
        }
    }

    private void setupSpinners() {
        ArrayList<String> departments = new ArrayList<>();
        departments.add("Computer Science (BSC)");
        departments.add("Social Work (BSW)");
        departments.add("Computer Engineering (BSE)");
        departments.add("Business Administration (BE)");
        departments.add("Business Communication (BCOM)");

        ArrayList<String> years = new ArrayList<>();
        years.add("First Year");
        years.add("Second Year");
        years.add("Third Year");
        years.add("Fourth Year");

        ArrayList<String> semesters = new ArrayList<>();
        semesters.add("First Semester");
        semesters.add("Second Semester");

        ArrayList<String> classrooms = new ArrayList<>();
        classrooms.add("Room 101");
        classrooms.add("Room 102");
        classrooms.add("Room 103");
        classrooms.add("Room 201");

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentsSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        semesterSpinner.setAdapter(semesterAdapter);

        ArrayAdapter<String> classroomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classrooms);
        classroomSpinner.setAdapter(classroomAdapter); // Added Classroom adapter
    }

    private void displayFilteredTimetable() {
        timetableTable.removeAllViews(); // Clear previous rows

        if (departmentsSpinner.getSelectedItem() == null || yearSpinner.getSelectedItem() == null ||
                semesterSpinner.getSelectedItem() == null || classroomSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select all fields.", Toast.LENGTH_SHORT).show();
            return; // Early exit if any spinner is not selected
        }

        String selectedDepartment = departmentsSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();
        String selectedSemester = semesterSpinner.getSelectedItem().toString();
        String selectedClassroom = classroomSpinner.getSelectedItem().toString(); // Added Classroom filter

        // Add headers
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("Date"));
        headerRow.addView(createTextView("Time"));
        headerRow.addView(createTextView("Module"));
        headerRow.addView(createTextView("Lecturer"));
        headerRow.addView(createTextView("Classroom")); // Added Classroom header
        timetableTable.addView(headerRow);

        // Filter and add timetable entries based on the selected criteria
        boolean hasEntries = false;
        for (TimetableEntry entry : timetableEntries) {
            // Check for null values in TimetableEntry
            if (entry != null &&
                    selectedDepartment.equals(entry.getDepartment()) &&
                    selectedYear.equals(entry.getYear()) &&
                    selectedSemester.equals(entry.getSemester()) &&
                    selectedClassroom.equals(entry.getClassroom())) { // Added Classroom condition

                hasEntries = true;
                TableRow row = new TableRow(this);
                row.addView(createTextView(entry.getDate()));
                row.addView(createTextView(entry.getTime()));
                row.addView(createTextView(entry.getModule()));
                row.addView(createTextView(entry.getLecturer()));
                row.addView(createTextView(entry.getClassroom())); // Added Classroom data
                timetableTable.addView(row);
            }
        }

        if (!hasEntries) {
            Toast.makeText(this, "No entries found for the selected criteria.", Toast.LENGTH_SHORT).show();
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }
}
