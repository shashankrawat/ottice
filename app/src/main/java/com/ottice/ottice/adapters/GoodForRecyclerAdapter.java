package com.ottice.ottice.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ottice.ottice.R;
import com.ottice.ottice.models.CommonItemsBeanClass;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class GoodForRecyclerAdapter extends RecyclerView.Adapter<GoodForRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommonItemsBeanClass> goodForList;

    public GoodForRecyclerAdapter(Context context, ArrayList<CommonItemsBeanClass> goodForList){
        this.context = context;
        this.goodForList = goodForList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.good_for_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CommonItemsBeanClass item = goodForList.get(position);

        holder.item.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return goodForList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView item;

        public ViewHolder(View itemView) {
            super(itemView);
            item = (TextView) itemView.findViewById(R.id.goodforItem);
        }
    }
}
