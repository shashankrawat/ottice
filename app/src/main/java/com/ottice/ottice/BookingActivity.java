package com.ottice.ottice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.ottice.ottice.fragments.BookingDetailsFragment;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.Utilities;
import com.wefika.horizontalpicker.HorizontalPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * TODO: Add a class header comment!
 */

public class BookingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.booking_activity);
        Intent intent = getIntent();

        if(findViewById(R.id.bookingContainer) != null){
            if(savedInstanceState != null){
                return;
            }

            BookingDetailsFragment bookingDetailsFragment = new BookingDetailsFragment();
            Bundle args = new Bundle();
            args.putString(Common.INTENT_KEY_PlanType, intent.getStringExtra(Common.INTENT_KEY_PlanType));
            args.putInt(Common.INTENT_KEY_TotalSeats, intent.getIntExtra(Common.INTENT_KEY_TotalSeats, 0));
            args.putLong(Common.INTENT_KEY_Price, intent.getLongExtra(Common.INTENT_KEY_Price, 0));
            args.putInt(Common.INTENT_KEY_SpaceId, intent.getIntExtra(Common.INTENT_KEY_SpaceId, 0));
            bookingDetailsFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction().add(R.id.bookingContainer, bookingDetailsFragment).commit();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.bookingContainer);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
