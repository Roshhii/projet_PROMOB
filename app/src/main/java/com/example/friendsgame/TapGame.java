package com.example.friendsgame;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;
import java.util.Random;

public class TapGame extends AppCompatActivity {

    private static final long start_time_millis=30000;
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
                //showScore(count);
                MainActivity.GAME_COUNT--;
                System.out.println(MainActivity.GAME_COUNT);
                MainActivity.game = randomGame(1, 2);
                if (MainActivity.devicesConnected.size() == 0 && MainActivity.GAME_COUNT != 0) {
                    Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                    startActivity(loading);
                    finish();
                } else {
                    if (MainActivity.GAME_COUNT != 0) {
                        Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                        startActivity(loading);
                        finish();
                    } else {
                        /*
                        C'est ici que le jeu est fini
                        */
                        /*
                        if (MainActivity.devicesConnected.size() != 0) {
                            String msg = "{ \"type\": \"tap\", \"score\": "+ String.valueOf(count) +"  }";
                            MainActivity.sendReceive.write(msg.getBytes());
                        }
                        */
                    }
                }
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

    public int randomGame(int borneInf, int borneSup) {
        Random rand = new Random();
        int nb = rand.nextInt(borneSup - borneInf + 1) + borneInf;
        return nb;
    }
}
