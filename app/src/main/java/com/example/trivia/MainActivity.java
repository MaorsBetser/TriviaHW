package com.example.trivia;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private TextView questionText;
    private Button[] answerButtons;
    private ArrayList<Question> questions;
    private int currentQuestionIndex;
    private int correctAnswersCount; // Counter for correct answers

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        questionText = findViewById(R.id.questionText);
        answerButtons = new Button[]{
                findViewById(R.id.btnAnswer1),
                findViewById(R.id.btnAnswer2),
                findViewById(R.id.btnAnswer3),
                findViewById(R.id.btnAnswer4)
        };

        // Initialize variables
        correctAnswersCount = 0;

        // Load questions
        loadQuestions();
        Collections.shuffle(questions);
        currentQuestionIndex = 0;

        // Display the first question
        displayQuestion();

        // Set click listeners for buttons
        for (int i = 0; i < answerButtons.length; i++) {
            int finalI = i;
            answerButtons[i].setOnClickListener(v -> checkAnswer(finalI));
        }
    }

    private void loadQuestions() {
        // Add 10 questions to the list
        questions = new ArrayList<>();
        questions.add(new Question("מהו הבירה של ישראל?",
                new String[]{"ירושלים", "תל אביב", "חיפה", "באר שבע"}, 0));
        questions.add(new Question("מי היה הנשיא הראשון של מדינת ישראל?",
                new String[]{"חיים ויצמן", "דוד בן גוריון", "יצחק רבין", "שמעון פרס"}, 0));
        questions.add(new Question("כמה ימים יש בשבוע?",
                new String[]{"5", "6", "7", "8"}, 2));
        questions.add(new Question("איזו עיר ידועה בתור 'העיר הלבנה'?",
                new String[]{"חיפה", "תל אביב", "ירושלים", "אילת"}, 1));
        questions.add(new Question("מי כתב את הספר 'הארי פוטר'?",
                new String[]{"ג'.ק. רולינג", "טולקין", "שייקספיר", "המינגוויי"}, 0));
        questions.add(new Question("איזה ים נמצא ממזרח לישראל?",
                new String[]{"הים התיכון", "הים השחור", "ים המלח", "הים האדום"}, 2));
        questions.add(new Question("כמה צבעים יש בקשת בענן?",
                new String[]{"5", "6", "7", "8"}, 2));
        questions.add(new Question("מהי החיה הגדולה ביותר בעולם?",
                new String[]{"הפיל", "הלוויתן הכחול", "הכריש הלבן", "הג'ירפה"}, 1));
        questions.add(new Question("מהי בירת צרפת?",
                new String[]{"רומא", "ברלין", "פריז", "מדריד"}, 2));
        questions.add(new Question("איזה כוכב לכת ידוע בתור 'כוכב הלכת האדום'?",
                new String[]{"כדור הארץ", "יופיטר", "מאדים", "נוגה"}, 2));
    }

    private void displayQuestion() {
        // Show the current question
        Question currentQuestion = questions.get(currentQuestionIndex);
        questionText.setText(currentQuestion.questionText);

        for (int i = 0; i < answerButtons.length; i++) {
            answerButtons[i].setText(currentQuestion.options[i]);
            answerButtons[i].setVisibility(View.VISIBLE); // Ensure buttons are visible
        }
    }

    private void checkAnswer(int selectedOption) {
        Question currentQuestion = questions.get(currentQuestionIndex);

        if (selectedOption == currentQuestion.correctOptionIndex) {
            correctAnswersCount++; // Increment correct answers count
        }

        moveToNextQuestion();
    }

    private void moveToNextQuestion() {
        currentQuestionIndex++;

        if (currentQuestionIndex < questions.size()) {
            displayQuestion();
        } else {
            // Show summary in the UI
            showSummary();
        }
    }

    private void showSummary() {
        // Display the summary in the TextView
        String summaryMessage = "החידון הסתיים!\nענית נכון על " + correctAnswersCount + " מתוך " + questions.size() + " שאלות!";
        questionText.setText(summaryMessage);

        // Hide the answer buttons
        for (Button button : answerButtons) {
            button.setVisibility(View.GONE);
        }
    }
}
