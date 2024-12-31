package com.example.trivia;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class RegisterActivity extends AppCompatActivity {

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText usernameField = findViewById(R.id.username);
        EditText passwordField = findViewById(R.id.password);
        Button registerButton = findViewById(R.id.registerButton);

        database = openOrCreateDatabase("TriviaApp", MODE_PRIVATE, null);
        setupDatabase();

        registerButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else if (isUserExists(username)) {
                Toast.makeText(RegisterActivity.this, "Username already exists", Toast.LENGTH_SHORT).show();
            } else {
                createUser(username, password);
                Toast.makeText(RegisterActivity.this, "User registered successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setupDatabase() {
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS Users (" +
                    "id INTEGER PRIMARY KEY, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT)");
            Log.d("TriviaApp", "Users table created successfully.");
        } catch (Exception e) {
            Log.e("TriviaApp", "Error in setupDatabase: " + e.getMessage());
        }
    }

    private boolean isUserExists(String username) {
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM Users WHERE username = ?";
            cursor = database.rawQuery(query, new String[]{username});
            boolean exists = cursor.moveToFirst();
            if (exists) {
                Log.d("TriviaApp", "User already exists: " + username);
            } else {
                Log.d("TriviaApp", "User does not exist: " + username);
            }
            return exists;
        } catch (Exception e) {
            Log.e("TriviaApp", "Error checking user existence: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void createUser(String username, String password) {
        try {
            String query = "INSERT INTO Users (username, password) VALUES (?, ?)";
            database.execSQL(query, new Object[]{username, password});
            Log.d("TriviaApp", "User created successfully: " + username);
        } catch (Exception e) {
            Log.e("TriviaApp", "Error creating user: " + e.getMessage());
            Toast.makeText(this, "Error registering user", Toast.LENGTH_SHORT).show();
        }
    }
}
