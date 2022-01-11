package com.example.friendsgame.practice;

import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.friendsgame.PracticeActivity;
import com.example.friendsgame.R;

import java.util.Locale;

public class PracticeLuminoActivity extends AppCompatActivity {

    private static final long start_time_millis=13000;
    private boolean mTimerRunning=true ;
    private long mTimeLeft = start_time_millis ;
    private CountDownTimer mCountDown;
    private int count = 0;
    private ImageView luminoBack, luminoAgain;
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
        luminoAgain = findViewById(R.id.iv_luminoAgain);
        luminoBack = findViewById(R.id.iv_luminoBack);

        luminoAgain.setImageResource(R.drawable.replay);
        luminoBack.setImageResource(R.drawable.arrow);

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

        luminoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), PracticeActivity.class);
                startActivity(main);
                finish();
            }
        });

        luminoAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(getApplicationContext(), PracticeLuminoActivity.class);
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
                mSensorManager.unregisterListener(Listener,mlum);
                //Comparer lmin de chaque utilisateur


            }
        }.start();
    }
    public void updateCountdownText (){
        int minutes = (int) mTimeLeft/1000/60 ;
        int seconds = (int) (mTimeLeft/1000)%60;
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
