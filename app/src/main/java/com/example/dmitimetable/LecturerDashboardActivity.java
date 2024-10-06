package com.example.dmitimetable;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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

    private Spinner departmentsSpinner, yearSpinner, semesterSpinner;
    private TableLayout timetableTable;
    private ArrayList<TimetableEntry> timetableEntries;
    private ProgressBar progressBar;
    private DatabaseHelper databaseHelper;
    private Button logoutButton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lecturer_dashboard);

        // Initialize UI components
        departmentsSpinner = findViewById(R.id.spinnerDepartments);
        yearSpinner = findViewById(R.id.spinnerYear);
        semesterSpinner = findViewById(R.id.spinnerSemester);
        timetableTable = findViewById(R.id.timetableTable);
        Button viewTimetableButton = findViewById(R.id.buttonViewTimetable);
        progressBar = findViewById(R.id.progressBar);
        logoutButton = findViewById(R.id.buttonLogout);  // Initialize logout button

        timetableEntries = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);

        setupSpinners();
        loadTimetableEntries();

        // Handle button click to view timetable
        viewTimetableButton.setOnClickListener(v -> displayFilteredTimetable());

        // Handle logout button click
        logoutButton.setOnClickListener(v -> logoutUser());
    }

    private void loadTimetableEntries() {
        progressBar.setVisibility(View.VISIBLE); // Show progress bar while loading

        try {
            timetableEntries.clear(); // Clear old entries
            timetableEntries.addAll(databaseHelper.getAllTimetableEntries()); // Load new entries
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

        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentsSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        semesterSpinner.setAdapter(semesterAdapter);
    }

    private void displayFilteredTimetable() {
        timetableTable.removeAllViews(); // Clear previous rows

        String selectedDepartment = departmentsSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();
        String selectedSemester = semesterSpinner.getSelectedItem().toString();

        // Add headers
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("Date"));
        headerRow.addView(createTextView("Time"));
        headerRow.addView(createTextView("Module"));
        headerRow.addView(createTextView("Lecturer"));
        headerRow.addView(createTextView("Classroom"));
        timetableTable.addView(headerRow);

        // Filter and add timetable entries based on the selected criteria
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
                row.addView(createTextView(entry.getClassroom()));
                timetableTable.addView(row);
            }
        }

        if (hasEntries) {
            timetableTable.setVisibility(View.VISIBLE); // Make the timetable visible if entries exist
        } else {
            timetableTable.setVisibility(View.GONE); // Hide the timetable if no entries match
            Toast.makeText(this, "No entries found for the selected criteria.", Toast.LENGTH_SHORT).show();
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }

    // Logout function
    private void logoutUser() {
        // Clear session data (example with SharedPreferences)
        SharedPreferences preferences = getSharedPreferences("user_session", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear(); // Clear the session data
        editor.apply();

        // Show a logout message
        Toast.makeText(LecturerDashboardActivity.this, "You have been logged out.", Toast.LENGTH_SHORT).show();

        // Redirect to login activity
        Intent intent = new Intent(LecturerDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear the activity stack
        startActivity(intent);
        finish(); // Close the dashboard activity
    }
}
