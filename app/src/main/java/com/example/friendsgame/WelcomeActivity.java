package com.example.friendsgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

public class WelcomeActivity extends AppCompatActivity {

    TextView explication;
    Button ok;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_page);

        init();
        listeners();
    }

    private void listeners() {
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                finish();
            }
        });
    }

    public void init() {
        explication = findViewById(R.id.tv_explication);
        explication.setMovementMethod(new ScrollingMovementMethod());
        ok = findViewById(R.id.bt_ok);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("text/explication.txt")));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            explication.setText(text);
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),"Error reading file!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}