package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class GameActivity extends AppCompatActivity {
    Context context;

    boolean animate;
    String letterToAnimate;
    ArrayList<String> remainingLetters = new ArrayList<>();

    MediaPlayer m;

    ImageButton btnToAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        context = getApplicationContext();
        startGame();
    }

    public void startGame() {
        m = MediaPlayer.create(context, R.raw.music);

        m.setLooping(true);
        m.start();
        resetGame();
        getNextLetter();
    }

    public void stopGame() {
        m.stop(); // Stops the music
        resetGame();
    }


    public void animateLetter() {
        animate = true;
        Thread t = new Thread() {
            @Override
            public void run() {
                int audioSessionId;
                int letterId;
                switch (letterToAnimate) { // Gets the audio clip and ID for ImageButton to animate
                    case "a": audioSessionId = R.raw.a; letterId = R.id.a_btn; break;
                    case "b": audioSessionId = R.raw.b; letterId = R.id.b_btn; break;
                    case "c": audioSessionId = R.raw.c; letterId = R.id.c_btn; break;
                    case "d": audioSessionId = R.raw.d; letterId = R.id.d_btn; break;
                    case "e": audioSessionId = R.raw.e; letterId = R.id.e_btn; break;
                    case "f": audioSessionId = R.raw.f; letterId = R.id.f_btn; break;
                    case "g": audioSessionId = R.raw.g; letterId = R.id.g_btn; break;
                    case "h": audioSessionId = R.raw.h; letterId = R.id.h_btn; break;
                    case "i": audioSessionId = R.raw.i; letterId = R.id.i_btn; break;
                    case "j": audioSessionId = R.raw.j; letterId = R.id.j_btn; break;
                    case "k": audioSessionId = R.raw.k; letterId = R.id.k_btn; break;
                    case "l": audioSessionId = R.raw.l; letterId = R.id.l_btn; break;
                    case "m": audioSessionId = R.raw.m; letterId = R.id.m_btn; break;
                    case "n": audioSessionId = R.raw.n; letterId = R.id.n_btn; break;
                    case "o": audioSessionId = R.raw.o; letterId = R.id.o_btn; break;
                    case "p": audioSessionId = R.raw.p; letterId = R.id.p_btn; break;
                    case "q": audioSessionId = R.raw.q; letterId = R.id.q_btn; break;
                    case "r": audioSessionId = R.raw.r; letterId = R.id.r_btn; break;
                    case "s": audioSessionId = R.raw.s; letterId = R.id.s_btn; break;
                    case "t": audioSessionId = R.raw.t; letterId = R.id.t_btn; break;
                    case "u": audioSessionId = R.raw.u; letterId = R.id.u_btn; break;
                    case "v": audioSessionId = R.raw.v; letterId = R.id.v_btn; break;
                    case "w": audioSessionId = R.raw.w; letterId = R.id.w_btn; break;
                    case "x": audioSessionId = R.raw.x; letterId = R.id.x_btn; break;
                    case "y": audioSessionId = R.raw.y; letterId = R.id.y_btn; break;
                    case "z": audioSessionId = R.raw.z; letterId = R.id.z_btn; break;
                    default: audioSessionId = -1; letterId = -1; // If invalid letter is given, set both to -1
                }

                try {
                    MediaPlayer childVoice = MediaPlayer.create(context, audioSessionId);
                    ImageView iv = findViewById(letterId);
                    String localLetterCopy = letterToAnimate;
                    while (localLetterCopy == letterToAnimate) { // Loop until the letter to animate is not longer this;
                        childVoice.start();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Animation anim = new ScaleAnimation(
                                        1f, 1f, // Start and end values for the X axis scaling
                                        0, 1, // Start and end values for the Y axis scaling
                                        Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                                        Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
                                anim.setFillAfter(true); // Needed to keep the result of the animation
                                anim.setDuration(1000);
                                iv.startAnimation(anim);
                            }
                        });
                        sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Animation anim = new ScaleAnimation(
                                        1f, 1f, // Start and end values for the X axis scaling
                                        1, 0, // Start and end values for the Y axis scaling
                                        Animation.RELATIVE_TO_SELF, 0f, // Pivot point of X scaling
                                        Animation.RELATIVE_TO_SELF, 1f); // Pivot point of Y scaling
                                anim.setFillAfter(true); // Needed to keep the result of the animation
                                anim.setDuration(1000);
                                iv.startAnimation(anim);
                            }
                        });
                        sleep(1000);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }

    public void getNextLetter() {
        Collections.shuffle(remainingLetters);
        letterToAnimate = remainingLetters.remove(0);
        Log.v("MyTag", "Letter to animate: "+letterToAnimate);
        animateLetter();
    }

    public void resetGame() {
        animate = false;
        remainingLetters = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
        letterToAnimate = "";
    }

}