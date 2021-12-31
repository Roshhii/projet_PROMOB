package com.example.friendsgame.temporary;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.friendsgame.MainActivity;
import com.example.friendsgame.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DefeatScreen extends AppCompatActivity {

    TextView textVictory;
    Button btFinish;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.defeat_screen);

        this.mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.defeat);

        textVictory = findViewById(R.id.tv_victory);
        btFinish = findViewById(R.id.bt_finish);
        textVictory.setMovementMethod(new ScrollingMovementMethod());
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("text/defeat.txt")));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            textVictory.setText(text);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        mediaPlayer.start();

        btFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }
}
