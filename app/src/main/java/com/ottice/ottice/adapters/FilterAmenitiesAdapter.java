package com.ottice.ottice.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ottice.ottice.R;
import com.ottice.ottice.models.CommonItemsBeanClass;
import com.ottice.ottice.services.ServiceProcessor;
import com.ottice.ottice.utils.Utilities;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class FilterAmenitiesAdapter extends RecyclerView.Adapter<FilterAmenitiesAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommonItemsBeanClass> amenitiesArrayList;
    private AmenitiesItemClickListener itemClickListener;

    public FilterAmenitiesAdapter(Context context, ArrayList<CommonItemsBeanClass> amenitiesArrayList, AmenitiesItemClickListener itemClickListener){

        this.context = context;
        this. amenitiesArrayList = amenitiesArrayList;
        this.itemClickListener = itemClickListener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_amenity_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CommonItemsBeanClass amenitiesItem = amenitiesArrayList.get(position);
        final int itemPosition = position;

        String iconUri = amenitiesItem.getImageUri();
        String nameText = amenitiesItem.getName();

        if(Utilities.isNotNull(iconUri)){
            ServiceProcessor.makeImageRequest(iconUri, holder.amenitiesIcon);
        }
        holder.amenityName.setText(nameText);
        if(amenitiesItem.isSelected()){
            holder.amenitiesIcon.setBackgroundResource(R.drawable.amenities_selected_background);
            holder.amenityName.setTextColor(ContextCompat.getColor(context,R.color.color_d560a1));
        }else{
            holder.amenitiesIcon.setBackgroundResource(R.drawable.amenities_unselected_background);
            holder.amenityName.setTextColor(ContextCompat.getColor(context,R.color.colorPrimary));
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClickListener != null) {
                    itemClickListener.onAmenityClick(amenitiesItem);
                }
                if(amenitiesArrayList.get(itemPosition).isSelected()){
                    amenitiesArrayList.get(itemPosition).setSelected(false);
                }else {
                    amenitiesArrayList.get(itemPosition).setSelected(true);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return amenitiesArrayList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        ImageView amenitiesIcon;
        TextView amenityName;
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.amenitiesItemView);
            amenitiesIcon = (ImageView) itemView.findViewById(R.id.filterAmenitiesIcon);
            amenityName = (TextView) itemView.findViewById(R.id.filterAmenityName);
        }
    }


    public interface AmenitiesItemClickListener{
        void onAmenityClick(Object itemObject);
    }
}
