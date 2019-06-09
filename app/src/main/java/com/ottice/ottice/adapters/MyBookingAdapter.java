package com.ottice.ottice.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ottice.ottice.R;
import com.ottice.ottice.models.BookingsBeanClass;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.services.ServiceProcessor;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class MyBookingAdapter extends RecyclerView.Adapter<MyBookingAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BookingsBeanClass> bookingsList;

    public MyBookingAdapter(Context context, ArrayList<BookingsBeanClass> bookingsList){
        this.context = context;
        this.bookingsList = bookingsList;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bookings_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BookingsBeanClass item = bookingsList.get(position);

        String imageUri = item.getImageUri();
        if(imageUri != null && !imageUri.equalsIgnoreCase("null")){
            ServiceProcessor.makeImageRequest(imageUri, holder.spaceImage);
        }

        holder.spaceName.setText(item.getSpaceName());
        String transcId = "Trans. Id : "+item.getTransactionId();
        holder.transactionId.setText(transcId);
        String stat = item.getStatus();
        holder.status.setText(stat);
        String period = "From - "+item.getFromDate()+"     To - "+item.getToDate();
        holder.bookingPeriod.setText(period);
    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View mainView;
        ImageView spaceImage;
        TextView spaceName, transactionId, status, bookingPeriod;
        public ViewHolder(View itemView) {
            super(itemView);

            mainView = itemView;
            spaceImage = (ImageView) itemView.findViewById(R.id.bookedSpaceImage);
            spaceName = (TextView) itemView.findViewById(R.id.bookedSpaceName);
            transactionId = (TextView) itemView.findViewById(R.id.bookedSpaceTransactionId);
            status = (TextView) itemView.findViewById(R.id.bookedSpaceStatus);
            bookingPeriod = (TextView) itemView.findViewById(R.id.bookingPeriod);
        }
    }
}
