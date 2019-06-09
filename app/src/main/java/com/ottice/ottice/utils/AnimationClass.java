package com.ottice.ottice.utils;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.ottice.ottice.SelectLocationActivity;

/**
 * TODO: Add a class header comment!
 */
public class AnimationClass {

    private Context context;
    public AnimationClass(Context context){
        this.context = context;
    }

    public void collapse(final View v, View v1, final View v2){
        final int initialWidth = v.getMeasuredWidth();
        final int collapseWidth = v2.getMeasuredWidth();

        v1.setClickable(false);

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v2.setVisibility(View.VISIBLE);
                }else{
                    v.getLayoutParams().width = initialWidth - (int) (collapseWidth * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialWidth / v.getContext().getResources().getDisplayMetrics().density)*2);
        v.startAnimation(a);
    }


    public void expand(final View v, final View v1, View v2){
        final int initialWidth = v.getMeasuredWidth();
        final int expandedWidth = v2.getMeasuredWidth();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().width = initialWidth;
        v2.setVisibility(View.INVISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v1.setClickable(true);
                }else {
                    v.getLayoutParams().width = initialWidth + (int) (expandedWidth * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialWidth / v.getContext().getResources().getDisplayMetrics().density)*2);
        v.startAnimation(a);
    }

}
