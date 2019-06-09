package com.ottice.ottice.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ottice.ottice.DashboardActivity;
import com.ottice.ottice.R;
import com.ottice.ottice.SelectLocationActivity;
import com.ottice.ottice.models.CommonItemsBeanClass;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */
public class SelectCityListAdapter extends RecyclerView.Adapter<SelectCityListAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommonItemsBeanClass> spinnerList;

    public SelectCityListAdapter(Context context, ArrayList<CommonItemsBeanClass> spinnerList){
        this.context = context;
        this.spinnerList = spinnerList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.city_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {

        String totalSpaces;
        final CommonItemsBeanClass spinnerItem = spinnerList.get(i);

        holder.cityName.setText(spinnerItem.getName());
        int totalSeats = spinnerItem.getTotalSpaces();
        if(totalSeats > 1) {
            totalSpaces = String.valueOf(totalSeats) + " Locations";
        }else {
            totalSpaces = String.valueOf(totalSeats) + " Location";
        }
        if(Utilities.isNotNull(totalSpaces)){
            holder.totalLocations.setText(totalSpaces);
        }


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.isConnectedToInternet(context)) {
                    int cityId = spinnerItem.getId();
                    String cityName = spinnerItem.getName();
                    if(cityId != -1) {
                        PrefrencesClass.savePreferenceInt(context,Common.CITY_ID, cityId);
                        PrefrencesClass.savePreference(context,Common.CITY_NAME, cityName);
                        Intent intent = new Intent(context, DashboardActivity.class);
                        context.startActivity(intent);
                    }
                }else {
                    Snackbar.make(view,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return spinnerList.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView cityName, totalLocations;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.cityItemView);
            cityName = (TextView)itemView.findViewById(R.id.cityName);
            totalLocations = (TextView)itemView.findViewById(R.id.totalSpacesAvailable);
        }
    }
}
