package com.example.trivia;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView questionText;
    private TextView lastScoreText;
    private Button[] answerButtons;
    private ArrayList<Question> questions;
    private int currentQuestionIndex;
    private int correctAnswersCount;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("TriviaApp", "MainActivity started.");
        setContentView(R.layout.activity_main);

        lastScoreText = findViewById(R.id.lastScoreText);
        String lastScore = getIntent().getStringExtra("lastScore");
        if (lastScore != null) {
            lastScoreText.setText(lastScore);
        }

        questionText = findViewById(R.id.questionText);
        answerButtons = new Button[]{
                findViewById(R.id.btnAnswer1),
                findViewById(R.id.btnAnswer2),
                findViewById(R.id.btnAnswer3),
                findViewById(R.id.btnAnswer4)
        };

        correctAnswersCount = 0;

        try {
            database = openOrCreateDatabase("TriviaApp", MODE_PRIVATE, null);
            setupDatabase();
            loadQuestions();
            Collections.shuffle(questions);
            currentQuestionIndex = 0;

            Log.d("TriviaApp", "Questions loaded successfully.");
            displayQuestion();
        } catch (Exception e) {
            Log.e("TriviaApp", "Error in MainActivity: " + e.getMessage());
        }

        for (int i = 0; i < answerButtons.length; i++) {
            int finalI = i;
            answerButtons[i].setOnClickListener(v -> checkAnswer(finalI));
        }
    }

    private void setupDatabase() {

        database.execSQL("CREATE TABLE IF NOT EXISTS Users (id INTEGER PRIMARY KEY, username TEXT, password TEXT)");
        database.execSQL("CREATE TABLE IF NOT EXISTS Questions (id INTEGER PRIMARY KEY, questionText TEXT, option1 TEXT, option2 TEXT, option3 TEXT, option4 TEXT, correctOption INTEGER)");
        database.execSQL("CREATE TABLE IF NOT EXISTS Scores (id INTEGER PRIMARY KEY, username TEXT, score INTEGER, timestamp TEXT)");
    }

    private void loadQuestions() {
        questions = new ArrayList<>();
        Cursor cursor = null;

        try {
            cursor = database.rawQuery("SELECT * FROM Questions", null);

            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String questionText = cursor.getString(cursor.getColumnIndex("questionText"));
                    @SuppressLint("Range") String[] options = {
                            cursor.getString(cursor.getColumnIndex("option1")),
                            cursor.getString(cursor.getColumnIndex("option2")),
                            cursor.getString(cursor.getColumnIndex("option3")),
                            cursor.getString(cursor.getColumnIndex("option4"))
                    };
                    @SuppressLint("Range") int correctOption = cursor.getInt(cursor.getColumnIndex("correctOption"));

                    questions.add(new Question(questionText, options, correctOption));
                } while (cursor.moveToNext());
            } else {
                Log.d("TriviaApp", "No questions found in the database.");
            }
        } catch (Exception e) {
            Log.e("TriviaApp", "Error loading questions: " + e.getMessage());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void displayQuestion() {
        if (questions == null || questions.isEmpty() || currentQuestionIndex >= questions.size()) {
            questionText.setText("לא נמצאו שאלות במאגר.");
            for (Button button : answerButtons) {
                button.setVisibility(View.GONE);
            }
            return;
        }

        Question currentQuestion = questions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.questionText);

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(currentQuestion.options[i]);
            answerButtons[i].setVisibility(View.VISIBLE);
        }
    }

    private void checkAnswer(int selectedOption) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        if (selectedOption == currentQuestion.correctOptionIndex) {
            correctAnswersCount++;
        }

        moveToNextQuestion();
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;

        if (currentQuestionIndex < questions.size()) {
            displayQuestion();
        } else {
            showSummary();
        }
    }

    private void saveScore(String username, int score) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
            String query = "INSERT INTO Scores (username, score, timestamp) VALUES (?, ?, ?)";
            database.execSQL(query, new Object[]{username, score, timestamp});
            Log.d("TriviaApp", "Score saved for user: " + username + " at " + timestamp);
        } catch (Exception e) {
            Log.e("TriviaApp", "Error saving score: " + e.getMessage());
        }
    }

    private void showSummary() {
        String username = getIntent().getStringExtra("username");
        if (username == null) {
            username = "Maor Betser";
        }
        Log.d("TriviaApp", username);

        saveScore(username, correctAnswersCount);

        String summaryMessage = "החידון הסתיים!\nענית נכון על " + correctAnswersCount + " מתוך " + questions.size() + " שאלות!";
        questionText.setText(summaryMessage);

        for (Button button : answerButtons) {
            button.setVisibility(View.GONE);
        }
    }
}
