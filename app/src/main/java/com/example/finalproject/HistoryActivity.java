package com.example.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.DateTimePatternGenerator;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    Context context;

    BarChart barChart;

    SQLiteDatabase db;

    LinearLayout scrollView;

    String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    ArrayList<ArrayList<ImageSubmission>> recentPhotos;  // ArrayList that will be populated with the 3 most recent photos (if there are even 3 photos) for each letter. recentPhotos[0] = recent photos for A, etc.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        db = openOrCreateDatabase("MyDatabase", Context.MODE_PRIVATE, null);
        barChart = findViewById(R.id.chart);
        scrollView = findViewById(R.id.scrollView);
        context = getApplicationContext();
        getRecentPhotos();
        showRecentPhotos();

        //printDbContents();
        initBarChart();
        showBarChart();
    }

    public void onClick(View v) {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
    }

    // Method for displaying the three most recent photos
    public void showRecentPhotos() {
        for (int i = 0; i < recentPhotos.size(); i++) {
            ArrayList<ImageSubmission> threeMostRecent = recentPhotos.get(i);

            LinearLayout top = new LinearLayout(context);
            top.setOrientation(LinearLayout.VERTICAL);
            top.setGravity(Gravity.CENTER); // Centers components

            TextView tv = new TextView(context);
            tv.setText(letters[i].toUpperCase());
            tv.setGravity(Gravity.CENTER);
            tv.setTextSize(20);

            LinearLayout imagePanel = new LinearLayout(context); // Panel that will hold the 3 images
            imagePanel.setOrientation(LinearLayout.HORIZONTAL);
            imagePanel.setGravity(Gravity.CENTER);

            for (ImageSubmission imageSubmission : threeMostRecent) {
                LinearLayout imageLayout = new LinearLayout(context);
                imageLayout.setOrientation(LinearLayout.VERTICAL);

                ImageView iv = new ImageView(context);
                iv.setImageBitmap(imageSubmission.image);
                imageLayout.addView(iv);

                TextView tags = new TextView(context);
                tags.setText(imageSubmission.tags.replace(",", "\n"));
                tags.setTextSize(10);
                tags.setGravity(Gravity.CENTER);
                imageLayout.addView(tags);

                TextView timestamp = new TextView(context);
                timestamp.setText(imageSubmission.timestamp.toString());
                timestamp.setTextSize(10);
                timestamp.setGravity(Gravity.CENTER);
                imageLayout.addView(timestamp);

                TextView elapsedTime = new TextView(context);
                elapsedTime.setText("Took " + imageSubmission.elapsedTime + " seconds to take photo");
                elapsedTime.setTextSize(10);
                elapsedTime.setGravity(Gravity.CENTER);                imageLayout.addView(elapsedTime);

                imagePanel.addView(imageLayout);                         // Adds image taken to imagePanel
            }

            top.addView(tv);
            top.addView(imagePanel); // Adds the image panel
            scrollView.addView(top);
        }
    }

    public void getRecentPhotos() {
        // Clears the recentPhotos array
        recentPhotos = new ArrayList<ArrayList<ImageSubmission>>();

        for (int i = 0; i < letters.length; i++) {
            // Gets all Image byte arrays for a given letter
            Cursor c = db.rawQuery(String.format("SELECT Image, Letter, Tags, Score, ElapsedTime, Timestamp from Images WHERE Letter == \"%s\"", letters[i]), null);
            c.moveToFirst();
            ArrayList<ImageSubmission> threeMostRecent = new ArrayList<>(); // Creates a new ArrayList to store the 3 most recent image bitmap byte arrays
            // Iterates through each result
            for (int j = 0; j < c.getCount(); j++) {
                if (threeMostRecent.size() < 3) { // While the ArrayList is smaller than 3 entries, by default this Byte array will be the most recent
                    threeMostRecent.add(new ImageSubmission(c.getBlob(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getLong(5)));
                }
                else { // Otherwise will need to check for only the 3 most recent photos
                    ImageSubmission oldestPhoto = null;
                    for (ImageSubmission imageSubmission : threeMostRecent) { // Loops through each Tuple until it finds (if any) one that has a smaller Timestamp
                        if (c.getLong(5) > imageSubmission.timestamp.getTime()) { // If this photo is newer
                            // If no oldestPhoto has been chosen or if the one previously chosen is newer than this one, replace it
                            if (oldestPhoto == null || oldestPhoto.timestamp.getTime() > imageSubmission.timestamp.getTime()) oldestPhoto = imageSubmission;
                        }
                    }
                    if (oldestPhoto != null) {
                        threeMostRecent.remove(oldestPhoto);
                        threeMostRecent.add(new ImageSubmission(c.getBlob(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getLong(5))); // Replaces the oldest of the most recent with this photo if applicable
                    }
                }
                c.moveToNext(); // Moves cursor to the next entry
            }
            recentPhotos.add(threeMostRecent); // Adds data to recentPhotos ArrayList
        }
    }

    private void showBarChart(){
        ArrayList<Double> valueList = new ArrayList<Double>();
        ArrayList<BarEntry> entries = new ArrayList<>();
        String title = "Average Times";

        for (String letter : letters) {
            Cursor c = db.rawQuery(String.format("SELECT ElapsedTime from Images WHERE Letter == \"%s\"", letter), null);
            int sum = 0;
            c.moveToFirst();
            for (int i = 0; i < c.getCount(); i++) {
                sum += c.getInt(0);
            }
            double avg = 0;
            if (c.getCount() > 0) {
                avg = sum / c.getCount(); // Computes average elapsed time
            }
            valueList.add(avg);
        }

        //fit the data into a bar
        for (int i = 0; i < valueList.size(); i++) {
            BarEntry barEntry = new BarEntry(i, valueList.get(i).floatValue());
            entries.add(barEntry);
        }

        BarDataSet barDataSet = new BarDataSet(entries, title);

        BarData data = new BarData(barDataSet);
        barChart.setData(data);
        barChart.invalidate();
    }


    private void initBarChart(){
        //hiding the grey background of the chart, default false if not set
        barChart.setDrawGridBackground(false);
        //remove the bar shadow, default false if not set
        barChart.setDrawBarShadow(false);
        //remove border of the chart, default false if not set
        barChart.setDrawBorders(false);

        //remove the description label text located at the lower right corner
        Description description = new Description();
        description.setEnabled(false);
        barChart.setDescription(description);

        //setting animation for y-axis, the bar will pop up from 0 to its value within the time we set
        barChart.animateY(1000);
        //setting animation for x-axis, the bar will pop up separately within the time we set
        barChart.animateX(1000);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(26);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return letters[(int)value];
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();
        //hiding the left y-axis line, default true if not set
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = barChart.getAxisRight();
        //hiding the right y-axis line, default true if not set
        rightAxis.setDrawAxisLine(false);

        Legend legend = barChart.getLegend();
        //setting the shape of the legend form to line, default square shape
        legend.setForm(Legend.LegendForm.LINE);
        //setting the text size of the legend
        legend.setTextSize(11f);
        //setting the alignment of legend toward the chart
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        //setting the stacking direction of legend
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //setting the location of legend outside the chart, default false if not set
        legend.setDrawInside(false);

    }
}