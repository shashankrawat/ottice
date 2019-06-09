package com.ottice.ottice.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ottice.ottice.AppController;
import com.ottice.ottice.LoginActivity;
import com.ottice.ottice.R;
import com.ottice.ottice.UpdateProfileActivity;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.services.ServiceProcessor;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

/**
 * TODO: Add a class header comment!
 */

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private RelativeLayout noSignInLayout;
    private LinearLayout userProfileLayout;
    private Button signInButton;
    private AppBarLayout profileAppbar;
    private CircleImageView userProfileImage;
    private TextView userName, userEmail, userPhoneNo;
    private Button logoutButton;
    private String fname = "",mName = "", lname = "",email = "", phNo, imageUri;
    private ImageView userDetailsUpdateButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(container == null){
            return null;
        }else {
            view = inflater.inflate(R.layout.profile_screen, container, false);
            return view;
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        userProfileLayout = (LinearLayout) view.findViewById(R.id.userProfileLayout);
        noSignInLayout = (RelativeLayout) view.findViewById(R.id.profileNoSignInLayout);
        signInButton = (Button) view.findViewById(R.id.profileSignInButton);
        profileAppbar = (AppBarLayout) view.findViewById(R.id.profileAppBar);
        userProfileImage = (CircleImageView) view.findViewById(R.id.userProfileImage);
        userName = (TextView) view.findViewById(R.id.userName);
        userEmail = (TextView) view.findViewById(R.id.userEmail);
        userPhoneNo = (TextView) view.findViewById(R.id.userPhoneNo);
        logoutButton = (Button) view.findViewById(R.id.logoutButton);
        userDetailsUpdateButton = (ImageView) view.findViewById(R.id.userDetailsEditButton);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileAppbar.setPadding(0, Utilities.getStatusBarHeight(context), 0, 0);
        }

       checkIsLogin();

        signInButton.setOnClickListener(this);
        logoutButton.setOnClickListener(this);
        userDetailsUpdateButton.setOnClickListener(this);
    }


    private void checkIsLogin(){
        String userID = PrefrencesClass.getPreferenceValue(context, Common.USERID);
        if(Utilities.isNotNull(userID)) {
            noSignInLayout.setVisibility(View.GONE);
            userProfileLayout.setVisibility(View.VISIBLE);
            getUserDetails();
        }else{
            userProfileLayout.setVisibility(View.GONE);
            noSignInLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    private void getUserDetails(){
        // checking internet connection
        if(Utilities.isConnectedToInternet(getContext())) {
            // getting data from service
            JSONObject jsonValue = getRequestBodyJson();
            if (jsonValue != null) {
                Log.e("JSON VALUE", jsonValue.toString());
                VolleyService(jsonValue, ServiceConstants.GET_USER_DETAILS_URL);
            }
        }else {
            Snackbar.make(profileAppbar,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                    .setAction("Action",null).show();
        }
    }


    private void setUserInfo() {
        if(lname == null || lname.equalsIgnoreCase("null")){
            lname = "";
        }
        if(mName == null || mName.equalsIgnoreCase("null")){
            mName = "";
        }
        if(fname != null && !fname.equalsIgnoreCase("null")){
            userName.setText(fname+" "+mName+" "+lname);
        }
        if(email != null && !email.equalsIgnoreCase("null")){
            userEmail.setText(email);
        }

        if(phNo != null && !phNo.equalsIgnoreCase("null")){
            userPhoneNo.setText(phNo);
        }

        if(imageUri != null && !imageUri.equalsIgnoreCase("null")){
            ServiceProcessor.makeImageRequest(imageUri, userProfileImage);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.profileSignInButton:
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                break;

            case R.id.logoutButton:
                logoutConfirmationDialog();
                break;


            case R.id.userDetailsEditButton:
                Intent updateProfileIntent = new Intent(getActivity(), UpdateProfileActivity.class);
                updateProfileIntent.putExtra("firstName",fname);
                updateProfileIntent.putExtra("lastName", lname);
                updateProfileIntent.putExtra("middleName", mName);
                updateProfileIntent.putExtra("email", email);
                updateProfileIntent.putExtra("user_image", imageUri);
                updateProfileIntent.putExtra("user_phone", phNo);
                startActivityForResult(updateProfileIntent, Common.UPDATE_PROFILE_REQUEST);
                break;
        }
    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Common.UPDATE_PROFILE_REQUEST){
            if(resultCode == RESULT_OK){
                getUserDetails();
            }
        }
    }




    // method for creating request body json object for service
    private JSONObject getRequestBodyJson() {
        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();

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
                    JSONObject responseData = response.getJSONObject(ServiceConstants.KEY_data);
                    if (responseData != null) {
                        fname = responseData.getString(ServiceConstants.KEY_FirstName);
                        lname = responseData.getString(ServiceConstants.KEY_LastName);
                        mName = responseData.getString(ServiceConstants.KEY_MiddleName);
                        email = responseData.getString(ServiceConstants.KEY_Email);
                        phNo = responseData.getString(ServiceConstants.KEY_PhoneNo);
                        imageUri = responseData.getString(ServiceConstants.KEY_Imagedata);

                        PrefrencesClass.savePreference(context, Common.USER_FIRST_NAME, fname);
                        PrefrencesClass.savePreference(context, Common.USER_LAST_NAME, lname);
                        PrefrencesClass.savePreference(context, Common.USER_EMAIL, email);

                        setUserInfo();
                    }
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
                getUserDetails();
            }
        });

        alertDialog.show();
    }




    private void logoutConfirmationDialog() {
        final AlertDialog  dialog = new AlertDialog.Builder(context).create();
        dialog.setTitle(getString(R.string.logout));
        dialog.setMessage(getString(R.string.logout_confirmation_message));
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PrefrencesClass.savePreference(context, Common.USERID, null);
                PrefrencesClass.savePreference(context, Common.TOKEN, null);
                PrefrencesClass.savePreference(context, Common.USER_FIRST_NAME, null);
                PrefrencesClass.savePreference(context, Common.USER_LAST_NAME, null);
                PrefrencesClass.savePreference(context, Common.USER_EMAIL, null);

                Intent intent = new Intent(getContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }


}
