package com.example.trivia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        EditText usernameField = findViewById(R.id.username);
        EditText passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        Button registerButton = findViewById(R.id.registerButton);

        try {
            database = openOrCreateDatabase("TriviaApp", MODE_PRIVATE, null);
            setupDatabase();
        } catch (Exception e) {
            Log.e("TriviaApp", "Database initialization error: " + e.getMessage());
            Toast.makeText(this, "Database error", Toast.LENGTH_SHORT).show();
            return;
        }

        loginButton.setOnClickListener(v -> {
            String username = usernameField.getText().toString();
            String password = passwordField.getText().toString();

            if (validateUser(username, password)) {
                String lastScore = getLastScore(username);

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("lastScore", lastScore);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupDatabase() {
        try {
            database.execSQL("CREATE TABLE IF NOT EXISTS Users (" +
                    "id INTEGER PRIMARY KEY, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT)");

            Log.d("TriviaApp", "Tables created successfully.");
        } catch (Exception e) {
            Log.e("TriviaApp", "Error in setupDatabase: " + e.getMessage());
        }
    }

    private boolean validateUser(String username, String password) {
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
            cursor = database.rawQuery(query, new String[]{username, password});
            boolean isValid = cursor.moveToFirst();
            if (isValid) {
                Log.d("TriviaApp", "User validated: " + username);
            } else {
                Log.d("TriviaApp", "Invalid credentials for: " + username);
            }
            return isValid;
        } catch (Exception e) {
            Log.e("TriviaApp", "Error validating user: " + e.getMessage());
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    private String getLastScore(String username) {
        Cursor cursor = null;
        try {
            String query = "SELECT score, timestamp FROM Scores WHERE username = ? ORDER BY id DESC LIMIT 1";
            cursor = database.rawQuery(query, new String[]{username});
            if (cursor.moveToFirst()) {
                @SuppressLint("Range") int score = cursor.getInt(cursor.getColumnIndex("score"));
                @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                return "Last Score: " + score + " on " + timestamp;
            } else {
                return "No score available.";
            }
        } catch (Exception e) {
            Log.e("TriviaApp", "Error fetching last score: " + e.getMessage());
            return "Error fetching score.";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
