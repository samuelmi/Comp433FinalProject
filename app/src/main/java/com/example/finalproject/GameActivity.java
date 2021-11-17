package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageButton;

public class GameActivity extends AppCompatActivity {
    Context context;

    boolean animate = false;
    ImageButton btnToAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        context = getApplicationContext();
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.music); // TODO: call stop() method when finished or regret your life choices
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    public void resetGame() {
        animate = false;

    }

}