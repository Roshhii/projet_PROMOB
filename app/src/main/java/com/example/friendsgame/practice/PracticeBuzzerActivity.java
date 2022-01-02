package com.example.friendsgame.practice;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
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

public class PracticeBuzzerActivity extends AppCompatActivity {

    private static final long start_time_millis=10000; //Mettre à 30000 pour le vrai jeu
    private TextView time, score, textScore;
    private boolean mTimerRunning = true ;
    private long mTimeLeft = start_time_millis;
    private ImageButton buzzer;
    private ImageView buzzerBack, buzzerAgain;
    private CountDownTimer mCountDown;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_game);

        init();
        listeners();
    }

    public void init () {
        time = (TextView) findViewById(R.id.tv_time);
        score = (TextView) findViewById(R.id.tv_tapScore);
        buzzer = (ImageButton) findViewById(R.id.iv_buzzer);
        textScore = (TextView) findViewById(R.id.tv_tapTextScore);
        buzzerAgain = findViewById(R.id.iv_buzzerAgain);
        buzzerBack = findViewById(R.id.iv_buzzerBack);

        buzzerBack.setVisibility(View.VISIBLE);
        buzzerAgain.setVisibility(View.VISIBLE);

        showToast();
    }

    public void listeners () {
        buzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(count==0) {
                    startTime();
                }
                if(mTimerRunning) {
                    count++;
                    score.setText(Integer.toString(count));
                }
                else{
                }
            }
        });
        buzzerBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), PracticeActivity.class);
                startActivity(main);
                finish();
            }
        });

        buzzerAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(getApplicationContext(), PracticeBuzzerActivity.class);
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
                score.setText(Integer.toString(count));
                textScore.setText("Final score:");
                //showScore(count);
            }
        }.start();
    }

    public void updateCountdownText (){
        int minutes = (int) mTimeLeft/1000/60 ;
        int seconds = (int) (mTimeLeft/1000)%60 ;
        String TimeString = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        time.setText(TimeString);
    }

    public void showToast(){
        CharSequence text = "Press as many times as possible!";
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    public void showScore(int value){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Integer.toString(value)).setTitle("Final score:");
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
