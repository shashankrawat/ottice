package com.ottice.ottice.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ottice.ottice.AppController;
import com.ottice.ottice.R;
import com.ottice.ottice.SelectLocationActivity;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shashank.rawat on 14-07-2017.
 */

public class UserDetailsFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = UserDetailsFragment.class.getName();
    private Context context;
    private EditText firstName, lastName, email;
    private Button saveButton;
    private String userPhoneNumber;
    private String userFirstName, userLastName, userEmail, userId, userToken;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        userPhoneNumber = args.getString("user_phone_no");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(container == null){
            return null;
        }else {
            view = inflater.inflate(R.layout.user_details_input_screen, container, false);

            return view;
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        context = getContext();
        firstName = (EditText) view.findViewById(R.id.firstName);
        lastName = (EditText) view.findViewById(R.id.lastName);
        email = (EditText) view.findViewById(R.id.email);
        saveButton = (Button) view.findViewById(R.id.saveButton);


        saveButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.saveButton:
                registerUser();
                break;
        }
    }



    private void registerUser(){
        if(Utilities.isConnectedToInternet(getContext())) {
            JSONObject jsonValue = getRequestBodyJson();
            if(jsonValue != null){
                VolleyService(jsonValue, ServiceConstants.MOBILE_LOGIN_URL);
            }
        }else {
            Snackbar.make(saveButton,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                    .setAction("Action",null).show();
        }
    }



    // method for creating request body json object for service
    private JSONObject getRequestBodyJson() {
        String fname = firstName.getText().toString();
        String lname = lastName.getText().toString();
        String emailText = email.getText().toString();

        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();
        JSONObject dataObject = new JSONObject();

        // checking mandatory fields are filled or not.
        if(Utilities.isNotNull(fname) && Utilities.isNotNull(emailText)){
                if (Utilities.validate(emailText)) {
                    try {
                        dataObject.put(ServiceConstants.KEY_FirstName, fname);
                        dataObject.put(ServiceConstants.KEY_LastName, lname);
                        dataObject.put(ServiceConstants.KEY_PhoneNo, userPhoneNumber);
                        dataObject.put(ServiceConstants.KEY_Email, emailText);
                        dataObject.put(ServiceConstants.KEY_UserType, ServiceConstants.user_type);

                        if (Common.deviceId != null) {
                            headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
                        }
                        headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);

                        mainObject.put(ServiceConstants.KEY_header, headerObject);
                        mainObject.put(ServiceConstants.KEY_data, dataObject);

                        return mainObject;
                    } catch (JSONException e) {
                        Log.e(TAG, "" + e.getMessage());
                        return null;
                    }
                } else {
                    Snackbar.make(saveButton, "Email address is not valid", Snackbar.LENGTH_LONG).show();
                    return null;
                }
        }else {
            Snackbar.make(saveButton,"Please fill the mandatory fields.", Snackbar.LENGTH_LONG).show();
            return null;
        }
    }


    // Volley service method for background tasks.
    private void VolleyService(JSONObject jsonObject, String Url) {
        Log.e("URL", Url);
        Log.e("REQUEST BODY",""+jsonObject);
        // Tag used to cancel the request
        final String tag_json_obj = "json_obj_req";

        // progress bar.
        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getServiceResponse(response);
                        progressDialog.hideProgressBar();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", "" + error.getMessage());
                progressDialog.hideProgressBar();
                Snackbar.make(saveButton,R.string.volley_error_message,Snackbar.LENGTH_LONG)
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




    private void getServiceResponse(JSONObject response)
    {
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
                        userFirstName = responseData.getString(ServiceConstants.KEY_FirstName);
                        userLastName = responseData.getString(ServiceConstants.KEY_LastName);
                        userEmail = responseData.getString(ServiceConstants.KEY_Email);
                    }

                    userToken = serverResponse.getString(ServiceConstants.KEY_Token);

                    Log.e("TOKEN",""+userToken);

                    JSONObject responseHeader = response.getJSONObject(ServiceConstants.KEY_header);
                    userId = responseHeader.getString(ServiceConstants.KEY_UserId);

                    saveSignupDetails();
                }else {
                    Snackbar.make(saveButton, responseMessage, Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
            } catch (JSONException e) {
                Log.e(TAG,""+e.getMessage());
            }
        }
    }



    private void saveSignupDetails()
    {
        PrefrencesClass.savePreference(context, Common.USERID, userId);
        PrefrencesClass.savePreference(context, Common.TOKEN, userToken);
        PrefrencesClass.savePreference(context, Common.USER_FIRST_NAME, userFirstName);
        PrefrencesClass.savePreference(context, Common.USER_LAST_NAME, userLastName);
        PrefrencesClass.savePreference(context, Common.USER_EMAIL, userEmail);

        Intent intent = new Intent(getContext(), SelectLocationActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}
