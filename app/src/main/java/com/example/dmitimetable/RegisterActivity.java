package com.example.dmitimetable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;  // Import ProgressBar
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private EditText idEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText confirmPasswordEditText;
    private Spinner roleSpinner;
    private Button registerButton;
    private ProgressBar progressBar; // Add ProgressBar reference

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth and Database
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        // Bind UI elements
        idEditText = findViewById(R.id.editTextId);
        emailEditText = findViewById(R.id.editTextEmail);
        passwordEditText = findViewById(R.id.editTextPassword);
        confirmPasswordEditText = findViewById(R.id.editTextConfirmPassword);
        roleSpinner = findViewById(R.id.spinnerRole);
        registerButton = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBar); // Initialize ProgressBar

        // Populate the spinner with roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        roleSpinner.setAdapter(adapter);

        // Set OnClickListener for the register button
        registerButton.setOnClickListener(v -> registerUser());
    }

    private void registerUser() {
        String id = idEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        // Get selected role from Spinner
        String role = roleSpinner.getSelectedItem() != null ? roleSpinner.getSelectedItem().toString() : null;

        // Validate inputs
        if (validateInputs(id, email, password, confirmPassword, role)) {
            progressBar.setVisibility(View.VISIBLE);  // Show ProgressBar

            // Create user with Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        progressBar.setVisibility(View.GONE);  // Hide ProgressBar
                        if (task.isSuccessful()) {
                            saveUserToDatabase(id, email, role);
                        } else {
                            Toast.makeText(RegisterActivity.this, "Registration failed: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private boolean validateInputs(String id, String email, String password, String confirmPassword, String role) {
        if (id.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || role == null) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void saveUserToDatabase(String id, String email, String role) {
        String userId = Objects.requireNonNull(auth.getCurrentUser()).getUid();
        User user = new User(id, email, role); // Make sure User class is properly defined

        // Save user to Firebase Realtime Database
        database.getReference().child("users").child(userId).setValue(user)
                .addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        navigateToDashboard(role);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Database error: " + Objects.requireNonNull(dbTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToDashboard(String role) {
        Intent intent;
        switch (role) {
            case "Admin":
                intent = new Intent(RegisterActivity.this, AdminDashboardActivity.class);
                break;
            case "Lecturer":
                intent = new Intent(RegisterActivity.this, LecturerDashboardActivity.class);
                break;
            case "Student":
                intent = new Intent(RegisterActivity.this, StudentDashboardActivity.class);
                break;
            default:
                Toast.makeText(this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                return;
        }
        startActivity(intent);
        finish(); // Close RegisterActivity
    }
}