package com.ottice.ottice;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.MapView;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONObject;

public class StartScreen extends AppCompatActivity {

    private boolean previouslyInstalled;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // attached layout
        setContentView(R.layout.activity_start_screen);

        //to load map faster
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    MapView mv = new MapView(getApplicationContext());
                    mv.onCreate(null);
                    mv.onPause();
                    mv.onDestroy();
                }catch (Exception ignored){

                }
            }
        }).start();


        // getting device Id to send to service
        Common.deviceId = Utilities.getDeviceID(AppController.getInstance());

        previouslyInstalled = PrefrencesClass.getPreferenceBoolean(this, Common.FIRST_TIME_INSTALLED);
        userID = PrefrencesClass.getPreferenceValue(StartScreen.this, Common.USERID);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!previouslyInstalled) {
                    Intent intent = new Intent(StartScreen.this, IntroductionScreenActivity.class);
                    intent.putExtra(Common.INTENT_KEY_StartScreen, Common.START_SCREEN_ID);
                    startActivity(intent);
                    finish();
                }else if(Utilities.isNotNull(userID)) {
                    Intent intent = new Intent(StartScreen.this, SelectLocationActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(StartScreen.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 3000);

    }


}
