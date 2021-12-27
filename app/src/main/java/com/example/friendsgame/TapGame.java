package com.example.friendsgame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class TapGame extends AppCompatActivity {

    private static final long start_time_millis=10000; //Mettre à 30000 pour le vrai jeu
    private TextView time, score, textScore;
    private boolean mTimerRunning = true ;
    private long mTimeLeft = start_time_millis;
    private ImageButton buzzer;
    private CountDownTimer mCountDown;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tap_game);

        System.out.println("MyName : " + MainActivity.myName);

        init();
        listeners();
    }

    public void init () {
        time = (TextView) findViewById(R.id.tv_time);
        score = (TextView) findViewById(R.id.tv_tapScore);
        buzzer = (ImageButton) findViewById(R.id.iv_buzzer);
        textScore = (TextView) findViewById(R.id.tv_tapTextScore);
        showToast();
        System.out.println("GAME_COUNT : " + MainActivity.GAME_COUNT);
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
                MainActivity.GAME_COUNT--;
                //showScore(count);
                new Handler().postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        MainActivity.myScore += count;
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
                                String msg = "{ \"type\": \"game\", \"score\": "+ String.valueOf(count) +", \"name\": "+MainActivity.myName +" }";
                                MainActivity.sendReceive.write(msg.getBytes());
                                Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                startActivity(loading);
                                finish();
                            } else {
                                //Jeu terminé et on est plusieurs
                                String msg = "{ \"type\": \"finished\", \"score\": "+ String.valueOf(count) +", \"name\": "+MainActivity.myName +" }";
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
