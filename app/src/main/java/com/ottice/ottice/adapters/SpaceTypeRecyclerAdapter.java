package com.ottice.ottice.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.ottice.ottice.R;
import com.ottice.ottice.models.CommonItemsBeanClass;
import com.ottice.ottice.services.ServiceProcessor;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class SpaceTypeRecyclerAdapter extends RecyclerView.Adapter<SpaceTypeRecyclerAdapter.ViewHolder> {

    private Context context;
    private ArrayList<CommonItemsBeanClass> spaceTypeList;
    private RecyclerViewClickListener itemClickListener;
    private int previousSelected;

    public SpaceTypeRecyclerAdapter(Context context, ArrayList<CommonItemsBeanClass> spaceTypeList, RecyclerViewClickListener itemClickListener){
        this.context = context;
        this.spaceTypeList = spaceTypeList;
        this.itemClickListener = itemClickListener;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.filter_space_type_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final CommonItemsBeanClass spaceTyepItem = spaceTypeList.get(position);
        final int itemPosition = position;

        if(spaceTyepItem.isSelected()){
            previousSelected = position;
        }
        String typeName = spaceTyepItem.getName();
        String imageUri = spaceTyepItem.getImageUri();
        String planTypeName = spaceTyepItem.getPlanType();

        if(Utilities.isNotNull(typeName)){
            holder.spaceTypeName.setText(typeName);
        }
        if(Utilities.isNotNull(planTypeName)){
            planTypeName = "( "+planTypeName+" )";
            holder.planType.setText(planTypeName);
        }
        if(Utilities.isNotNull(imageUri)){
            ServiceProcessor.makeImageRequest(imageUri, holder.spaceTypeImage);
        }

        if(spaceTyepItem.isSelected()){
            holder.spaceTypeName.setTextColor(ContextCompat.getColor(context,R.color.color_d560a1));
            holder.planType.setTextColor(ContextCompat.getColor(context,R.color.color_d560a1));
        }else {
            holder.spaceTypeName.setTextColor(ContextCompat.getColor(context,R.color.color_414040));
            holder.planType.setTextColor(ContextCompat.getColor(context,R.color.color_414040));
        }

        if(position == getItemCount()-1){
            holder.divider.setVisibility(View.INVISIBLE);
        }


        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(itemClickListener != null){
                    itemClickListener.onRecyclerItemClick(spaceTyepItem);
                }
                if(itemPosition != previousSelected){
                    spaceTyepItem.setSelected(true);
                    spaceTypeList.get(previousSelected).setSelected(false);
                    previousSelected = itemPosition;
                }

                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return spaceTypeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public View view, divider;
        ImageView spaceTypeImage;
        TextView spaceTypeName, planType;

        ViewHolder(View itemView) {
            super(itemView);

            view = itemView.findViewById(R.id.spaceTypeItemLayout);
            spaceTypeImage = (ImageView)itemView.findViewById(R.id.spaceTypeImage);
            spaceTypeName  = (TextView) itemView.findViewById(R.id.spaceTypeName);
            divider = itemView.findViewById(R.id.spaceTypeItemDivider);
            planType = (TextView) itemView.findViewById(R.id.planType);
        }
    }


    public interface RecyclerViewClickListener{
        void onRecyclerItemClick(Object itemObject);
    }

}
