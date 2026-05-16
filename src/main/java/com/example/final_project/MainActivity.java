package com.example.final_project;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // UI элементы
    private TextView questionText, scoreText, livesText, correctAnswerHint;
    private EditText answerInput;
    private Button checkButton, newGameButton;

    // Логика игры
    private int currentScore = 0;
    private int currentLives = 3;
    private int currentAnswer = 0;
    private int currentLevel = 1; // Уровень сложности (1, 2, 3)
    private Random random = new Random();

    // Уровни сложности
    private final int[][] LEVEL_RANGES = {
            {1, 10},   // Уровень 1: числа до 10
            {1, 25},   // Уровень 2: числа до 25
            {10, 99}   // Уровень 3: числа до 99
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Связываем переменные с экраном
        questionText = findViewById(R.id.question_text);
        scoreText = findViewById(R.id.score_text);
        livesText = findViewById(R.id.lives_text);
        correctAnswerHint = findViewById(R.id.correct_answer_hint);
        answerInput = findViewById(R.id.answer_input);
        checkButton = findViewById(R.id.check_button);
        newGameButton = findViewById(R.id.new_game_button);

        // Настройка кнопок
        setupLevelButtons();

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        startNewGame(); // Инициализация первой игры
    }

    private void setupLevelButtons() {
        Button level1 = findViewById(R.id.level_1);
        Button level2 = findViewById(R.id.level_2);
        Button level3 = findViewById(R.id.level_3);

        level1.setOnClickListener(v -> { setLevel(1); });
        level2.setOnClickListener(v -> { setLevel(2); });
        level3.setOnClickListener(v -> { setLevel(3); });
    }

    private void setLevel(int level) {
        currentLevel = level;
        startNewGame();
        Toast.makeText(this, "Уровень " + level + " выбран", Toast.LENGTH_SHORT).show();
    }

    private void startNewGame() {
        currentScore = 0;
        currentLives = 3;
        updateUI();
        correctAnswerHint.setText("");
        generateNewQuestion();
    }

    private void generateNewQuestion() {
        int min = LEVEL_RANGES[currentLevel-1][0];
        int max = LEVEL_RANGES[currentLevel-1][1];

        // Генерация первого и второго числа
        int num1 = random.nextInt((max - min) + 1) + min;
        int num2 = random.nextInt((max - min) + 1) + min;

        // 0 = +, 1 = -
        boolean isAddition = random.nextBoolean();
        String operator = isAddition ? "+" : "-";

        if (isAddition) {
            currentAnswer = num1 + num2;
        } else {
            if (num1 < num2) {
                int temp = num1;
                num1 = num2;
                num2 = temp;
            }
            currentAnswer = num1 - num2;
        }

        questionText.setText(num1 + " " + operator + " " + num2 + " = ?");
        answerInput.setText(""); // Очищаем поле ввода
    }

    private void checkAnswer() {
        String userInput = answerInput.getText().toString();
        if (userInput.isEmpty()) {
            Toast.makeText(this, "Введите ответ!", Toast.LENGTH_SHORT).show();
            return;
        }

        int userAnswer = Integer.parseInt(userInput);

        if (userAnswer == currentAnswer) {
            // Правильно
            currentScore++;
            correctAnswerHint.setText("✅ Верно!");
            generateNewQuestion();
        } else {
            // Неправильно
            currentLives--;
            correctAnswerHint.setText("❌ Неверно! Правильно: " + currentAnswer);

            if (currentLives == 0) {
                // Игра проиграна
                gameOver();
                return;
            } else {
                generateNewQuestion(); // Новый вопрос, но жизни убавились
            }
        }
        updateUI();
    }

    private void gameOver() {
        Toast.makeText(this, "Игра окончена! Твой счет: " + currentScore, Toast.LENGTH_LONG).show();
        questionText.setText("ИГРА ОКОНЧЕНА");
        checkButton.setEnabled(false); // блок кнопки проверки
        correctAnswerHint.setText("Нажмите 'Новая игра'");
    }

    private void updateUI() {
        scoreText.setText("Счет: " + currentScore);
        livesText.setText("Жизни: " + currentLives);

        // Если игра активна, разблокируем кнопку
        if (currentLives > 0 && !questionText.getText().toString().equals("ИГРА ОКОНЧЕНА")) {
            checkButton.setEnabled(true);
        }
    }
}