package com.example.dmitimetable;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class AdminDashboardActivity extends AppCompatActivity {

    private Spinner departmentsSpinner, yearSpinner, semesterSpinner;
    private TableLayout timetableTable;
    private EditText editTextDate, editTextTime, editTextModule, editTextLecturer, editTextClassroom;
    private ArrayList<String> departments;
    private ArrayList<String> years;
    private ArrayList<String> semesters;
    private DatabaseHelper databaseHelper;

    // Define classroom list
    private ArrayList<String> classrooms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize UI components
        departmentsSpinner = findViewById(R.id.spinnerDepartments);
        yearSpinner = findViewById(R.id.spinnerYear);
        semesterSpinner = findViewById(R.id.spinnerSemester);
        timetableTable = findViewById(R.id.timetableTable);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);
        editTextModule = findViewById(R.id.editTextModule);
        editTextLecturer = findViewById(R.id.editTextLecturer);
        editTextClassroom = findViewById(R.id.editTextClassroom);
        Button buttonAdd = findViewById(R.id.buttonAdd);
        Button buttonLogout = findViewById(R.id.buttonLogout); // Add logout button

        databaseHelper = new DatabaseHelper(this);
        classrooms = new ArrayList<>();

        // Populate classrooms
        classrooms.add("Room 101");
        classrooms.add("Room 102");
        classrooms.add("Room 103");
        classrooms.add("Room 201");

        departments = new ArrayList<>();
        years = new ArrayList<>();
        semesters = new ArrayList<>();

        // Populate departments
        departments.add("Computer Science (BSC)");
        departments.add("Social Work (BSW)");
        departments.add("Computer Engineering (BSE)");
        departments.add("Business Administration (BE)");
        departments.add("Business Communication (BCOM)");

        // Populate years
        years.add("First Year");
        years.add("Second Year");
        years.add("Third Year");
        years.add("Fourth Year");

        // Populate semesters
        semesters.add("First Semester");
        semesters.add("Second Semester");

        // Setup adapters for spinners
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, departments);
        departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        departmentsSpinner.setAdapter(departmentAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        yearSpinner.setAdapter(yearAdapter);

        ArrayAdapter<String> semesterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        semesterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        semesterSpinner.setAdapter(semesterAdapter);

        // Setup date picker dialog
        editTextDate.setOnClickListener(v -> showDatePickerDialog());

        buttonAdd.setOnClickListener(v -> addTimetableEntry());

        // Setup logout button click listener
        buttonLogout.setOnClickListener(v -> logout());

        // Load existing entries from database
        displayTimetableEntries();
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDay) -> {
            selectedMonth += 1; // Format month to be 1-based
            String selectedDate = selectedDay + "/" + selectedMonth + "/" + selectedYear;
            editTextDate.setText(selectedDate);
        }, year, month, day);
        datePickerDialog.show();
    }

    private void addTimetableEntry() {
        String date = editTextDate.getText().toString();
        String time = editTextTime.getText().toString();
        String module = editTextModule.getText().toString();
        String lecturer = editTextLecturer.getText().toString();
        String department = departmentsSpinner.getSelectedItem().toString();
        String year = yearSpinner.getSelectedItem().toString();
        String semester = semesterSpinner.getSelectedItem().toString();
        String classroom = editTextClassroom.getText().toString(); // Get classroom input

        // Validate inputs
        if (date.isEmpty() || time.isEmpty() || module.isEmpty() || lecturer.isEmpty() || classroom.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate time format for range (HH:mm-HH:mm)
        if (!validateAndSetTime(time)) {
            return; // Time validation failed, exit method
        }

        TimetableEntry entry = new TimetableEntry(date, time, module, lecturer, department, year, semester, classroom);
        if (databaseHelper.addTimetableEntry(entry)) {
            Toast.makeText(this, "Entry added", Toast.LENGTH_SHORT).show();
            displayTimetableEntries();
        } else {
            Toast.makeText(this, "Error adding entry", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateAndSetTime(String time) {
        // Validate time range format (HH:mm-HH:mm)
        if (!time.matches("([01]?\\d|2[0-3]):([0-5]\\d)-([01]?\\d|2[0-3]):([0-5]\\d)")) {
            Toast.makeText(this, "Invalid time format. Please use HH:mm-HH:mm.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true; // Time is valid
    }

    private void displayTimetableEntries() {
        // Clear the table before displaying
        timetableTable.removeAllViews();

        ArrayList<TimetableEntry> entries = databaseHelper.getAllTimetableEntries();
        for (TimetableEntry entry : entries) {
            // Create a new vertical layout for each entry
            TableLayout entryLayout = new TableLayout(this);

            // Create TextViews for each field
            TextView dateView = new TextView(this);
            TextView timeView = new TextView(this);
            TextView moduleView = new TextView(this);
            TextView lecturerView = new TextView(this);
            TextView departmentView = new TextView(this);
            TextView yearView = new TextView(this);
            TextView semesterView = new TextView(this);
            TextView classroomView = new TextView(this);
            Button deleteButton = new Button(this);

            // Set the text for each TextView
            dateView.setText("Date: " + entry.getDate());
            timeView.setText("Time: " + entry.getTime());
            moduleView.setText("Module Code: " + entry.getModule());
            lecturerView.setText("Lecturer: " + entry.getLecturer());
            departmentView.setText("Department: " + entry.getDepartment());
            yearView.setText("Year: " + entry.getYear());
            semesterView.setText("Semester: " + entry.getSemester());
            classroomView.setText("Classroom: " + entry.getClassroom());

            // Set the delete button text and action
            deleteButton.setText("Delete");
            deleteButton.setOnClickListener(v -> confirmDeleteEntry(entry.getId()));

            // Add all TextViews and the delete button to the entry layout
            entryLayout.addView(dateView);
            entryLayout.addView(timeView);
            entryLayout.addView(moduleView);
            entryLayout.addView(lecturerView);
            entryLayout.addView(departmentView);
            entryLayout.addView(yearView);
            entryLayout.addView(semesterView);
            entryLayout.addView(classroomView);
            entryLayout.addView(deleteButton); // Add delete button for each entry

            // Add the entry layout to the main timetable table
            timetableTable.addView(entryLayout);
        }
    }

    private void confirmDeleteEntry(int entryId) {
        // Display confirmation dialog for deletion
        new AlertDialog.Builder(this)
                .setTitle("Delete Entry")
                .setMessage("Are you sure you want to delete this entry?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (databaseHelper.deleteTimetableEntry(entryId)) {
                        Toast.makeText(this, "Entry deleted", Toast.LENGTH_SHORT).show();
                        displayTimetableEntries(); // Refresh the timetable after deletion
                    } else {
                        Toast.makeText(this, "Error deleting entry", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void logout() {
        // Clear user session or perform necessary logout operations
        // For example, redirect to login activity
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Close the current activity
    }
}
