package com.ottice.ottice;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ottice.ottice.adapters.IntroductionViewPagerAdapter;
import com.ottice.ottice.models.IntroductionSliderBeanClass;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.PrefrencesClass;

import java.util.ArrayList;

import me.relex.circleindicator.CircleIndicator;

/**
 * TODO: Add a class header comment!
 */

public class IntroductionScreenActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private Button getstarted;
    private ViewPager sliderViewPager;
    private CircleIndicator sliderIndicator;
    private ArrayList<IntroductionSliderBeanClass> slidersList;
    private int navigatedScreenID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.introduction_screen);

        Intent intent = getIntent();
        if(intent != null) {
            navigatedScreenID = intent.getIntExtra(Common.INTENT_KEY_StartScreen, 0);
        }

        init();

        getSliderList();

        if(slidersList != null) {
            setSliderViewPager();
        }
    }



    private void init() {
        context = this;
        getstarted = (Button) findViewById(R.id.get_started_button);
        sliderViewPager = (ViewPager) findViewById(R.id.introductionPages);
        sliderIndicator = (CircleIndicator) findViewById(R.id.introductionPageIndicator);

        if(navigatedScreenID == Common.START_SCREEN_ID) {
            getstarted.setOnClickListener(this);
        }else {
            getstarted.setVisibility(View.INVISIBLE);
        }
    }



    private void getSliderList() {
        String[] headings = getResources().getStringArray(R.array.slider_headings);
        String[] subHeadings = getResources().getStringArray(R.array.slider_sub_headings);
        TypedArray imagesId = getResources().obtainTypedArray(R.array.slider_images);

        slidersList = new ArrayList<>();

        for(int i=0; i<headings.length; i++)
        {
            IntroductionSliderBeanClass item = new IntroductionSliderBeanClass();
            item.setHeading(headings[i]);
            item.setSubHeading(subHeadings[i]);
            item.setImageId(imagesId.getResourceId(i, -1));

            slidersList.add(item);
        }
    }



    private void setSliderViewPager() {
        IntroductionViewPagerAdapter sliderAdapter = new IntroductionViewPagerAdapter(context, slidersList);
        sliderViewPager.setAdapter(sliderAdapter);
        sliderIndicator.setViewPager(sliderViewPager);
    }




    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.get_started_button:
                Intent intent = new Intent(context, LoginActivity.class);
                startActivity(intent);

                PrefrencesClass.savePreferenceBoolean(context, Common.FIRST_TIME_INSTALLED, true);
                finish();
                break;
        }
    }

}
