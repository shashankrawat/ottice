package com.ottice.ottice.utils;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * TODO: Add a class header comment!
 */
public class RecyclerViewItemMargin extends RecyclerView.ItemDecoration {

    private int marginTop, marginBottom;
    private int marginRight, marginLeft;

    public RecyclerViewItemMargin(int marginTop, int marginRight, int marginBottom, int marginLeft)
    {
        this.marginTop = marginTop;
        this.marginRight = marginRight;
        this.marginBottom = marginBottom;
        this.marginLeft = marginLeft;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.set(marginLeft, marginTop, marginRight, marginBottom);
    }
}
