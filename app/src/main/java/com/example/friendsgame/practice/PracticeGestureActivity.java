package com.example.friendsgame.practice;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendsgame.GestureGame;
import com.example.friendsgame.PracticeActivity;
import com.example.friendsgame.R;

import java.util.Locale;
import java.util.Random;
import java.util.Timer;

public class PracticeGestureActivity extends Activity implements
        GestureDetector.OnDoubleTapListener,
        GestureDetector.OnGestureListener {

    private GestureDetector mGestureDetector;

    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    private static final int SWIPE_THRESHOLD = 100;

    private TextView tvGesture, tvScore, tvFinalScore, titleGesture;
    private ImageView gestureBack, gestureAgain;

    String[] mouvements = {
            "Swipe up",
            "Swipe right",
            "Swipe down",
            "Swipe left",
            "Single tap",
            "Double tap"
    };

    private static final long start_time_millis=10000;
    private boolean mTimerRunning=true ;
    private long mTimeLeft = start_time_millis ;
    private CountDownTimer mCountDown;
    private int count = -1;
    private int points = 0 ;
    int task ;
    String done ;
    private boolean end=false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gesture_game);
        mGestureDetector = new GestureDetector(this, this);
        mGestureDetector.setOnDoubleTapListener(this);

        tvGesture = findViewById(R.id.tv_gesture);
        tvScore = findViewById(R.id.tv_timeGesture);
        tvFinalScore = findViewById(R.id.tv_gestureScore);
        titleGesture = findViewById(R.id.tv_titleGesture);
        gestureAgain = findViewById(R.id.iv_gestureAgain);
        gestureBack = findViewById(R.id.iv_gestureBack);

        gestureBack.setImageResource(R.drawable.arrow);
        gestureAgain.setImageResource(R.drawable.replay);

        Typeface audiowide = Typeface.createFromAsset(getAssets(),
                "fonts/audiowide.ttf");
        Typeface bungee_shade = Typeface.createFromAsset(getAssets(),
                "fonts/bungee_shade.ttf");
        titleGesture.setTypeface(bungee_shade);
        tvFinalScore.setTypeface(audiowide);

        tvFinalScore.setVisibility(View.VISIBLE);


        gestureBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), PracticeActivity.class);
                startActivity(main);
                finish();
            }
        });

        gestureAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(getApplicationContext(), PracticeGestureActivity.class);
                startActivity(game);
                finish();
            }
        });

        if(count==-1) {
            startTime();
            count=0;
        }
        if(mTimerRunning) {
            count++;
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
                tvGesture.setVisibility(View.VISIBLE);
                mTimerRunning=false ;
                tvScore.setText(Integer.toString(points));
                Random generator = new Random();

                Timer t = new java.util.Timer();
                t.schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                task=generator.nextInt(mouvements.length);
                                tvGesture.setText(mouvements[task]);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if(done==mouvements[task]){
                                    System.out.println(+1);
                                    points++;
                                    tvScore.setText(Integer.toString(points));
                                }
                                else{
                                    tvGesture.setText("You lost...");
                                    //showScore(points);
                                    //je n'arrive pas à afficher le score ici
                                    t.cancel();

                                    end = true;

                                    tvFinalScore.setText("Final score: " + points);

                                }
                            }
                        }, 1000,1000
                );
            }
        }.start();

    }
    public void updateCountdownText (){
        int minutes = (int) mTimeLeft/1000/60 ;
        int seconds = (int) (mTimeLeft/1000)%60 ;
        String TimeString = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        tvScore.setText(TimeString);
    }
    public void showToast(){
        Context context = getApplicationContext();
        CharSequence text = "Les mouvements à realiser s'afficheront en bas";
        int duration = Toast.LENGTH_LONG ;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();


    }
    public void showScore(int value){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(Integer.toString(value))
                .setTitle("Score final");

        // 3. Get the <code><a href="/reference/android/app/AlertDialog.html">AlertDialog</a></code> from <code><a href="/reference/android/app/AlertDialog.Builder.html#create()">create()</a></code>
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    //fonctions pour les mouvements

    @Override
    public boolean onTouchEvent(MotionEvent event){
        mGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        //pas besoin
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float diffY = e2.getY() - e1.getY();
        float diffX = e2.getX() - e1.getX();
        if (Math.abs(diffX) > Math.abs(diffY)) {
            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffX > 0) {
                    onSwipeRight();
                } else {
                    onSwipeLeft();
                }
            }
        } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
            if (diffY > 0) {
                onSwipeBottom();
            } else {
                onSwipeTop();
            }
        }
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        //Pas besoin
    }



    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        /*Log.d("GestureDetector","onScroll");
        text.setText("onScroll");*/
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        //pas besoin
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        //pas besoin
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        //pas besoin
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        Log.d("GestureDetector","onDoubleTapEvent");
        done= "Double tap";
        //text.setText("onDoubleTapEvent");
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        Log.d("GestureDetector","onSingleTapConfirmed");
        done="Single tap";
        //text.setText("onSingleTapConfirmed");
        return true;
    }

    public void onSwipeRight() {
        Log.d("GestureDetector","onSingleTapConfirmed");
        done="Swipe right";
        //text.setText("swipRight");
    }

    public void onSwipeLeft() {
        Log.d("GestureDetector","onSingleTapConfirmed");
        done="Swipe left";
        //text.setText("swipLeft");
    }

    public void onSwipeTop() {
        Log.d("GestureDetector","onSingleTapConfirmed");
        done="Swipe up";
        //text.setText("swipTop");
    }

    public void onSwipeBottom() {
        Log.d("GestureDetector","onSingleTapConfirmed");
        done="Swipe down";
        //text.setText("swipBottom");
    }
}

