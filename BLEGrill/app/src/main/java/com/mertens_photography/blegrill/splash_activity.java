package com.mertens_photography.blegrill;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 */
public class splash_activity extends Activity {

    // Time to sleep before change activity
    int sleepTime = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Deactiviere die ActionBar (Activity Name und AppIcon)
        getActionBar().hide();

        setContentView(R.layout.activity_splash_activity);

        // Create new timer thread which switch to main activity after some seconds
        Thread timer = new Thread() {

            public  void run(){
                try {
                    sleep(sleepTime);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }finally {
                    Intent openMainActivity = new Intent("com.mertens_photography.blegrill.MAINACTIVITY");
                    startActivity(openMainActivity);
                }
            }

        };

        timer.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Close this activity on pause
        finish();
    }
}
