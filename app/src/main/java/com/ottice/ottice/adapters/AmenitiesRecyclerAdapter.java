package com.ottice.ottice.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ottice.ottice.R;
import com.ottice.ottice.models.ProvidersBeanClass;
import com.ottice.ottice.services.ServiceProcessor;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.Utilities;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class AmenitiesRecyclerAdapter extends RecyclerView.Adapter<AmenitiesRecyclerAdapter.ViewHolder> {

    private  Context context;
    private ArrayList<ProvidersBeanClass.Amenities> amenitiesArrayList;
    private int screenId;

    public AmenitiesRecyclerAdapter(Context context, ArrayList<ProvidersBeanClass.Amenities> amenitiesArrayList, int screenId){
        this.context = context;
        this.amenitiesArrayList = amenitiesArrayList;
        this.screenId = screenId;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.amenities_recycler_item, parent, false);
        return new ViewHolder(view);
    }


    public void onBindViewHolder(ViewHolder holder, int position) {
        ProvidersBeanClass.Amenities amenitiesItem = amenitiesArrayList.get(position);

        String iconUri = amenitiesItem.getAmenitiesImageData();
        String nameText = amenitiesItem.getAmenityName();

        if(Utilities.isNotNull(iconUri)){
            ServiceProcessor.makeImageRequest(iconUri, holder.aminitiesIcon);
        }

        if(screenId == Common.INFO_SCREEN_ID){
            holder.amenityName.setVisibility(View.VISIBLE);
            if(Utilities.isNotNull(nameText)){
                holder.amenityName.setText(nameText);
            }
        }else {
            holder.amenityName.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return amenitiesArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView aminitiesIcon;
        TextView amenityName;
        public ViewHolder(View itemView) {
            super(itemView);
            aminitiesIcon = (ImageView) itemView.findViewById(R.id.amenitiesIcon);
            amenityName = (TextView) itemView.findViewById(R.id.amenityName);
        }
    }
}
