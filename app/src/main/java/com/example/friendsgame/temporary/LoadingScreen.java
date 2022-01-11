package com.example.friendsgame.temporary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;

import com.example.friendsgame.DiceGame;
import com.example.friendsgame.GestureGame;
import com.example.friendsgame.LuminoGame;
import com.example.friendsgame.MainActivity;
import com.example.friendsgame.MathGame;
import com.example.friendsgame.R;
import com.example.friendsgame.TapGame;

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
                    /*
                    case 6 :
                        game = MainActivity.table_games[0];
                        break;
                    case 5 :
                        game = MainActivity.table_games[1];
                        break;
                    case 4 :
                        game = MainActivity.table_games[2];
                        break;
                     */
                    case 3 :
                        game = MainActivity.table_games[0];
                        break;
                    case 2 :
                        game = MainActivity.table_games[1];
                        break;
                    case 1 :
                        game = MainActivity.table_games[2];
                        break;
                }
                switch (game) {
                    /*
                    1 : DiceGame
                    2 : GestureGame
                    3 : TapGame
                    4 : MathGame
                    5 : LuminoGame
                     */
                    case 1 :
                        Intent diceActivity = new Intent(getApplicationContext(), DiceGame.class);
                        startActivity(diceActivity);
                        finish();
                        break;
                    case 2 :
                        /*Intent gestureActivity = new Intent(getApplicationContext(), GestureGame.class);
                        startActivity(gestureActivity);
                        finish();
                        break;*/
                    case 3 :
                        Intent tapActivity = new Intent(getApplicationContext(), TapGame.class);
                        startActivity(tapActivity);
                        finish();
                        break;
                    case 4 :
                        Intent mathActivity = new Intent(getApplicationContext(), MathGame.class);
                        startActivity(mathActivity);
                        finish();
                        break;
                    case 5 :
                        Intent brightActivity = new Intent(getApplicationContext(), LuminoGame.class);
                        startActivity(brightActivity);
                        finish();
                        break;
                }
            }
        }, 5000);

    }


}