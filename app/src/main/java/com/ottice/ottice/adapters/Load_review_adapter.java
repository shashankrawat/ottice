package com.ottice.ottice.adapters;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ottice.ottice.R;
import com.ottice.ottice.models.ProvidersBeanClass;
import com.ottice.ottice.models.ReviewRatingBeanClass;
import com.ottice.ottice.utils.Utilities;

import java.util.ArrayList;

/**
 * Created by shashank.rawat on 13-07-2017.
 */

public class Load_review_adapter extends RecyclerView.Adapter<Load_review_adapter.viewholder> {

    private Context context;
    private ArrayList<ReviewRatingBeanClass> review_list;


    public Load_review_adapter(Context context, ArrayList<ReviewRatingBeanClass >review_list){
        this.context=context;
        this.review_list=review_list;

    }
    @Override
    public Load_review_adapter.viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.load_reviews_items, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(Load_review_adapter.viewholder holder, int position) {
        ReviewRatingBeanClass reviewRatingBeanClass=review_list.get(position);
        holder.ratingBar.setRating(Float.parseFloat(reviewRatingBeanClass.getRating()));
        holder.reviews.setText(reviewRatingBeanClass.getReviews());
        holder.username.setText(reviewRatingBeanClass.getUsername());


    }
    public class viewholder extends RecyclerView.ViewHolder {
        TextView username;
        TextView reviews;
        RatingBar ratingBar;

        public viewholder(View itemView) {
            super(itemView);
            ratingBar=(RatingBar)itemView.findViewById(R.id.total_rating);
            reviews=(TextView)itemView.findViewById(R.id.review_text);
            username=(TextView)itemView.findViewById(R.id.User_name);

            Utilities.initializeRatingBars(context, ratingBar);
        }
    }

    @Override
    public int getItemCount() {
        return review_list==null? 0 : review_list.size();
        //return itemsData == null ? 0 : itemsData.length;
    }
}
