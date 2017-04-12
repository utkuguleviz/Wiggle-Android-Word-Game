package utku.guleviz.anagram.androidproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class wiggleScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {

                startActivity(new Intent("utku.guleviz.anagram.androidproject.SINGLEMULTIPLAYER"));// farklı bir aktivite başlatıyorsun.
            }
        }, 1000);

    }



    @Override
    protected void onPause()
    {

        super.onPause();
        finish();
    }

}