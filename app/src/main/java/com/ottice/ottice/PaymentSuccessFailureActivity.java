package com.ottice.ottice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

/**
 * TODO: Add a class header comment!
 */

public class PaymentSuccessFailureActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private RelativeLayout paymentResultLayout;
    private ImageView successFailureImage;
    private TextView successFailureMessage, subText;
    private Button okButton;
    private int spaceId;
    private String transctionId, paymentMode, payeeName;
    private double paidAmount;
    private long paymentReferenceId;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_success_failure_screen);

        init();

        Intent args = getIntent();
        String paymentResult = args.getStringExtra(Common.INTENT_KEY_paymentResult);
        spaceId = args.getIntExtra(Common.INTENT_KEY_SpaceId, 0);
        Log.e("PAYMENT RESULT",""+paymentResult);


        if(paymentResult != null){
            paymentResultLayout.setVisibility(View.GONE);
            try {
                JSONObject payuResponseObject = new JSONObject(paymentResult);
                String paymentStatus = payuResponseObject.getString("status");
                String lastname;
                try{
                    lastname = payuResponseObject.getString("lastname");
                }catch (JSONException e){
                    Log.e("LAST NAME",""+e.getMessage());
                    lastname = "";
                }

                Log.e("LST NAME",""+lastname);
                if(Utilities.isNotNull(lastname) && !lastname.equalsIgnoreCase("null")) {
                    payeeName = payuResponseObject.getString("firstname") + " " + lastname;
                }else {
                    payeeName = payuResponseObject.getString("firstname");
                }
                paidAmount = Double.parseDouble(payuResponseObject.getString("amount"));

                if(paymentStatus.equalsIgnoreCase("success")) {
                    transctionId = payuResponseObject.getString("txnid");
                    paymentMode = payuResponseObject.getString("mode");
                    paymentReferenceId = payuResponseObject.getLong("id");

                    successFailureImage.setImageResource(R.mipmap.success);
                    successFailureMessage.setText(getString(R.string.payment_successfull));
                    subText.setText("Hey "+payeeName+", space booking payment of Rs. "+paidAmount+" is successful.\n reference id : "+paymentReferenceId);

                    getPaymentStatus();
                }else if(paymentStatus.equalsIgnoreCase("failure")){

                    String errorMessage = payuResponseObject.getString("Error_Message");

                    successFailureImage.setImageResource(R.mipmap.failure);
                    successFailureMessage.setText(getString(R.string.payment_failure));
                    subText.setText("Sorry "+payeeName+", space booking payment of Rs. "+paidAmount+" is failed. Please Retry..\n Error : "+errorMessage);

                    paymentResultLayout.setVisibility(View.VISIBLE);
                }

            } catch (JSONException e) {
                Log.e("Last Name ERROR",""+e.getMessage());
            }
        }

    }

    private void init() {
        context = this;

        paymentResultLayout = (RelativeLayout) findViewById(R.id.paymentResultLayout);
        successFailureImage = (ImageView) findViewById(R.id.successFailureImage);
        successFailureMessage = (TextView) findViewById(R.id.successFailureMessageText);
        subText = (TextView) findViewById(R.id.successFailureSubText);
        okButton = (Button) findViewById(R.id.successFailureOkButton);

        okButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.successFailureOkButton:
                    finish();
                break;
        }
    }



    private void getPaymentStatus(){
        // getting data from service
        JSONObject jsonValue = getRequestBodyJson();
        if (jsonValue != null) {
            Log.e("JSON VALUE", jsonValue.toString());
            VolleyService(jsonValue, ServiceConstants.SPACE_PAYMENT_URL);
        }
    }



    // method for creating request body json object for service
    private JSONObject getRequestBodyJson() {
        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();
        JSONObject dataObject = new JSONObject();

       String notificationToken = PrefrencesClass.getPreferenceValue(context, Common.FIREBASE_NOTIFICATION_TOKEN);

        Log.e("TOKEN NOTIFICATION",""+notificationToken);
        try {
            if (notificationToken != null) {
                headerObject.put(ServiceConstants.KEY_DeviceId, notificationToken);
            }
            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);
            headerObject.put(ServiceConstants.KEY_UserId, PrefrencesClass.getPreferenceValue(context,Common.USERID));
            headerObject.put(ServiceConstants.KEY_Token, PrefrencesClass.getPreferenceValue(context, Common.TOKEN));

            if(spaceId != 0) {
                dataObject.put(ServiceConstants.KEY_spaceID, spaceId);
            }
            dataObject.put(ServiceConstants.KEY_transactionID, transctionId);
            dataObject.put(ServiceConstants.KEY_Total_Price, paidAmount);
            dataObject.put(ServiceConstants.KEY_Payment_Type, paymentMode);

            mainObject.put(ServiceConstants.KEY_header, headerObject);
            mainObject.put(ServiceConstants.KEY_data, dataObject);

            return mainObject;
        } catch (JSONException e) {
            Log.e("JSON REQUEST ERROR",""+e.getMessage());
            return null;
        }
    }




    // Volley service method for background tasks.
    private void VolleyService(JSONObject jsonObject, String Url) {
        Log.e("URL", Url);
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        // setting progress_bar dialog
        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        // Volley Method for requesting json type data
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Url, jsonObject,
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
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }



    private void getServiceValue(JSONObject response) {

        int responseCode;
        String responseMessage;

        if(response != null){
            try {
                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                responseCode = serverResponse.getInt(ServiceConstants.KEY_ResponseCode);
                responseMessage = serverResponse.getString(ServiceConstants.KEY_ResponseMessage);

                if(responseCode == ServiceConstants.success_code){
                    paymentResultLayout.setVisibility(View.VISIBLE);
                }else {
                    Snackbar.make(paymentResultLayout, responseMessage, Snackbar.LENGTH_INDEFINITE).setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getPaymentStatus();
                        }
                    }).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    // on Volley Error dialog popup
    public void retryDialog()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(context.getResources().getString(R.string.retry_dialog_title));
        alertDialog.setMessage(context.getResources().getString(R.string.retry_dialog_message));
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,context.getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getPaymentStatus();
            }
        });

        alertDialog.show();
    }

}
