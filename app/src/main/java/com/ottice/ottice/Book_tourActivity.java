package com.ottice.ottice;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

public class Book_tourActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText first_name_edit_text, last_name_edit_text, phone_edit_text;
    private TextView time_picker_text, date_picker_text;
    Button button_submit;
    private String FirstName, LastName, MobileNumber, datee, time;
    Context context;
    String tokenStr;
    private String response_message;
    private int responsecode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_tour);
        init();


    }

    private void init() {

        context = this;

        first_name_edit_text = (EditText) findViewById(R.id.booking_first_name);
        last_name_edit_text = (EditText) findViewById(R.id.booking_last_name);
        phone_edit_text = (EditText) findViewById(R.id.booking_ph_no);
        time_picker_text = (TextView) findViewById(R.id.time_text_picker);
        date_picker_text = (TextView) findViewById(R.id.date_text_picker);
        button_submit = (Button) findViewById(R.id.submit_button);

        tokenStr = PrefrencesClass.getPreferenceValue(context, Common.TOKEN);

//        FirstName = PrefrencesClass.getPreferenceValue(context,Common.USER_FIRST_NAME);
//        LastName = PrefrencesClass.getPreferenceValue(context,Common.USER_LAST_NAME);

//        first_name_edit_text.setText(FirstName);
//        last_name_edit_text.setText(LastName);


        date_picker_text.setOnClickListener(this);
        time_picker_text.setOnClickListener(this);
        button_submit.setOnClickListener(this);

        Common.startDateTime = Utilities.serviceTypeDateAndTimeFormat(Common.startDateStr + " " + Common.startTimeStr);


    }

    private void submitProviderDetails() {

        // getting data from service

        JSONObject jsonValue = getRequestBodyJson();

        if (jsonValue != null) {
            Log.e("JSON VALUE", jsonValue.toString());
            VolleyService(jsonValue, ServiceConstants.BOOK_A_SERVICE_URL);
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.time_text_picker:

                java.util.Calendar mcurrentTime = java.util.Calendar.getInstance();
                final int hour = mcurrentTime.get(java.util.Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(java.util.Calendar.MINUTE);

                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Book_tourActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {


                        boolean isPM = (selectedHour >= 12);
                        time_picker_text.setText(String.format("%02d:%02d %s", (selectedHour == 12 || selectedHour == 0) ? 12 : selectedHour % 12, selectedMinute, isPM ? "PM" : "AM"));


                    }
                }, hour, minute, false);

                mTimePicker.show();
                break;

            case R.id.date_text_picker:

                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        date, java.util.Calendar.YEAR, java.util.Calendar.MONTH, Calendar.DAY_OF_MONTH);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                datePickerDialog.show();


                break;

            case R.id.submit_button:

                FirstName = first_name_edit_text.getText().toString().trim();
                LastName = last_name_edit_text.getText().toString().trim();

                //setting pref name in textview

                MobileNumber = phone_edit_text.getText().toString().trim();
                datee = date_picker_text.getText().toString();
                time = time_picker_text.getText().toString();

                if (TextUtils.isEmpty(FirstName) || TextUtils.isEmpty(LastName) || TextUtils.isEmpty(MobileNumber)||MobileNumber.length()<10) {

                    Snackbar.make(view, "Please fill all mandatory fields", Snackbar.LENGTH_SHORT).show();
                }
                else {
                    if (TextUtils.isEmpty(datee) || TextUtils.isEmpty(time)) {
                        Snackbar.make(view, "Please select date and time of tour.", Snackbar.LENGTH_SHORT).show();
                    }
                    else {
                        if (Utilities.isConnectedToInternet(Book_tourActivity.this)) {
                            submitProviderDetails();
                        } else {
                            Snackbar.make(view, "No internet connection. Please check your connection and try again..", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                }
        }
    }

    final java.util.Calendar calendar = java.util.Calendar.getInstance();

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

            calendar.set(java.util.Calendar.YEAR, i);
            calendar.set(java.util.Calendar.MONTH, i1);
            calendar.set(java.util.Calendar.DAY_OF_MONTH, i2);

            Date selected_date = calendar.getTime();

            String datestr = Common.showingDateFormat.format(selected_date);
            date_picker_text.setText(datestr);

        }
    };


    private JSONObject getRequestBodyJson() {

        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();
        JSONObject dataObject = new JSONObject();

        try {
            if (Common.deviceId != null) {
                headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
            }
            if (Common.USERID != null) {
                headerObject.put(ServiceConstants.KEY_UserId, Common.USERID);
            }
            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);

            if (tokenStr != null) {
                headerObject.put(ServiceConstants.KEY_Token, tokenStr);
            }

            dataObject.put(ServiceConstants.KEY_FirstName, FirstName);
            dataObject.put(ServiceConstants.KEY_LastName, LastName);
            dataObject.put(ServiceConstants.KEY_mobileno, MobileNumber);
            dataObject.put(ServiceConstants.KEY_datetime, Common.startDateTime);

            mainObject.put(ServiceConstants.KEY_header, headerObject);
            mainObject.put(ServiceConstants.KEY_data, dataObject);


            return mainObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void VolleyService(final JSONObject jsonObject, String url) {

        Log.e("Url", "" + url);

        String tag_json_obj = "json_obj_req";

        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("SERVICE_RESPONSE", response.toString());
                        Onresponse(response);
                        progressDialog.hideProgressBar();


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hideProgressBar();
                Snackbar.make(button_submit,error.getMessage(),Snackbar.LENGTH_SHORT).show();
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);


    }
    private void dialog(){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
        builder1.setMessage(response_message);
        builder1.setCancelable(false);
        builder1.setNegativeButton(
                "ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();

    }



    private void Onresponse(JSONObject response) {
        if (response != null) {

            try {
                //getting json responsecode
                JSONObject object = response.getJSONObject(ServiceConstants.KEY_response);
                responsecode = object.getInt(ServiceConstants.KEY_ResponseCode);
                response_message = object.getString(ServiceConstants.KEY_ResponseMessage);

                if (responsecode == 116) {
                    dialog();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


}










