package com.example.finalproject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Date;

public class ImageSubmission {
    final Bitmap image;
    final String letter;
    final String tags;
    final String score;
    final int elapsedTime;
    final Date timestamp;



    public ImageSubmission(byte[] byteArr, String letter, String tags, String score, int elapsedTime, long timestamp) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        image = BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length, options); // Converts byte array to bitmap
        this.letter = letter;
        this.tags = tags;
        this.score = score;
        this.elapsedTime = elapsedTime;
        this.timestamp = new Date(timestamp);
    }
}
