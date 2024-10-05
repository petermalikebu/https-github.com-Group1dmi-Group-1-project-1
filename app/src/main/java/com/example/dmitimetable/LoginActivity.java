package com.example.dmitimetable;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private EditText idEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button registerButton; // Button to navigate to registration
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth and Database reference
        auth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        // Bind UI elements
        idEditText = findViewById(R.id.editTextId);
        passwordEditText = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.buttonLogin);
        registerButton = findViewById(R.id.buttonRegister); // Reference to the register button
        progressBar = findViewById(R.id.progressBar);

        // Set OnClickListeners for buttons
        loginButton.setOnClickListener(v -> loginUser());
        registerButton.setOnClickListener(v -> navigateToRegister()); // Navigate to RegisterActivity
    }

    private void loginUser() {
        String id = idEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        // Validate inputs
        if (!isEmailValid(id)) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter your password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Show progress bar
        progressBar.setVisibility(View.VISIBLE);

        // Log in with Firebase Authentication
        auth.signInWithEmailAndPassword(id, password)
                .addOnCompleteListener(this, task -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        String errorMsg = task.getException() != null ? task.getException().getMessage() : "Login failed";
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean isEmailValid(String email) {
        return Pattern.compile("^[\\w-\\.]+@[\\w-]+\\.[a-zA-Z]{2,}$").matcher(email).matches();
    }

    private void navigateToDashboard() {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            // Fetch the user's role from the database
            databaseReference.child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String role = dataSnapshot.child("role").getValue(String.class);
                    if (role != null) {
                        Intent intent;
                        if ("Admin".equalsIgnoreCase(role)) {
                            intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                        } else if ("Lecturer".equalsIgnoreCase(role)) {
                            intent = new Intent(LoginActivity.this, LecturerDashboardActivity.class);
                        } else if ("Student".equalsIgnoreCase(role)) {
                            intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
                        } else {
                            Toast.makeText(LoginActivity.this, "Unknown role: " + role, Toast.LENGTH_SHORT).show();
                            return; // Exit if role is unknown
                        }
                        startActivity(intent);
                        finish(); // Close LoginActivity
                    } else {
                        Toast.makeText(LoginActivity.this, "User role not found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(LoginActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Method to navigate to the registration screen
    private void navigateToRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
    }
}
