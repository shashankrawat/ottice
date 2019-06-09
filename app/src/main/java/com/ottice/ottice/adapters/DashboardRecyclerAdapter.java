package com.ottice.ottice.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.ottice.ottice.R;
import com.ottice.ottice.SpaceDescriptionActivity;
import com.ottice.ottice.models.ProvidersBeanClass;
import com.ottice.ottice.services.ServiceProcessor;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.RecyclerViewItemMargin;
import com.ottice.ottice.utils.Utilities;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */
public class DashboardRecyclerAdapter extends RecyclerView.Adapter<DashboardRecyclerAdapter.ViewHolder> {

    private Context context;
    private Activity activity;
    private ArrayList<ProvidersBeanClass> providersList;
    public DashboardRecyclerAdapter(Activity context, ArrayList<ProvidersBeanClass> providersList)
    {
        this.context = context;
        activity = context;
        this.providersList = providersList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.provider_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DashboardRecyclerAdapter.ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        String availableSeats;

        // provider item to be set for recyclerview item
        ProvidersBeanClass providerItem = providersList.get(position);

        String spaceNameText = providerItem.getSpaceName();

        int remainingSeats = providerItem.getSeats();
        if(remainingSeats > 1) {
            availableSeats = String.valueOf(remainingSeats) + " Seats";
        }else {
            availableSeats = String.valueOf(remainingSeats) + " Seat";
        }
        String imageUri = providerItem.getImageData();
        final int spaceId = providerItem.getId();
        final String spacePlan = providerItem.getSpacePlan();

        String cityNameText = providerItem.getCity()+" "+ providerItem.getState();
        String spaceTypeName = providerItem.getTypeName();
        String price = "Rs "+providerItem.getPrice()+" "+spacePlan;
        String typeIconUri = providerItem.getTypeImageData();

        holder.rating.setRating(Float.parseFloat(providerItem.getRating()));

        // setting values of recyclerview item views
        holder.spaceName.setText(spaceNameText);

        holder.availableSeats.setText(availableSeats);
        if(Utilities.isNotNull(imageUri) && !imageUri.equalsIgnoreCase("null")){
            ServiceProcessor.makeImageRequest(imageUri, holder.spaceImage);
        }

        if(Utilities.isNotNull(cityNameText)){
            holder.providerCityName.setText(cityNameText);
        }

        if(Utilities.isNotNull(spaceTypeName)){
            holder.spaceTypeName.setText(spaceTypeName);
        }
        if(Utilities.isNotNull(price)){
            holder.price.setText(price);
        }
        if(Utilities.isNotNull(typeIconUri)){
            ServiceProcessor.makeImageRequest(typeIconUri, holder.spaceTypeIcon);
        }


        ArrayList<ProvidersBeanClass.Amenities> amenitiesList = providerItem.getAmenitiesList();
        if(amenitiesList != null){
            holder.amenities.setHasFixedSize(true);
            holder.amenities.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            holder.amenities.addItemDecoration(new RecyclerViewItemMargin(0,30,0,30));
            AmenitiesRecyclerAdapter amenitiesAdapter = new AmenitiesRecyclerAdapter(context, amenitiesList, Common.HOME_SCREEN_ID);
            holder.amenities.setAdapter(amenitiesAdapter);
        }

        // recyclerView item click functionality
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.isConnectedToInternet(context)) {
                    ActivityOptions options = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        options = ActivityOptions.
                                makeSceneTransitionAnimation((Activity) context, holder.spaceImage, context.getString(R.string.space_image_transition));
                    }
                    Intent providerIntent = new Intent(context, SpaceDescriptionActivity.class);
                    providerIntent.putExtra("spaceId", spaceId);
                    providerIntent.putExtra("spacePlan",spacePlan);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        if (options != null) {
                            activity.startActivityForResult(providerIntent, Common.SPACE_BOOK_REQUEST,  options.toBundle());
                        }else{
                            activity.startActivityForResult(providerIntent, Common.SPACE_BOOK_REQUEST);
                        }
                    }else {
                        activity.startActivityForResult(providerIntent, Common.SPACE_BOOK_REQUEST);
                    }
                }else {
                    Snackbar.make(holder.view,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return providersList.size();
    }



    class ViewHolder extends RecyclerView.ViewHolder {
        public View view;
        ImageView spaceImage, spaceTypeIcon;
        TextView spaceName, availableSeats, providerCityName, spaceTypeName, price;
        RatingBar rating;
        RecyclerView amenities;

        ViewHolder(View itemView) {
            super(itemView);
            spaceImage = (ImageView)itemView.findViewById(R.id.spaceDefaultImage);
            view = itemView.findViewById(R.id.providerItemLayout);
            spaceName = (TextView) itemView.findViewById(R.id.spaceName);
            providerCityName = (TextView) itemView.findViewById(R.id.providerCityName);
            availableSeats = (TextView) itemView.findViewById(R.id.availableSeats);
            amenities = (RecyclerView) itemView.findViewById(R.id.amenitiesList);
            spaceTypeIcon = (ImageView) itemView.findViewById(R.id.officeTypeIcon);
            spaceTypeName = (TextView) itemView.findViewById(R.id.officeTypeName);
            price = (TextView) itemView.findViewById(R.id.officeTypePrice);
            rating=(RatingBar) itemView.findViewById(R.id.rating_text);


            Utilities.initializeRatingBars(context, rating);
        }
    }


}
