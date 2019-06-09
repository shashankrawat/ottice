package com.ottice.ottice.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ottice.ottice.R;
import com.ottice.ottice.services.ServiceProcessor;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */
public class SpaceImagesViewPagerAdapter extends PagerAdapter {

    private Context context;
    private ArrayList<String> spaceImagesUri;

    public SpaceImagesViewPagerAdapter(Context context, ArrayList<String> spaceImagesUri){
        this.context = context;
        this.spaceImagesUri = spaceImagesUri;
    }


    @Override
    public int getCount() {
        return spaceImagesUri.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.space_images_viewpager_item, container, false);

        ImageView spaceImage = (ImageView) view.findViewById(R.id.spaceImage);
        ServiceProcessor.makeImageRequest(spaceImagesUri.get(position),spaceImage);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        (container).removeView((View)object);
    }
}
