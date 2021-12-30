package com.example.friendsgame;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendsgame.temporary.DefeatScreen;
import com.example.friendsgame.temporary.LoadingScreen;
import com.example.friendsgame.temporary.VictoryScreen;

import java.util.Random;

public class DiceGame extends AppCompatActivity {

    public static int GAME_COUNT = MainActivity.GAME_COUNT;
    SensorEventListener sensorEventListener;
    ImageView dice;
    TextView texte, finalScore;
    boolean mouvement = false;
    int[] faces =
            {
                    R.drawable.dice1,
                    R.drawable.dice2,
                    R.drawable.dice3,
                    R.drawable.dice4,
                    R.drawable.dice5,
                    R.drawable.dice6,
            };

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private double accelerationCurrentValue = 0;
    private double accelerationPreviousValue = 0;

    private double changeInAcceleration = 0;
    private double limit = 10;
    int score = 0;
    int p = 4;
    int total = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dice_game);

        System.out.println("GAME_COUNT : " + MainActivity.GAME_COUNT);

        init();
    }

    public void init () {
        dice = (ImageView) findViewById(R.id.iv_dee);
        finalScore = findViewById(R.id.tv_tapScore);
        dice.setImageResource(R.drawable.dice6);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        SensorEventListener sensorEventListener = new SensorEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];
                float y = sensorEvent.values[1];
                float z = sensorEvent.values[2];
                accelerationCurrentValue = Math.sqrt((x * x + y * y + z * z));
                changeInAcceleration = Math.abs(accelerationCurrentValue - accelerationPreviousValue);
                accelerationPreviousValue = accelerationCurrentValue;
                if (changeInAcceleration > limit) {
                    //Log.i("bool","acceleration changed ");
                    mouvement = true;
                    Random generator = new Random();
                    score = generator.nextInt(faces.length);
                    if (dice != null) {
                        dice.setImageResource(faces[score]);
                    }
                    /*
                    Toast toast = Toast.makeText(getApplicationContext(), "SCORE :" + (score+1), Toast.LENGTH_SHORT);
                    toast.show();
                     */
                    //vibrer();
                } else {
                    if (changeInAcceleration < 0.1 && mouvement) {
                        try {
                            Context context = getApplicationContext();
                            score += 1;
                            mouvement = false;
                            Thread.sleep(200);
                            switch (p) {
                                case 1:
                                    texte = (TextView) findViewById(R.id.tv_value3);
                                    texte.setText(Integer.toString(score));
                                    total += score;
                                    Toast.makeText(getApplicationContext(), "SCORE :" + score, Toast.LENGTH_SHORT).show();
                                    p--;
                                    break;
                                case 2:
                                    texte = (TextView) findViewById(R.id.tv_value2);
                                    texte.setText(Integer.toString(score));
                                    total += score;
                                    Toast.makeText(getApplicationContext(), "SCORE :" + score, Toast.LENGTH_SHORT).show();
                                    p--;
                                    break;
                                case 3:
                                    texte = (TextView) findViewById(R.id.tv_value1);
                                    texte.setText(Integer.toString(score));
                                    total += score;
                                    Toast.makeText(getApplicationContext(), "SCORE :" + score, Toast.LENGTH_SHORT).show();
                                    p--;
                                    break;
                                    /*
                                case 4:
                                    p--;
                                    break;
                                     */
                                default:
                                    p = 3;
                                    break;

                            }
                            if (p == 0) {
                                mSensorManager.unregisterListener(this, mAccelerometer);
                                finalScore.setText("Final score: " + total);
                                //showScore(total);
                                MainActivity.GAME_COUNT--;
                                System.out.println("GAME_COUNT : " + MainActivity.GAME_COUNT);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        MainActivity.myScore += total;
                                        if (MainActivity.devicesConnected.size() == 0 ) {
                                            //On est tout seul
                                            if (MainActivity.GAME_COUNT != 0) {
                                                //Le jeu continue
                                                Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                                startActivity(loading);
                                                finish();
                                            } else {
                                                //Jeu terminé, on a forcément gagné
                                                MainActivity.reset();
                                                Intent victory = new Intent(getApplicationContext(), VictoryScreen.class);
                                                startActivity(victory);
                                                finish();
                                            }
                                        } else {
                                            if (MainActivity.GAME_COUNT != 0) {
                                                //le jeu continue, on envoie notre score et on lance le chargement
                                                String msg = "{ \"type\": \"game\", \"score\": "+ String.valueOf(total) +", \"name\": "+MainActivity.myName +"}";
                                                p = 4;
                                                score = 0;
                                                total = 0;
                                                MainActivity.sendReceive.write(msg.getBytes());
                                                Intent loading = new Intent(getApplicationContext(), LoadingScreen.class);
                                                startActivity(loading);
                                                finish();
                                            } else {
                                                /*
                                                C'est ici que le jeu est fini et on est plusieurs. On envoie nos score
                                                */
                                                MainActivity.finished = true;
                                                String msg = "{ \"type\": \"finished\", \"score\": "+ String.valueOf(total) +", \"name\": "+MainActivity.myName +" }";
                                                p = 4;
                                                score = 0;
                                                total = 0;
                                                MainActivity.sendReceive.write(msg.getBytes());
                                                if (MainActivity.allFinished()) {
                                                    MainActivity.determineRanking(getApplicationContext());
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
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
        mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onResume() {
        super.onResume();
        if (mSensorManager != null) {
            mSensorManager.registerListener(sensorEventListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    protected void onPause() {
        super.onPause();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(sensorEventListener);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void vibrer() {
        Vibrator v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
        v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    public void showScore(int value) {
        AlertDialog.Builder builder = new AlertDialog.Builder(DiceGame.this);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(Integer.toString(value))
                .setTitle("Final score:");

        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();
        finalScore.setText("Final score: " + value);
    }
}

