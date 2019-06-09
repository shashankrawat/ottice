package com.ottice.ottice.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ottice.ottice.AppController;
import com.ottice.ottice.R;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;
import com.wefika.horizontalpicker.HorizontalPicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * TODO: Add a class header comment!
 */

public class BookingDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = BookingDetailsFragment.class.getName();
    private Context context;
    private TextView bookingHeading, startDate, durationValue, fromValue, toValue, priceType, price, totalPrice, proceedButton;
    private HorizontalPicker seatsPicker;
    private String durationValueStr;
    private String planTypeStr, startDateStr, startTimeStr, checkoutTime;
    private int totalSeats, spaceId;
    private long priceAlloted;
    private CharSequence[] availableSeatsArray;
    private List<String> data;
    private int seatsSelected;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        planTypeStr = args.getString(Common.INTENT_KEY_PlanType);
        totalSeats = args.getInt(Common.INTENT_KEY_TotalSeats);
        priceAlloted = args.getLong(Common.INTENT_KEY_Price);
        spaceId = args.getInt(Common.INTENT_KEY_SpaceId);

        Log.e("SPACE ID",""+spaceId);


        if(totalSeats != 0){
            data = new ArrayList<>();
            for(int i=0; i<totalSeats; i++){
                data.add(String.valueOf(i+1));
            }
            availableSeatsArray = data.toArray(new CharSequence[data.size()]);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(container == null){
            return null;
        }else {
            view = inflater.inflate(R.layout.booking_details_screen, container, false);

            init(view);

            seatsPicker.setOnItemSelectedListener(new HorizontalPicker.OnItemSelected() {
                @Override
                public void onItemSelected(int index) {
                    calculateTotalPrice(Integer.parseInt(String.valueOf(availableSeatsArray[index])));
                }
            });

            return view;
        }
    }



    private void init(View view) {
        context = getContext();
        bookingHeading = (TextView) view.findViewById(R.id.bookingHeading);
        startDate = (TextView) view.findViewById(R.id.startDate);
        durationValue = (TextView) view.findViewById(R.id.durationValue);
        fromValue = (TextView) view.findViewById(R.id.fromValue);
        toValue = (TextView) view.findViewById(R.id.toValue);
        priceType = (TextView) view.findViewById(R.id.pricingType);
        price = (TextView) view.findViewById(R.id.priceValue);
        totalPrice = (TextView) view.findViewById(R.id.totalPrice);
        seatsPicker = (HorizontalPicker) view.findViewById(R.id.seatsSelector);
        proceedButton = (TextView) view.findViewById(R.id.proceedButton);

        setValues();

        proceedButton.setOnClickListener(this);
    }


    private void setValues() {
        if(Utilities.isNotNull(planTypeStr)){
            bookingHeading.setText(planTypeStr);
            priceType.setText(planTypeStr);
        }
        if(priceAlloted != 0){
            String priceText = "Rs "+priceAlloted;
            price.setText(priceText);
        }

        if(availableSeatsArray != null && availableSeatsArray.length != 0) {
            seatsPicker.setValues(availableSeatsArray);
        }

        if(Common.duration != 0){
            if(Common.duration == 1){
                if(planTypeStr.equalsIgnoreCase("Hourly")) {
                    durationValueStr = "(" + Common.duration + " hour)";
                }else if(planTypeStr.equalsIgnoreCase("Daily")){
                    durationValueStr = "(" + Common.duration + " day)";
                }else if(planTypeStr.equalsIgnoreCase("Monthly")){
                    durationValueStr = "(" + Common.duration + " month)";
                }else if(planTypeStr.equalsIgnoreCase("Yearly")){
                    durationValueStr = "(" + Common.duration + " year)";
                }
            }else if(Common.duration > 1){
                if(planTypeStr.equalsIgnoreCase("Hourly")) {
                    durationValueStr = "(" + Common.duration + " hours)";
                }else if(planTypeStr.equalsIgnoreCase("Daily")){
                    durationValueStr = "(" + Common.duration + " days)";
                }else if(planTypeStr.equalsIgnoreCase("Monthly")){
                    durationValueStr = "(" + Common.duration + " months)";
                }else if(planTypeStr.equalsIgnoreCase("Yearly")){
                    durationValueStr = "(" + Common.duration + " years)";
                }
            }
            durationValue.setText(durationValueStr);
        }


        if(Common.startDateTime != null){
            try {
                Date date = new SimpleDateFormat(Common.outputPattern, Locale.ENGLISH).parse(Common.startDateTime);
                startDateStr = getSelelectDateTime(date, Common.shownDateFormat);
                startDate.setText(startDateStr);

                startTimeStr = getSelelectDateTime(date, Common.shownTimeFormat);

                if(planTypeStr.equalsIgnoreCase("Hourly")) {
                    fromValue.setText("From: "+startTimeStr);

                }else if(planTypeStr.equalsIgnoreCase("Daily") || planTypeStr.equalsIgnoreCase("Monthly") || planTypeStr.equalsIgnoreCase("Yearly")){
                    fromValue.setText("From: "+startDateStr);
                }

                if(Common.duration != 0) {
                    checkoutTime = setCheckOutTime(date, Common.duration, planTypeStr);
                    toValue.setText("To: "+checkoutTime);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        if(priceAlloted != 0 && Common.duration != 0 && availableSeatsArray != null){
            calculateTotalPrice(Integer.parseInt(String.valueOf(availableSeatsArray[seatsPicker.getSelectedItem()])));
        }
    }

    private String getSelelectDateTime(Date dateTime, String Format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Format, Locale.ENGLISH);

        return dateFormat.format(dateTime);
    }


    private void calculateTotalPrice(int seatsSelected) {
        String totalPriceValue = "Rs "+ (priceAlloted * Common.duration * seatsSelected);
        totalPrice.setText(totalPriceValue);
    }

    private String setCheckOutTime(Date startingDate, int duration, String planType){

        Calendar cal;
        cal= Calendar.getInstance();
        cal.setTime(startingDate);
        if(planType.equalsIgnoreCase("Hourly")){
            cal.add(Calendar.HOUR, duration);
            return getSelelectDateTime(cal.getTime(), Common.shownTimeFormat);
        }else if (planType.equalsIgnoreCase("Daily")){
            cal.add(Calendar.DATE, duration);
            return getSelelectDateTime(cal.getTime(), Common.shownDateFormat);
        }else if (planType.equalsIgnoreCase("Monthly")){
            cal.add(Calendar.MONTH, duration);
            return getSelelectDateTime(cal.getTime(), Common.shownDateFormat);
        }else if (planType.equalsIgnoreCase("Yearly")){
            cal.add(Calendar.YEAR, duration);
            return getSelelectDateTime(cal.getTime(), Common.shownDateFormat);
        }else {
            return null;
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.proceedButton:
                if(Utilities.isConnectedToInternet(getContext())) {
                    JSONObject jsonValue = getRequestBodyJson();
                    if(jsonValue != null){
                        Log.e(TAG+" JSON",""+jsonValue);
                        VolleyService(jsonValue, ServiceConstants.INVOICE_URL);
                    }
                }else {
                    Snackbar.make(proceedButton,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                break;
        }
    }


    // method for creating request body json object for service
    private JSONObject getRequestBodyJson() {

         seatsSelected = Integer.parseInt(String.valueOf(availableSeatsArray[seatsPicker.getSelectedItem()]));

        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();
        JSONObject dataObject = new JSONObject();
        String tokenStr = PrefrencesClass.getPreferenceValue(context, Common.TOKEN);
        String userId = PrefrencesClass.getPreferenceValue(context, Common.USERID);

        try {
            if (Common.deviceId != null) {
                headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
            }
            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);
            if(tokenStr != null){
                headerObject.put(ServiceConstants.KEY_Token, tokenStr);
            }
            if(userId != null){
                headerObject.put(ServiceConstants.KEY_UserId, userId);
            }


            dataObject.put(ServiceConstants.KEY_spaceID, spaceId);
            dataObject.put(ServiceConstants.KEY_Seat, seatsSelected);
            dataObject.put(ServiceConstants.KEY_Space_Plan, planTypeStr);
            dataObject.put(ServiceConstants.KEY_Duration, Common.duration);
            dataObject.put(ServiceConstants.KEY_StartDateTime, Common.startDateTime);

            mainObject.put(ServiceConstants.KEY_header, headerObject);
            mainObject.put(ServiceConstants.KEY_data, dataObject);

            return mainObject;
        } catch (JSONException e) {
            Log.e(TAG,""+e.getMessage());
            return null;
        }
    }


    // Volley service method for background tasks.
    private void VolleyService(JSONObject jsonObject, String Url) {
        Log.e("URL", Url);
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("RESPONSE",""+response);
                        getServiceResponse(response);
                        progressDialog.hideProgressBar();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", "" + error.getMessage());
                progressDialog.hideProgressBar();
                Snackbar.make(proceedButton,R.string.volley_error_message,Snackbar.LENGTH_LONG)
                        .setAction("Action",null).show();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }

    private void getServiceResponse(JSONObject response) {

        Log.e("RESPONSE BOOKING",""+response);
        int responseCode;
        String responseMessage;

        if(response != null){
            try {
                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                responseCode = serverResponse.getInt(ServiceConstants.KEY_ResponseCode);
                responseMessage = serverResponse.getString(ServiceConstants.KEY_ResponseMessage);

                if(responseCode == ServiceConstants.success_code){
                    JSONObject responseData = response.getJSONObject(ServiceConstants.KEY_data);
                    if(responseData != null){
                        String transactionId = responseData.getString(ServiceConstants.KEY_TransactionID);
                        String spaceName = responseData.getString(ServiceConstants.KEY_SpaceName);
                        long netTotal = responseData.getLong(ServiceConstants.KEY_Net_Total);
                        long serviceTax = responseData.getLong(ServiceConstants.KEY_Service_Tax);
                        long sbCess = responseData.getLong(ServiceConstants.KEY_SBCess);
                        long kkCess = responseData.getLong(ServiceConstants.KEY_KKCess);
                        long totalAmount = responseData.getLong(ServiceConstants.KEY_Total);

                        Bundle args = new Bundle();

                        args.putInt("spaceID", spaceId);
                        args.putString("transactionID",transactionId);
                        args.putString("spaceName",spaceName);
                        args.putLong("netTotal",netTotal);
                        args.putLong("serviceTax",serviceTax);
                        args.putLong("sbCess",sbCess);
                        args.putLong("kkCess",kkCess);
                        args.putLong("totalAmount",totalAmount);
                        args.putString("bookingType",planTypeStr);
                        args.putInt("seatsBooked",seatsSelected);
                        args.putString("date",startDateStr);
                        args.putString("startTime",startTimeStr);
                        args.putString("checkoutTime",checkoutTime);

                        InvoiceScreenFragment invoiceFragment = new InvoiceScreenFragment();
                        invoiceFragment.setArguments(args);

                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                        transaction.replace(R.id.bookingContainer, invoiceFragment);
                        transaction.commit();
                    }
                }else {
                    Snackbar.make(proceedButton, responseMessage, Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
            } catch (JSONException e) {
                Log.e(TAG,""+e.getMessage());
            }
        }
    }
}
