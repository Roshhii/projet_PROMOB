package com.example.friendsgame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.friendsgame.practice.PracticeBuzzerActivity;
import com.example.friendsgame.practice.PracticeDiceActivity;
import com.example.friendsgame.practice.PracticeGestureActivity;
import com.example.friendsgame.practice.PracticeMathActivity;

public class PracticeActivity extends AppCompatActivity {

    CardView cvDice, cvMath, cvBuzzer, cvGesture;
    ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_activity);

        cvBuzzer = findViewById(R.id.cv_practiceTap);
        cvDice = findViewById(R.id.cv_practiceDice);
        cvGesture = findViewById(R.id.cv_practiceGesture);
        cvMath = findViewById(R.id.cv_practiceMath);
        back = findViewById(R.id.iv_practiceBack);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });

        cvDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(getApplicationContext(), PracticeDiceActivity.class);
                startActivity(game);
                finish();
            }
        });
        cvGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(getApplicationContext(), PracticeGestureActivity.class);
                startActivity(game);
                finish();
            }
        });
        cvMath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(getApplicationContext(), PracticeMathActivity.class);
                startActivity(game);
                finish();
            }
        });
        cvBuzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent game = new Intent(getApplicationContext(), PracticeBuzzerActivity.class);
                startActivity(game);
                finish();
            }
        });
    }

}
