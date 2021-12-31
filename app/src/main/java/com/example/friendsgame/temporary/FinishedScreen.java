package com.example.friendsgame.temporary;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.friendsgame.MainActivity;
import com.example.friendsgame.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class FinishedScreen extends AppCompatActivity {

    TextView textFinished;
    Button btFinished;
    private MediaPlayer mediaPlayer;

    ListView ranking;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finished_screen);

        textFinished = findViewById(R.id.tv_finishedScreen);
        btFinished = findViewById(R.id.bt_finishedScreen);
        ranking = findViewById(R.id.lv_ranking);
        textFinished.setMovementMethod(new ScrollingMovementMethod());

        if (MainActivity.determineWinner()) {
            this.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.defeat);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("text/defeat.txt")));
                StringBuilder text = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                textFinished.setText(text);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        } else {
            this.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.victoire1);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("text/victory.txt")));
                StringBuilder text = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                textFinished.setText(text);
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }

        mediaPlayer.start();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ranking.setAdapter(MainActivity.determineRanking(getApplicationContext()));
        }

        MainActivity.reset();

        btFinished.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }
}

