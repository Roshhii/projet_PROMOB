package com.example.friendsgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class LoadingScreen extends AppCompatActivity {

    TextView textLoading;
    public int game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_screen);

        textLoading = findViewById(R.id.tv_textLoading);
        textLoading.setMovementMethod(new ScrollingMovementMethod());
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("text/loading.txt")));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            textLoading.setText(text);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (MainActivity.GAME_COUNT) {
                    case 2 :
                        game = MainActivity.table_games[1];
                        break;
                    case 1 :
                        game = MainActivity.table_games[2];
                        break;
                }
                switch (game) {
                    case 1 :
                        Intent diceActivity = new Intent(getApplicationContext(), DiceGame.class);
                        startActivity(diceActivity);
                        finish();
                        break;
                    case 2 :
                        Intent tapActivity = new Intent(getApplicationContext(), TapGame.class);
                        startActivity(tapActivity);
                        finish();
                        break;
                }
            }
        }, 5000);

    }


}