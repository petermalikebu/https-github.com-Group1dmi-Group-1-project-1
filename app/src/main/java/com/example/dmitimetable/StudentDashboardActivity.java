package com.example.dmitimetable;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class StudentDashboardActivity extends AppCompatActivity {

    private Spinner departmentsSpinner, yearSpinner, semesterSpinner, mySpinner; // Added mySpinner
    private TableLayout timetableTable;
    private ArrayList<TimetableEntry> timetableEntries;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        // Initialize Spinners and TableLayout
        departmentsSpinner = findViewById(R.id.spinnerDepartments);
        yearSpinner = findViewById(R.id.spinnerYear);
        semesterSpinner = findViewById(R.id.spinnerSemester);
        mySpinner = findViewById(R.id.my_spinner); // Initialize new spinner
        timetableTable = findViewById(R.id.timetableTable);
        Button viewTimetableButton = findViewById(R.id.buttonViewTimetable);

        timetableEntries = new ArrayList<>();
        setupSpinners();

        viewTimetableButton.setOnClickListener(v -> displayFilteredTimetable());
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

        // Set up adapters for the spinners
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentsSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        semesterSpinner.setAdapter(semesterAdapter);

        // Example for the new Spinner
        ArrayList<String> options = new ArrayList<>();
        options.add("Option 1");
        options.add("Option 2");
        options.add("Option 3");
        ArrayAdapter<String> mySpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, options);
        mySpinner.setAdapter(mySpinnerAdapter); // Set the adapter for mySpinner
    }

    private void displayFilteredTimetable() {
        timetableTable.removeAllViews();

        String selectedDepartment = departmentsSpinner.getSelectedItem().toString();
        String selectedYear = yearSpinner.getSelectedItem().toString();
        String selectedSemester = semesterSpinner.getSelectedItem().toString();

        // Add headers
        TableRow headerRow = new TableRow(this);
        headerRow.addView(createTextView("Date"));
        headerRow.addView(createTextView("Time"));
        headerRow.addView(createTextView("Module"));
        headerRow.addView(createTextView("Lecturer"));
        timetableTable.addView(headerRow);

        // Filter the timetable entries (Replace with actual data filtering logic)
        for (TimetableEntry entry : timetableEntries) {
            if (entry.getDepartment().equals(selectedDepartment)
                    && entry.getYear().equals(selectedYear)
                    && entry.getSemester().equals(selectedSemester)) {

                TableRow row = new TableRow(this);
                row.addView(createTextView(entry.getDate()));
                row.addView(createTextView(entry.getTime()));
                row.addView(createTextView(entry.getModule()));
                row.addView(createTextView(entry.getLecturer()));
                timetableTable.addView(row);
            }
        }
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(10, 10, 10, 10);
        return textView;
    }
}
