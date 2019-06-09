package com.ottice.ottice;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.bumptech.glide.Glide;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MyBooking_detailActivity extends AppCompatActivity implements View.OnClickListener {
    private Context context;
    private String transactioId, response_message, space_id,rating;
    private int responsecode;
    private TextView space_booked_by, space_booking_total_time,space_book_on_date,space_completion_time, space_price, space_trans_id, space_name,space_payment_mode,space_email_id,space_ph_no;
    private ImageView space_image;
    private CardView cardView;
    private RatingBar  ratingBar;
    private String review_text;
    private EditText edit_reviews;
    private String iscompleted;
    private float final_rating;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_booking_detail);

        init();

        if (Utilities.isConnectedToInternet(context)) {
            JSONObject jsonValue = requestbody();
            if (jsonValue != null) {
                getMybookingDetails(jsonValue);
            }
        } else {
            Toast.makeText(context, "Check Internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void init() {
        context = this;
        transactioId = getIntent().getStringExtra(Common.TRANSACTION_ID);
        rating = getIntent().getStringExtra(Common.RATING);

        space_book_on_date = (TextView) findViewById(R.id.booked_date);
        space_completion_time = (TextView) findViewById(R.id.space_completion_time);
        space_booking_total_time = (TextView) findViewById(R.id.booking_total_time);

        space_price = (TextView) findViewById(R.id.space_price);
        space_booked_by = (TextView) findViewById(R.id.space_booked_by_name);
        space_trans_id = (TextView) findViewById(R.id.space_transid);
        space_name = (TextView) findViewById(R.id.space_name);
        space_image = (ImageView) findViewById(R.id.image_booked);
        space_payment_mode = (TextView) findViewById(R.id.space_payment_mode);
        cardView = (CardView) findViewById(R.id.card_rate_reviews);
        cardView.setOnClickListener(this);
        ratingBar = (RatingBar) findViewById(R.id.rating_bar);
        space_email_id=(TextView)findViewById(R.id.space_booked_email_id);
        space_ph_no=(TextView)findViewById(R.id.space_booked_Ph_no);

        Utilities.initializeRatingBars(context, ratingBar);


        if (rating != null && !rating.equalsIgnoreCase("null")) {
            final_rating = Float.parseFloat(rating);
        } else {
            final_rating = 0;
        }
    }



    private JSONObject requestbody() {

        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();
        try {
            if (Common.deviceId != null) {
                headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
            }
            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);
            headerObject.put(ServiceConstants.KEY_Token, PrefrencesClass.getPreferenceValue(context, Common.TOKEN));

            mainObject.put(ServiceConstants.KEY_header, headerObject);
            mainObject.put(ServiceConstants.KEY_data, transactioId);
            return mainObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void getMybookingDetails(JSONObject Object) {

        final String tag_json_obj = "json_obj_req";
        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ServiceConstants.MY_BOOKING_DETAILS_URL, Object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("RESPONSE_DATA", "" + response);
                progressDialog.hideProgressBar();
                if (response != null) {
                    try {
                        JSONObject data = response.getJSONObject(ServiceConstants.KEY_response);
                        responsecode = data.getInt(ServiceConstants.KEY_ResponseCode);

                        if (responsecode == ServiceConstants.success_code) {
                            JSONArray response_array = response.getJSONArray(ServiceConstants.KEY_data);
                            if (response_array != null) {
                                for (int i = 0; i < response_array.length(); i++) {
                                    JSONObject items_object = response_array.getJSONObject(i);

                                    String name = items_object.getString(ServiceConstants.KEY_SpaceName);
                                    space_name.setText(name);

                                    String image = items_object.getString(ServiceConstants.KEY_ImageData);
                                    Glide.with(context).load(image).asBitmap().into(space_image);

                                    space_id = items_object.getString(ServiceConstants.KEY_spaceid);

                                    String from_date = items_object.getString(ServiceConstants.KEY_FromDate);
                                    String to_date = items_object.getString(ServiceConstants.KEY_ToDate);

                                    //setting json date format
                                    SimpleDateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss ", Locale.ENGLISH);
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy  hh:mm a ", Locale.ENGLISH);
                                    Date fromDate;
                                    Date toDate;

                                    try {
                                        fromDate = sourceFormat.parse(from_date);
                                        String date1 = dateFormat.format(fromDate);
                                        space_book_on_date.setText(date1);
                                        toDate = sourceFormat.parse(to_date);
                                        String date2 = dateFormat.format(toDate);
                                        space_completion_time.setText(date2);

                                    } catch (ParseException e) {
                                        Log.e("error", "" + e.getMessage());
                                    }

                                    String transid = items_object.getString(ServiceConstants.KEY_TransactionID);
                                    space_trans_id.setText(transid);

                                    String price = items_object.getString(ServiceConstants.KEY_amountpaid);
                                    space_price.append(price);

                                    String no_ofhours = items_object.getString(ServiceConstants.KEY_Duration);
                                    space_booking_total_time.setText(no_ofhours);

                                    String booked_by = items_object.getString(ServiceConstants.KEY_BOOKEDBY);
                                    space_booked_by.setText(booked_by);

                                    String pay_mode = items_object.getString(ServiceConstants.KEY_paymentmode);
                                    space_payment_mode.setText(pay_mode);

                                    String email=items_object.getString(ServiceConstants.KEY_Email);
                                    space_email_id.setText(email);
                                    String phone=items_object.getString(ServiceConstants.KEY_PhoneNo);
                                    space_ph_no.setText(phone);
                                    iscompleted=items_object.getString(ServiceConstants.KEY_iscompleted);
                                    ratingBar.setRating(final_rating);

                                }
                            } else {
                                Log.e("havenodata", "have_no_data");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.e("no response", "no response");
                }

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                progressDialog.hideProgressBar();

                Log.e("volleyerror", "" + error.getMessage());
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.card_rate_reviews:
                if (iscompleted.equalsIgnoreCase("true")){
                    dialog();
                }
                else {
                    Snackbar.make(view,"Booking is not completed yet",Snackbar.LENGTH_SHORT).show();
                }

                break;}
    }


    private void dialog() {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.review_dialog);
        dialog.setTitle("Write Reviews");

        edit_reviews = (EditText) dialog.findViewById(R.id.edit_reviews);
        final RatingBar dialog_rating_bar = (RatingBar) dialog.findViewById(R.id.dialog_rating_bar);

        Utilities.initializeRatingBars(context, dialog_rating_bar);
        dialog_rating_bar.setRating(final_rating);

        dialog_rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                final_rating = v;
            }
        });
        Button button_done = (Button) dialog.findViewById(R.id.butn_done);
        button_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dialog_rating_bar.getRating()!=0) {
                    if (Utilities.isConnectedToInternet(context)) {
                        JSONObject jsonValue = jsonrequestbody();
                        Log.e("json_res",""+jsonValue);
                        if (jsonValue != null) {
                            submitReviews(jsonValue);
                            dialog.dismiss();
                        }
                    } else {
                        Toast.makeText(context, "Check Internet connection", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(context, "rating is required", Toast.LENGTH_SHORT).show();
                }
            }
        });
        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

    }

    private JSONObject jsonrequestbody() {

        review_text = edit_reviews.getText().toString();


        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();
        JSONObject dataObject = new JSONObject();


        try {
            if (Common.deviceId != null) {
                headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
            }
            headerObject.put(ServiceConstants.KEY_UserId,PrefrencesClass.getPreferenceValue(context, Common.USERID));
            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);
            headerObject.put(ServiceConstants.KEY_Token, PrefrencesClass.getPreferenceValue(context, Common.TOKEN));

            dataObject.put(ServiceConstants.KEY_spaceid, space_id);
            dataObject.put(ServiceConstants.KEY_Title, "");
            dataObject.put(ServiceConstants.KEY_ReviewText, review_text);
            dataObject.put(ServiceConstants.KEY_Rating, final_rating);
            dataObject.put(ServiceConstants.KEY_transactionID,transactioId);


            mainObject.put(ServiceConstants.KEY_header, headerObject);
            mainObject.put(ServiceConstants.KEY_data, dataObject);

            return mainObject;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void submitReviews(JSONObject object) {

        final String tag_json_obj = "json_obj_req";

        Log.e("Json request", object.toString());

        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ServiceConstants.REVIEW_RATING, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("Review_RESponse", "" + response);
                progressDialog.hideProgressBar();
                if (response!=null) {

                    JSONObject data = null;
                    try {
                        data = response.getJSONObject(ServiceConstants.KEY_response);
                        responsecode = data.getInt(ServiceConstants.KEY_ResponseCode);
                        if (responsecode == 117) {
                            response_message=data.getString(ServiceConstants.KEY_ResponseMessage);
                            Toast.makeText(getApplicationContext(),""+response_message,Toast.LENGTH_SHORT).show();
                            JSONObject res=response.getJSONObject(ServiceConstants.KEY_data);
                            rating = res.getString("Rating");
                            final_rating = Float.parseFloat(rating);
                            ratingBar.setRating(final_rating);

                            setResult(RESULT_OK);

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("data", "" + data);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
                progressDialog.hideProgressBar();


            }
        });
        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }
}







