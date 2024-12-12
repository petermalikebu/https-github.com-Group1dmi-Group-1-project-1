package com.example.dmitimetable;

import android.content.Intent;
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

    private Spinner departmentsSpinner, yearSpinner, semesterSpinner;
    private TableLayout timetableTable;
    private ArrayList<TimetableEntry> timetableEntries;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Initialize UI components
        departmentsSpinner = findViewById(R.id.spinnerDepartments);
        yearSpinner = findViewById(R.id.spinnerYear);
        semesterSpinner = findViewById(R.id.spinnerSemester);
        timetableTable = findViewById(R.id.timetableTable);
        Button viewTimetableButton = findViewById(R.id.buttonViewTimetable);
        Button logoutButton = findViewById(R.id.buttonLogout);

        timetableEntries = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        setupSpinners();

        viewTimetableButton.setOnClickListener(v -> displayFilteredTimetable());

        // Logout button functionality
        logoutButton.setOnClickListener(v -> {
            Toast.makeText(StudentDashboardActivity.this, "Logged out successfully!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
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

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentsSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        semesterSpinner.setAdapter(semesterAdapter);
    }

    private void displayFilteredTimetable() {
        timetableTable.removeAllViews(); // Clear previous rows

        if (departmentsSpinner.getSelectedItem() == null || yearSpinner.getSelectedItem() == null || semesterSpinner.getSelectedItem() == null) {
            Toast.makeText(this, "Please select all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        String selectedDepartment = departmentsSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();
        String selectedSemester = semesterSpinner.getSelectedItem().toString();

        timetableEntries = databaseHelper.getAllTimetableEntries();

        // Add headers
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("Date"));
        headerRow.addView(createTextView("Time"));
        headerRow.addView(createTextView("Module"));
        headerRow.addView(createTextView("Lecturer"));
        headerRow.addView(createTextView("Classroom"));
        timetableTable.addView(headerRow);

        // Filter and add timetable entries based on selected criteria
        boolean hasEntries = false;
        for (TimetableEntry entry : timetableEntries) {
            if (entry.getDepartment().equals(selectedDepartment)
                    && entry.getYear().equals(selectedYear)
                    && entry.getSemester().equals(selectedSemester)) {

                hasEntries = true;
                TableRow row = new TableRow(this);
                row.addView(createTextView(entry.getDate()));
                row.addView(createTextView(entry.getTime()));
                row.addView(createTextView(entry.getModule()));
                row.addView(createTextView(entry.getLecturer()));
                row.addView(createTextView(entry.getClassroom())); // Add classroom in displayed data
                timetableTable.addView(row);
            }
        }

        if (hasEntries) {
            timetableTable.setVisibility(View.VISIBLE); // Show timetable if entries are available
        } else {
            timetableTable.setVisibility(View.GONE); // Hide timetable and show a message
            Toast.makeText(this, "No timetable entries found for the selected criteria.", Toast.LENGTH_SHORT).show();
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }
}
