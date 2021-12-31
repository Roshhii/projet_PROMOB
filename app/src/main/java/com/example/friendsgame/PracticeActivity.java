package com.example.friendsgame;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
                finish();
            }
        });

        cvDice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cvGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cvMath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        cvBuzzer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

}
