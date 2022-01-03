package com.example.friendsgame.practice;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.friendsgame.MainActivity;
import com.example.friendsgame.PracticeActivity;
import com.example.friendsgame.R;
import com.example.friendsgame.temporary.FinishedScreen;
import com.example.friendsgame.temporary.LoadingScreen;
import com.example.friendsgame.temporary.VictoryScreen;

import java.util.Locale;
import java.util.Random;

public class PracticeMathActivity extends AppCompatActivity {

    private static final long start_time_millis=10000; //Mettre à 45000 pour le vrai jeu
    private TextView tvTime, tvCalculation, tvAnswer;
    private EditText etAnswer;
    private Button btFinished;
    private ImageView mathBack, mathAgain;
    private int userAnswer, answer, score;
    private boolean mTimerRunning = true ;
    private long mTimeLeft = start_time_millis;
    private CountDownTimer mCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.math_game);

        init();
        listeners();

        startTime();


    }

    public void init () {
        tvTime = (TextView) findViewById(R.id.tv_timeMath);
        btFinished = findViewById(R.id.bt_mathFinish);
        tvCalculation = findViewById(R.id.tv_calculation);
        tvAnswer = findViewById(R.id.tv_AnswerCalculation);
        etAnswer = findViewById(R.id.et_answerCalculation);
        mathAgain = findViewById(R.id.iv_mathAgain);
        mathBack = findViewById(R.id.iv_mathBack);

        mathBack.setVisibility(View.VISIBLE);
        mathAgain.setVisibility(View.VISIBLE);

        showToast();
        randomCalculation(10, 100);

    }

    public void listeners () {
        btFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rep = etAnswer.getText().toString();
                if (rep.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please give an answer", Toast.LENGTH_SHORT);
                } else {
                    try {
                        userAnswer = Integer.parseInt(rep);
                        if (userAnswer == answer) {
                            tvAnswer.setText("= " + answer);
                            Toast.makeText(getApplicationContext(), "Congratulations!", Toast.LENGTH_SHORT);
                        } else {
                            Toast.makeText(getApplicationContext(), "Wrong answer. Try again", Toast.LENGTH_SHORT);
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(getApplicationContext(), "Please give a number", Toast.LENGTH_SHORT);
                        Log.i("ERROR INPUT MATHGAME", rep + " is not a number");
                    }
                }
            }
        });

        mathBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), PracticeActivity.class);
                startActivity(main);
                finish();
            }
        });

        mathAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(getApplicationContext(), PracticeMathActivity.class);
                startActivity(game);
                finish();
            }
        });
    }

    public void startTime(){
        mCountDown = new CountDownTimer(mTimeLeft,1000){
            @Override
            public void onTick(long l) {
                mTimeLeft=l ;
                updateCountdownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning=false ;
                MainActivity.GAME_COUNT--;
                tvAnswer.setText("= " + answer);
            }
        }.start();
    }

    public void updateCountdownText (){
        int minutes = (int) mTimeLeft/1000/60 ;
        int seconds = (int) (mTimeLeft/1000)%60 ;
        String TimeString = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        tvTime.setText(TimeString);
    }

    public void showToast(){
        CharSequence text = "Good luck!";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void randomCalculation(int borneInf, int borneSup) {
        Random rand = new Random();
        int nb1 = rand.nextInt(borneSup - borneInf + 1) + borneInf;
        int nb2 = rand.nextInt(borneSup - borneInf + 1) + borneInf;
        answer = nb1 * nb2;
        tvCalculation.setText(nb1 + " x " + nb2);
    }
}

