package com.example.finalproject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.AnnotateImageResponse;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Context context;

    TextView tv;
    ImageView iv;
    Button captureBtn;

    Bitmap image;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BroadcastReceiver br = new MyReceiver();
        registerReceiver(br, new IntentFilter("AudioService"));


                context = getApplicationContext();

        tv = findViewById(R.id.tv);
        iv = findViewById(R.id.iv);
        captureBtn = findViewById(R.id.capture_btn);

        Intent x = new Intent(this, AudioService.class);
        x.putExtra("INPUT", 123);
        startService(x);
    }

    public void click(View v) {
        int id = v.getId();

        // Capture is clicked
        if (id == R.id.capture_btn) {
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        image = (Bitmap) data.getExtras().get("data");
        iv.setImageBitmap(image);

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
        f.setMaxResults(5);
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
        String labelString = "";
        for (int i = 0; i < annotations.length; i ++) {
            EntityAnnotation a = annotations[i];
            labelString += a.getDescription();
            if (i < annotations.length - 1) labelString += ", ";
            //Log.v("MyTag", ""+a.getDescription());

        }
        //Log.v("MyTag", response.toPrettyString());


        String finalLabelString = labelString;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(finalLabelString);
            }
        });
    }

}