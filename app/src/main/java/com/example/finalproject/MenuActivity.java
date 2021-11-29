package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class MenuActivity extends AppCompatActivity {
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        context = getApplicationContext();
    }

    public void onClick(View v) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.boing);
        mediaPlayer.start();
        if (v.getId() == R.id.start_btn) {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        }
        else {
            Intent intent = new Intent(this, HistoryActivity.class);
            startActivity(intent);
        }
    }

}