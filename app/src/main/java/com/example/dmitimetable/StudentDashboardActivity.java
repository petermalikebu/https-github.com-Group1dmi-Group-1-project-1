package com.example.dmitimetable;

import android.content.Intent; // Import Intent class
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StudentDashboardActivity extends AppCompatActivity {

    private Spinner departmentsSpinner, yearSpinner, semesterSpinner, classroomSpinner;
    private TableLayout timetableTable;
    private ArrayList<TimetableEntry> timetableEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Initialize Spinners and TableLayout
        departmentsSpinner = findViewById(R.id.spinnerDepartments);
        yearSpinner = findViewById(R.id.spinnerYear);
        semesterSpinner = findViewById(R.id.spinnerSemester);
        classroomSpinner = findViewById(R.id.spinnerClassroom);
        timetableTable = findViewById(R.id.timetableTable);
        Button viewTimetableButton = findViewById(R.id.buttonViewTimetable);
        Button logoutButton = findViewById(R.id.buttonLogout); // Initialize Logout button

        timetableEntries = new ArrayList<>();
        setupSpinners();

        viewTimetableButton.setOnClickListener(v -> displayFilteredTimetable());

        // Set Logout button functionality
        logoutButton.setOnClickListener(v -> {
            // Optionally, clear user session data or authentication token here

            // Notify user of successful logout
            Toast.makeText(StudentDashboardActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();

            // Create an Intent to navigate back to the LoginActivity
            Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
            startActivity(intent);

            // Finish this activity
            finish(); // This will close the current activity
        });
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

        // Set up adapters for the spinners
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentsSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        semesterSpinner.setAdapter(semesterAdapter);

        ArrayAdapter<String> classroomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, classrooms);
        classroomSpinner.setAdapter(classroomAdapter);
    }

    private void displayFilteredTimetable() {
        timetableTable.removeAllViews(); // Clear previous rows

        // Check for valid selections
        if (departmentsSpinner.getSelectedItem() == null || yearSpinner.getSelectedItem() == null ||
                semesterSpinner.getSelectedItem() == null || classroomSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select all fields.", Toast.LENGTH_SHORT).show();
            return; // Early exit if any spinner is not selected
        }

        String selectedDepartment = departmentsSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();
        String selectedSemester = semesterSpinner.getSelectedItem().toString();
        String selectedClassroom = classroomSpinner.getSelectedItem().toString();

        // Add headers
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("Date"));
        headerRow.addView(createTextView("Time"));
        headerRow.addView(createTextView("Module"));
        headerRow.addView(createTextView("Lecturer"));
        headerRow.addView(createTextView("Classroom"));
        timetableTable.addView(headerRow);

        // Filter and add timetable entries
        boolean hasEntries = false;
        for (TimetableEntry entry : timetableEntries) {
            if (entry.getDepartment().equals(selectedDepartment)
                    && entry.getYear().equals(selectedYear)
                    && entry.getSemester().equals(selectedSemester)
                    && entry.getClassroom().equals(selectedClassroom)) {

                hasEntries = true;
                TableRow row = new TableRow(this);
                row.addView(createTextView(entry.getDate()));
                row.addView(createTextView(entry.getTime()));
                row.addView(createTextView(entry.getModule()));
                row.addView(createTextView(entry.getLecturer()));
                row.addView(createTextView(entry.getClassroom()));
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
