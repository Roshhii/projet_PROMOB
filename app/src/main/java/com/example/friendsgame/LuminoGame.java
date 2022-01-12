package com.example.friendsgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;

import com.example.friendsgame.temporary.FinishedScreen;
import com.example.friendsgame.temporary.LoadingScreen;
import com.example.friendsgame.temporary.VictoryScreen;

import java.util.Locale;


public class LuminoGame extends AppCompatActivity {

    private static final long start_time_millis=13000;
    private boolean mTimerRunning=true ;
    private long mTimeLeft = start_time_millis ;
    private CountDownTimer mCountDown;
    private int count = 0;
    TextView titleLumino, timeLumino, maxLumino, lumino;
    double lmax = 0;
    double l ;
    Sensor mlum ;
    private SensorManager mSensorManager;
    private SensorEventListener Listener ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lumino_game);

        titleLumino = findViewById(R.id.tv_titleLumino);
        timeLumino = findViewById(R.id.tv_Timelumino);
        maxLumino = findViewById(R.id.tv_Maxlumino);
        lumino = findViewById(R.id.tv_lumino);

        Typeface bungee_shade = Typeface.createFromAsset(getAssets(),
                "fonts/bungee_shade.ttf");
        titleLumino.setTypeface(bungee_shade);

        mSensorManager=(SensorManager) getSystemService(SENSOR_SERVICE) ;
        mlum = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        SensorEventListener sensorEventListener=new SensorEventListener(){
            @Override
            public void onSensorChanged(SensorEvent sensorEvent){
                l = sensorEvent.values[0]/100;
                l = (double) Math.round(l * 100) / 100;
                System.out.println("Luminosité " + l);
                lumino.setText(Double.toString(l));
                changeValue(l);
            }
            @Override
            public void onAccuracyChanged(Sensor sensor,int i ){

            }
        };
        Listener = sensorEventListener ;
        mlum = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);


        mSensorManager.registerListener(sensorEventListener,mlum,SensorManager.SENSOR_DELAY_FASTEST);
        if(count==0) {
            startTime();
        }
        if(mTimerRunning) {
            count++;
        }
        else{

        }
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
                mSensorManager.unregisterListener(Listener,mlum);
                //Comparer lmin de chaque utilisateur
                MainActivity.GAME_COUNT--;
                //showScore(count);
                new Handler().postDelayed(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void run() {
                        MainActivity.myScore += (int) lmax;
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
                                String msg = "{ \"type\": \"game\", \"score\": "+ String.valueOf((int) lmax) +", \"name\": "+MainActivity.myName +" }";
                                MainActivity.sendReceive.write(msg.getBytes());
                                Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                startActivity(loading);
                                finish();
                            } else {
                                //Jeu terminé et on est plusieurs
                                MainActivity.finished = true;
                                String msg = "{ \"type\": \"finished\", \"score\": "+ String.valueOf((int) lmax) +", \"name\": "+MainActivity.myName +" }";
                                MainActivity.sendReceive.write(msg.getBytes());
                                if (MainActivity.allFinished()) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                        MainActivity.determineRanking(getApplicationContext());
                                    }
                                    Intent finished = new Intent(getApplicationContext(), FinishedScreen.class);
                                    startActivity(finished);
                                    finish();
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
        if (seconds <= 10) {
            String TimeString = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
            timeLumino.setText(TimeString);
        }
    }

    public void changeValue(double l){
        if(l > lmax) {
            lmax = l;
            maxLumino.setText("Maximum brightness: " + lmax );
        }

    }
}
