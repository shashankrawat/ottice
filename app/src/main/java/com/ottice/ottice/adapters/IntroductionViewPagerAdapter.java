package com.ottice.ottice.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ottice.ottice.R;
import com.ottice.ottice.models.IntroductionSliderBeanClass;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class IntroductionViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<IntroductionSliderBeanClass> sliderList;

    public IntroductionViewPagerAdapter(Context context, ArrayList<IntroductionSliderBeanClass> sliderList)
    {
        this.context = context;
        this. sliderList = sliderList;
    }
    @Override
    public int getCount() {
        return sliderList.size();
    }


    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.introduction_screen_item_layout, container, false);

        ImageView sliderImage = (ImageView) view.findViewById(R.id.sliderImage);
        TextView sliderHeading = (TextView) view.findViewById(R.id.sliderHeading);
        TextView sliderSubHeading = (TextView) view.findViewById(R.id.sliderSubHeading);

        IntroductionSliderBeanClass item = sliderList.get(position);
        sliderImage.setImageResource(item.getImageId());
        sliderHeading.setText(item.getHeading());
        sliderSubHeading.setText(item.getSubHeading());

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View)object);
    }
}
