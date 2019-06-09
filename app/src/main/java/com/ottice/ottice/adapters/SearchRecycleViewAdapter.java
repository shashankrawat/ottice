package com.ottice.ottice.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ottice.ottice.R;
import com.ottice.ottice.SearchActivity;
import com.ottice.ottice.models.CommonItemsBeanClass;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */
public class SearchRecycleViewAdapter extends RecyclerView.Adapter<SearchRecycleViewAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<CommonItemsBeanClass> searchedCityList;

    public SearchRecycleViewAdapter(Activity context, ArrayList<CommonItemsBeanClass> searchedCityList){
        this.context = context;
        this.searchedCityList = searchedCityList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_list_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommonItemsBeanClass cityItem = searchedCityList.get(position);
        final String cityName = cityItem.getName().trim();
        final int cityId = cityItem.getId();

        holder.searchCityName.setText(cityName);

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utilities.isConnectedToInternet(context)) {
                    Log.e("CITY DETAILS", "ID:" + cityId + "\nName:" + cityName);
                    PrefrencesClass.savePreferenceInt(context,Common.CITY_ID, cityId);
                    PrefrencesClass.savePreference(context,Common.CITY_NAME, cityName);
                    Intent intent = new Intent();
                    intent.putExtra(Common.INTENT_KEY_Latitude, 0);
                    intent.putExtra(Common.INTENT_KEY_Longitude, 0);
                    context.setResult(Activity.RESULT_OK, intent);
                    Utilities.hideKeyboard(context, view);
                    context.onBackPressed();
                }else {
                    Snackbar.make(view,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchedCityList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        TextView searchCityName;

        public ViewHolder(View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.searchedItemlayout);
            searchCityName = (TextView) itemView.findViewById(R.id.searchCityName);
        }
    }
}
