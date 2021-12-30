package com.example.friendsgame;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.friendsgame.temporary.DefeatScreen;
import com.example.friendsgame.temporary.LoadingScreen;
import com.example.friendsgame.temporary.VictoryScreen;

import java.util.Locale;
import java.util.Random;

public class MathGame extends AppCompatActivity {

    private static final long start_time_millis=10000; //Mettre à 45000 pour le vrai jeu
    private TextView tvTime, tvCalculation, tvAnswer;
    private EditText etAnswer;
    private Button btFinished;
    private int userAnswer, answer, score;
    private boolean mTimerRunning = true ;
    private long mTimeLeft = start_time_millis;
    private CountDownTimer mCountDown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.math_game);

        System.out.println("GAME_COUNT : " + MainActivity.GAME_COUNT);

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
                           MainActivity.GAME_COUNT--;
                           tvAnswer.setText("= " + answer);
                           new Handler().postDelayed(new Runnable() {
                               @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                               @Override
                               public void run() {
                                   Toast.makeText(getApplicationContext(), "Congratulations!", Toast.LENGTH_SHORT);
                                   MainActivity.myScore += 5;
                                   if (MainActivity.devicesConnected.size() == 0 ) {
                                       if (MainActivity.GAME_COUNT != 0) {
                                           Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                           startActivity(loading);
                                       } else {
                                           //Jeu terminé
                                           MainActivity.reset();
                                           Intent victory = new Intent(getApplicationContext(), VictoryScreen.class);
                                           startActivity(victory);
                                       }
                                       finish();
                                   } else {
                                       if (MainActivity.GAME_COUNT != 0) {
                                           String msg = "{ \"type\": \"game\", \"score\": "+ String.valueOf(5) +", \"name\": "+MainActivity.myName +" }";
                                           MainActivity.sendReceive.write(msg.getBytes());
                                           Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                           startActivity(loading);
                                           finish();
                                       } else {
                                           //Jeu terminé et on est plusieurs
                                           MainActivity.finished = true;
                                           String msg = "{ \"type\": \"finished\", \"score\": "+ String.valueOf(5) +", \"name\": "+MainActivity.myName +" }";
                                           MainActivity.sendReceive.write(msg.getBytes());
                                           if (MainActivity.allFinished()) {
                                               if (MainActivity.determineWinner()) {
                                                   Intent defeat = new Intent(getApplicationContext(), DefeatScreen.class);
                                                   startActivity(defeat);
                                                   finish();
                                               } else {
                                                   Intent victory = new Intent(getApplicationContext(), VictoryScreen.class);
                                                   startActivity(victory);
                                                   finish();
                                               }
                                               MainActivity.reset();
                                           }
                                       }
                                   }
                               }
                           }, 5000);
                       } else {
                           Toast.makeText(getApplicationContext(), "Wrong answer. Try again", Toast.LENGTH_SHORT);
                       }
                   } catch (NumberFormatException e) {
                       Toast.makeText(getApplicationContext(), "Please give a number", Toast.LENGTH_SHORT);
                       Log.i("", rep + " is not a number");
                   }
               }
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
                //showScore(count);
                new Handler().postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        MainActivity.myScore += 0;
                        if (userAnswer == answer) {
                            MainActivity.myScore += 5;
                            score = 5;
                        } else {
                            MainActivity.myScore += 0;
                            score = 0;
                        }
                        if (MainActivity.devicesConnected.size() == 0 ) {
                            if (MainActivity.GAME_COUNT != 0) {
                                Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                startActivity(loading);
                            } else {
                                //Jeu terminé
                                Intent victory = new Intent(getApplicationContext(), VictoryScreen.class);
                                startActivity(victory);
                            }
                            finish();
                        } else {
                            if (MainActivity.GAME_COUNT != 0) {
                                String msg = "{ \"type\": \"game\", \"score\": "+ String.valueOf(score) +", \"name\": "+MainActivity.myName +" }";
                                MainActivity.sendReceive.write(msg.getBytes());
                                Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                startActivity(loading);
                                finish();
                            } else {
                                //Jeu terminé et on est plusieurs
                                MainActivity.finished = true;
                                String msg = "{ \"type\": \"finished\", \"score\": "+ String.valueOf(score) +", \"name\": "+MainActivity.myName +" }";
                                MainActivity.sendReceive.write(msg.getBytes());
                                if (MainActivity.allFinished()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        MainActivity.determineRanking(getApplicationContext());
                                    }
                                    if (MainActivity.determineWinner()) {
                                        Intent defeat = new Intent(getApplicationContext(), DefeatScreen.class);
                                        startActivity(defeat);
                                        finish();
                                    } else {
                                        Intent victory = new Intent(getApplicationContext(), VictoryScreen.class);
                                        startActivity(victory);
                                        finish();
                                    }
                                    MainActivity.reset();
                                }
                            }
                        }
                    }
                }, 5000);

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
