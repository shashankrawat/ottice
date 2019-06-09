package com.ottice.ottice.fragments;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ottice.ottice.AppController;
import com.ottice.ottice.LoginActivity;
import com.ottice.ottice.MyBooking_detailActivity;
import com.ottice.ottice.R;
import com.ottice.ottice.adapters.MyBookingAdapter;
import com.ottice.ottice.models.BookingsBeanClass;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.NotificationUtils;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.RecyclerItemClickListener;
import com.ottice.ottice.utils.RecyclerViewItemMargin;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

/**
 * TODO: Add a class header comment!
 */

public class MyBookingsFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private RecyclerView bookingsRecycler;
    private RelativeLayout noSignInLayout;
    private Button signInButton;
    private AppBarLayout bookingAppbar;
    private TextView noBookingText;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private  BookingsBeanClass item;
    private ArrayList<BookingsBeanClass> bookedItemList;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.e("DASHBOARD RECEIVER","Received in dashboard");
                Log.e("intent",""+intent.getStringExtra("message"));
                getBookings();
            }
        };
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(container == null){
            return null;
        }else {
            view = inflater.inflate(R.layout.my_bookings_screen, container, false);
            return view;
        }
    }


    @Override
    public void onResume() {
        AppController.currentFragment = new WeakReference<Fragment>(this);
        super.onResume();


        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Common.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getContext());

        String userID = PrefrencesClass.getPreferenceValue(context, Common.USERID);
        if(Utilities.isNotNull(userID)) {
            PrefrencesClass.savePreferenceBoolean(getContext(), Common.NOTIFICATION_DATA_RECEIVER, false);
            getBookings();
        }else{
            bookingsRecycler.setVisibility(View.GONE);
            noSignInLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onPause() {
        AppController.currentFragment = null;
        super.onPause();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mRegistrationBroadcastReceiver);

    }



    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        bookingsRecycler = (RecyclerView) view.findViewById(R.id.myBookingRecycler);
        noSignInLayout = (RelativeLayout) view.findViewById(R.id.noSignInLayout);
        signInButton = (Button) view.findViewById(R.id.myBookingSignInButton);
        bookingAppbar = (AppBarLayout) view.findViewById(R.id.myBookingAppBar);
        noBookingText = (TextView) view.findViewById(R.id.noBookingsText);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bookingAppbar.setPadding(0, Utilities.getStatusBarHeight(context), 0, 0);
        }

        bookingsRecycler.setHasFixedSize(true);
        bookingsRecycler.setLayoutManager(new LinearLayoutManager(context));
        bookingsRecycler.addItemDecoration(new RecyclerViewItemMargin(getResources().getDimensionPixelOffset(R.dimen.dp_10),
                getResources().getDimensionPixelOffset(R.dimen.dp_10),
                getResources().getDimensionPixelOffset(R.dimen.dp_10),
                getResources().getDimensionPixelOffset(R.dimen.dp_10)));

        bookingsRecycler.addOnItemTouchListener(new RecyclerItemClickListener(context, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent=new Intent(context, MyBooking_detailActivity.class);
                intent.putExtra(Common.TRANSACTION_ID, bookedItemList.get(position).getTransactionId());
                intent.putExtra(Common.RATING, bookedItemList.get(position).getRating());
                startActivity(intent);

            }
        }));

        signInButton.setOnClickListener(this);
    }



    private void setUpRecyclerView(){
        noBookingText.setVisibility(View.GONE);
        bookingsRecycler.setVisibility(View.VISIBLE);

        MyBookingAdapter  adapter = new MyBookingAdapter(context, bookedItemList);
        bookingsRecycler.setAdapter(adapter);
    }


    private void getBookings(){
        noSignInLayout.setVisibility(View.GONE);
        // checking internet connection
        if(Utilities.isConnectedToInternet(getContext())) {
            // getting data from service

            JSONObject jsonValue = getRequestBodyJson();
            if (jsonValue != null) {
                Log.e("JSON VALUE", jsonValue.toString());
                VolleyService(jsonValue, ServiceConstants.MY_BOOKINGS_URL);
            }
        }else {
            checkInternetConnection();
        }
    }




    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.myBookingSignInButton:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
        }
    }




    // on No internet connection dialog popup
    private void checkInternetConnection() {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(context.getResources().getString(R.string.no_internet_connection));
        alertDialog.setMessage(context.getResources().getString(R.string.no_connection_message));
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, context.getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isConnectedToInternet(context)) {
                    dialog.dismiss();
                    getBookings();
                } else {
                    dialog.dismiss();
                    checkInternetConnection();
                }
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    private JSONObject getRequestBodyJson() {

        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();
        String token=PrefrencesClass.getPreferenceValue(context, Common.TOKEN);
        Log.e("token_value",""+token);

        try {
            if (Common.deviceId != null) {
                headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
            }
            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);
            headerObject.put(ServiceConstants.KEY_Token, PrefrencesClass.getPreferenceValue(context, Common.TOKEN));

            mainObject.put(ServiceConstants.KEY_header, headerObject);

            return mainObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }


    private void VolleyService(JSONObject jsonObject, String url) {
        Log.e("URL", url);
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        // setting progress_bar dialog
        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        // Volley Method for requesting json type data
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("SERVICE RESPONSE", response.toString());
                        getServiceValue(response);
                        progressDialog.hideProgressBar();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", "" + error.getMessage());
                // hide the progress_bar dialog
                progressDialog.hideProgressBar();
                retryDialog();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }



    private void getServiceValue(JSONObject response) {
        int responseCode;

        if(response != null){
            try {
                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                responseCode = serverResponse.getInt(ServiceConstants.KEY_ResponseCode);

                if (responseCode == ServiceConstants.success_code) {
                    JSONArray responseData = response.getJSONArray(ServiceConstants.KEY_data);
                    int size = responseData.length();
                    Log.e("size",""+size);
                    if (size > 0) {
                        bookedItemList = new ArrayList<>();
                        for(int i=0; i<size; i++){
                            JSONObject itemObject = responseData.getJSONObject(i);

                            item = new BookingsBeanClass();

                            item.setImageUri(itemObject.getString(ServiceConstants.KEY_ImageData));
                            item.setSpaceName(itemObject.getString(ServiceConstants.KEY_SpaceName));
                            item.setStatus(itemObject.getString(ServiceConstants.KEY_Status));
                            item.setTransactionId(itemObject.getString(ServiceConstants.KEY_TransactionID));
                            item.setRating(itemObject.getString(ServiceConstants.KEY_Rating));
                            item.setFromDate(itemObject.getString(ServiceConstants.KEY_FromDate));
                            item.setToDate(itemObject.getString(ServiceConstants.KEY_ToDate));

                            bookedItemList.add(item);
                        }
                        setUpRecyclerView();
                    }
                    else {
                        noBookingText.setVisibility(View.VISIBLE);
                        bookingsRecycler.setVisibility(View.GONE);
                    }
                }else {
                    noBookingText.setVisibility(View.VISIBLE);
                    bookingsRecycler.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }




    // on Volley Error dialog popup
    private void retryDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(context.getResources().getString(R.string.retry_dialog_title));
        alertDialog.setMessage(context.getResources().getString(R.string.retry_dialog_message));
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,context.getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getBookings();
            }
        });

        alertDialog.show();
    }


}
