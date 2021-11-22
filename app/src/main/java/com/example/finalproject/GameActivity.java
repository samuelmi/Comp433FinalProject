package com.example.finalproject;

import static com.example.finalproject.R.drawable.right;
import static com.example.finalproject.R.drawable.wrong;

import static java.lang.Thread.sleep;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameActivity extends AppCompatActivity {
    Context context;

    TextView timer;             // Text view that displays time between rounds

    Bitmap image;               // Image returned by camera

    String letter;              // The letter used
    int letterId;               // ID of the ImageView associated with the letter
    ArrayList<String> remainingLetters = new ArrayList<>();

    long timeStart;

    MediaPlayer m;

    ImageButton btnToAnimate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        context = getApplicationContext();
        timer = findViewById(R.id.timer);
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
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }


    public void animateLetter() {
        Thread t = new Thread() {
            @Override
            public void run() {
                int audioSessionId;
                switch (letter) { // Gets the audio clip and ID for ImageButton to animate
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
                    String localLetterCopy = letter;
                    while (localLetterCopy == letter) { // Loop until the letter to animate is not longer this;
                        childVoice.start();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                timer.setText(""+(System.currentTimeMillis() - timeStart) / 1000);
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
                                timer.setText(""+(System.currentTimeMillis() - timeStart) / 1000);
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
        if (remainingLetters.size() == 0) stopGame();  // Stop game and return to menu if no letters left
        Collections.shuffle(remainingLetters);
        letter = remainingLetters.remove(0);
        Log.v("MyTag", "Letter to animate: "+ letter);
        timeStart = System.currentTimeMillis(); // Resets timer
        animateLetter();
    }

    public void resetGame() {
        remainingLetters = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
        letter = "";
    }


    public void click(View v) {
        int id = v.getId();

        if (id == letterId) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 1);
        }
        else if (id == R.id.back_btn) { // Returns to menu
            stopGame();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = (Bitmap) data.getExtras().get("data");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    getImageLabels();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    void getImageLabels() throws IOException
    {
        ImageView iv = findViewById(letterId);

        //1. ENCODE image.
        Bitmap bitmap = image;
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bout);
        Image myimage = new Image();
        myimage.encodeContent(bout.toByteArray());

        //2. PREPARE AnnotateImageRequest
        AnnotateImageRequest annotateImageRequest = new AnnotateImageRequest();
        annotateImageRequest.setImage(myimage);
        Feature f = new Feature();
        f.setType("LABEL_DETECTION");
        f.setMaxResults(3);
        List<Feature> lf = new ArrayList<Feature>();
        lf.add(f);
        annotateImageRequest.setFeatures(lf);

        //3.BUILD the Vision
        HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
        builder.setVisionRequestInitializer(new VisionRequestInitializer(getResources().getString(R.string.api_key)));
        Vision vision = builder.build();

        //4. CALL Vision.Images.Annotate
        BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
        List<AnnotateImageRequest> list = new ArrayList<AnnotateImageRequest>();
        list.add(annotateImageRequest);
        batchAnnotateImagesRequest.setRequests(list);
        Vision.Images.Annotate task = vision.images().annotate(batchAnnotateImagesRequest);
        BatchAnnotateImagesResponse response = task.execute();

        EntityAnnotation[] annotations = response.getResponses().get(0).getLabelAnnotations().toArray(new EntityAnnotation[0]);
        boolean correct = false;
        for (int i = 0; i < annotations.length; i ++) {
            EntityAnnotation a = annotations[i];
            // If the photo is taken of something that starts with the right letter, mark correct
            if (a.getDescription().substring(0,1).toLowerCase().equals(letter)) {
                correct = true;
                break;
            }
        }

        getNextLetter(); // Gets the next letter

        try {
            sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean finalCorrect = correct;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (finalCorrect) {
                    iv.setImageDrawable(getResources().getDrawable(right));
                }
                else {
                    iv.setImageDrawable(getResources().getDrawable(wrong));
                }
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

        try {
            sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                iv.setImageBitmap(image); // Sets image bitmap
            }
        });
    }

}