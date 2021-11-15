package com.example.finalproject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class AudioService extends Service {
    public AudioService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int  val = intent.getIntExtra("INPUT", -1);

        String result = val % 2 == 0 ? val + " is EVEN" : val + " is ODD";
        Log.v("MyTag", "Result from inside service: " + result);

        Intent y = new Intent();
        y.putExtra("OUTPUT", result);
        y.setAction("AudioService"); // Can set it to any string you want
        sendBroadcast(y); // Broadcasts result

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}