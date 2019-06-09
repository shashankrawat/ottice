package com.ottice.ottice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ottice.ottice.fragments.HomeFragment;
import com.ottice.ottice.fragments.MyBookingsFragment;
import com.ottice.ottice.fragments.ProfileFragment;
import com.ottice.ottice.fragments.SettingsFragment;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.NotificationUtils;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * TODO: Add a class header comment!
 */

public class DashboardActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Button homeButton, myBookingButton, profileButton, moreButton;
    private Fragment fragment;
    private double user_Lat, user_Lng;
    private boolean isHome;
    private RelativeLayout mainView;
    private LinearLayout menuView;
    private ImageView menuOpener;
    private boolean isMenuOpen = true;
    private TextView menuOpenerNotifyIcon, myBookingNotifyIcon;
    private BroadcastReceiver mNotificationBroadcastReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_activity);


        Intent intent = getIntent();
        user_Lat = intent.getDoubleExtra(Common.INTENT_KEY_Latitude, 0);
        user_Lng = intent.getDoubleExtra(Common.INTENT_KEY_Longitude, 0);


        init();

        if(findViewById(R.id.dashboardContainer) != null){
            if(savedInstanceState != null){
                return;
            }
            homeButton.performClick();
            menuOpener.performClick();
        }

        Log.e("NOTIFY TOKEN",""+ PrefrencesClass.getPreferenceValue(context,Common.FIREBASE_NOTIFICATION_TOKEN));

        String notificationData = getIntent().getStringExtra("message");
        Bundle args = getIntent().getBundleExtra("message");
        if(notificationData != null || args != null)
        {
            myBookingButton.performClick();
        }

        mNotificationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("DASHBORD INTENT",""+intent.getStringExtra("message"));
                showNotificationIndicator();
            }
        };

    }

    private void init() {
        context = this;
        homeButton = (Button) findViewById(R.id.homeButton);
        myBookingButton = (Button) findViewById(R.id.myBookingsButton);
        profileButton = (Button) findViewById(R.id.profileButton);
        moreButton = (Button) findViewById(R.id.moreButton);
        mainView = (RelativeLayout) findViewById(R.id.mainDashboardContainer);
        menuView = (LinearLayout) findViewById(R.id.menuLayout);
        menuOpener = (ImageView) findViewById(R.id.menuOpener);
        menuOpenerNotifyIcon = (TextView) findViewById(R.id.menuOpenerNotifyIcon);
        myBookingNotifyIcon = (TextView) findViewById(R.id.myBookingNotifyIcon);

        setStartTimeValues();
        setFilterDefaultValue();

        homeButton.setOnClickListener(this);
        myBookingButton.setOnClickListener(this);
        profileButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);
        menuOpener.setOnClickListener(this);
    }

    private void setFilterDefaultValue() {
        Common.minPrice = "0";
        Common.maxPrice = "20000";
        Common.spaceTypeId = "1";
        Common.capacity = "1";
        Common.amenitiesList = new ArrayList<>();
        Common.resetApplied = true;
    }



    private void setStartTimeValues() {
        Common.currentDate = getStartTimeRoundOffValue();

        Common.startDateStr = Common.showingDateFormat.format(Common.currentDate);
        Common.startTimeStr = Common.showingTimeFormat.format(Common.currentDate);

        Common.dateArray = getRemainingDates(Common.currentDate);
    }



    private Date getStartTimeRoundOffValue(){

        Calendar calendar = Calendar.getInstance();
        int unroundedMinutes = calendar.get(Calendar.MINUTE);
        int mod = 60 - unroundedMinutes;
        calendar.add(Calendar.HOUR, mod < 30 ? 2:1);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        Date userDate = calendar.getTime();

        String currentDateStr = Common.showingDateFormat.format(userDate);

        String [] defaultTimeArray = getResources().getStringArray(R.array.start_timings);

        Date minTime, maxTime;
        try {
            minTime = Common.appDateFormat.parse(currentDateStr +" "+defaultTimeArray[0]);
            maxTime = Common.appDateFormat.parse(currentDateStr +" "+defaultTimeArray[defaultTimeArray.length-1]);
            if(userDate.after(maxTime)){
                calendar.add(Calendar.DATE, 1);
                calendar.set(Calendar.HOUR_OF_DAY,9);
            }
            if(userDate.before(minTime)){
                calendar.set(Calendar.HOUR_OF_DAY,9);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return calendar.getTime();
    }


    private String[] getRemainingDates(Date date){
        ArrayList<String> remainingDates = new ArrayList<>();
        Calendar mCalendar = Calendar.getInstance();
        int CurrentDay = mCalendar.get(Calendar.DAY_OF_MONTH);
        mCalendar.setTime(date);
        // Calculate remaining days in month
        Common.remainingDays = mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - mCalendar.get(Calendar.DAY_OF_MONTH) + 1;
        if(Common.remainingDays < 15){
            mCalendar.add(Calendar.MONTH,1);
            mCalendar.set(Calendar.DAY_OF_MONTH,1);
            Common.remainingDays = Common.remainingDays + mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH) - mCalendar.get(Calendar.DAY_OF_MONTH) + 1;
            mCalendar.add(Calendar.MONTH,-1);
            mCalendar.set(Calendar.DAY_OF_MONTH, CurrentDay);
        }
        for(int i=0; i<Common.remainingDays; i++){
            remainingDates.add(Common.showingDateFormat.format(mCalendar.getTime()));
            mCalendar.add(Calendar.DAY_OF_MONTH, 1);
        }
        return remainingDates.toArray(new String[remainingDates.size()]);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.dashboardContainer);
        fragment.onActivityResult(requestCode, resultCode, data);
    }



    @Override
    protected void onResume() {
        AppController.isDashboardActivityActive = true;
        super.onResume();
        LocalBroadcastManager.getInstance(context).registerReceiver(mNotificationBroadcastReceiver,
                new IntentFilter(Common.NOTIFICATION_DATA_RECEIVER));

        if(PrefrencesClass.getPreferenceBoolean(context, Common.NOTIFICATION_DATA_RECEIVER)){
            showNotificationIndicator();
        }
        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }



    private void showNotificationIndicator(){
        if(isMenuOpen){
            menuOpenerNotifyIcon.setVisibility(View.GONE);
            myBookingNotifyIcon.setVisibility(View.VISIBLE);
        }else {
            menuOpenerNotifyIcon.setVisibility(View.VISIBLE);
            myBookingNotifyIcon.setVisibility(View.VISIBLE);
        }
    }
    @Override
    protected void onPause() {
        AppController.isDashboardActivityActive = false;
        super.onPause();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(mNotificationBroadcastReceiver);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.homeButton:
                if (homeButton.isSelected()){}
                else {
                    Utilities.setSystemBarsTransparentFully(DashboardActivity.this, mainView, false);

                    isHome = true;
                    homeButton.setSelected(true);
                    myBookingButton.setSelected(false);
                    profileButton.setSelected(false);
                    moreButton.setSelected(false);


                    fragment = new HomeFragment();
                    Bundle args = new Bundle();
                    args.putDouble(Common.INTENT_KEY_Latitude, user_Lat);
                    args.putDouble(Common.INTENT_KEY_Longitude, user_Lng);
                    fragment.setArguments(args);

                    if (getSupportFragmentManager().findFragmentById(R.id.dashboardContainer) == null) {
                        getSupportFragmentManager().beginTransaction().add(R.id.dashboardContainer, fragment).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, fragment).commit();
                    }
                }
                break;

            case R.id.myBookingsButton:
                if (myBookingButton.isSelected()){

                }
                else {
                    Utilities.setSystemBarsTransparentFully(DashboardActivity.this, mainView, true);

                    isHome = false;
                    homeButton.setSelected(false);
                    myBookingButton.setSelected(true);
                    profileButton.setSelected(false);
                    moreButton.setSelected(false);

                    menuOpenerNotifyIcon.setVisibility(View.GONE);
                    myBookingNotifyIcon.setVisibility(View.GONE);

                    fragment = new MyBookingsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, fragment).commit();
                }
                break;

            case R.id.profileButton:
                if (profileButton.isSelected()){}
                else {
                    Utilities.setSystemBarsTransparentFully(DashboardActivity.this, mainView, true);

                    isHome = false;
                    homeButton.setSelected(false);
                    myBookingButton.setSelected(false);
                    profileButton.setSelected(true);
                    moreButton.setSelected(false);

                    fragment = new ProfileFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, fragment).commit();
                }
                break;

            case R.id.moreButton:
                if (moreButton.isSelected()){}
                else {
                    Utilities.setSystemBarsTransparentFully(DashboardActivity.this, mainView, true);

                    isHome = false;
                    homeButton.setSelected(false);
                    myBookingButton.setSelected(false);
                    profileButton.setSelected(false);
                    moreButton.setSelected(true);

                    fragment = new SettingsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.dashboardContainer, fragment).commit();
                }
                break;

            case R.id.menuOpener:
                if(isMenuOpen){
                    menuView.setVisibility(View.GONE);
                    isMenuOpen = false;
                }else {
                    menuView.setVisibility(View.VISIBLE);
                    isMenuOpen = true;
                }
                menuOpenerNotifyIcon.setVisibility(View.GONE);
                break;
        }
    }


    @Override
    public void onBackPressed() {

        if(!isHome){
            homeButton.performClick();
        }else {
            super.onBackPressed();
        }
    }

}
