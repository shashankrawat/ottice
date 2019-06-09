package com.ottice.ottice.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shashank.rawat on 14-07-2017.
 */

public class LoginFragment extends Fragment implements View.OnClickListener {

    private final static String TAG = LoginFragment.class.getSimpleName();
    private Context context;
    private EditText phoneNumberET, otpCodeET;
    private Button otpGenVerfyBtn;
    private boolean isOtpGenerated = false;
    private RelativeLayout fbLoginButton;
    private CallbackManager fbCallbackManager;
    private AccessToken accessToken;
    private String userFirstName, userLastName, userEmail, userId, userToken;
    private static final int mobileLoginType = 1, fbLoginType = 2;
    private ImageView reloadButton;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // facebook sdk initialization
        FacebookSdk.sdkInitialize(getContext());
//        AppEventsLogger.activateApp(this);
        fbCallbackManager = CallbackManager.Factory.create();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view;
        if(container == null){
            return null;
        }else {
            view = inflater.inflate(R.layout.login_layout, container, false);

            return view;
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        context = getContext();
        phoneNumberET = (EditText) view.findViewById(R.id.userNumber);
        otpCodeET = (EditText) view.findViewById(R.id.otpCodeET);
        otpGenVerfyBtn = (Button) view.findViewById(R.id.otpGenVerfyBtn);
        fbLoginButton = (RelativeLayout) view.findViewById(R.id.signInFBLoginButton);
        reloadButton = (ImageView) view.findViewById(R.id.reEnterNumberButton);

        otpGenVerfyBtn.setOnClickListener(this);
        fbLoginButton.setOnClickListener(this);
        reloadButton.setOnClickListener(this);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.otpGenVerfyBtn:
                if(Utilities.isConnectedToInternet(context)) {
                    if (!isOtpGenerated) {
                        if (Utilities.isNotNull(phoneNumberET.getText().toString())) {
                            if (phoneNumberET.getText().length() < 10) {
                                Snackbar.make(otpGenVerfyBtn, "Phone number is not valid.", Snackbar.LENGTH_LONG).show();
                            } else {
                                otpGenerateService();
                                phoneNumberET.setEnabled(false);
                            }

                        } else {
                            Snackbar.make(otpGenVerfyBtn, "Please enter phone number.", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        if (Utilities.isNotNull(otpCodeET.getText().toString())) {
                            otp_Verification();
                            Utilities.hideKeyboard(context, otpGenVerfyBtn);
                        } else {
                            Snackbar.make(otpGenVerfyBtn, "OTP is not valid.", Snackbar.LENGTH_LONG).show();
                        }
                    }
                }else {
                    Snackbar.make(otpGenVerfyBtn, R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
                }
                break;

            case R.id.signInFBLoginButton:
                if(Utilities.isConnectedToInternet(context)) {
                    facebookLogin();
                }else {
                    Snackbar.make(otpGenVerfyBtn, R.string.no_connection_message, Snackbar.LENGTH_LONG).show();
                }
                break;


            case R.id.reEnterNumberButton:
                reloadButton.setVisibility(View.INVISIBLE);
                otpCodeET.setVisibility(View.GONE);
                phoneNumberET.setEnabled(true);
                phoneNumberET.setText("");
                otpCodeET.setText("");
                phoneNumberET.requestFocus();
                otpGenVerfyBtn.setText(getString(R.string.send_otp));
                isOtpGenerated = false;
                break;
        }
    }




    private void otpGenerateService() {

        final String tag_json_obj = "json_obj_req";

        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();
        String user_text_number = phoneNumberET.getText().toString().trim();
        StringBuilder sbPostData = new StringBuilder(ServiceConstants.URL);
        sbPostData.append("authkey=" + ServiceConstants.AUTH_KEY);
        sbPostData.append("&mobiles=" + user_text_number);
        sbPostData.append("&message=" + "");
        sbPostData.append("&route=" + ServiceConstants.route);
        sbPostData.append("&sender=" + ServiceConstants.SENDER_ID);
        String data = sbPostData.toString();
        Log.e("data", "" + data);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, data, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("RESponse", "" + response);
                progressDialog.hideProgressBar();
                try {
                    String responseType = response.getString("type");
                    Log.e("sucess", "" + responseType);
                    if (responseType.equalsIgnoreCase("success")) {
//                        reloadButton.setVisibility(View.VISIBLE);
                        otpCodeET.setVisibility(View.VISIBLE);
                        otpCodeET.requestFocus();
                        otpGenVerfyBtn.setText(getString(R.string.verify));
                        isOtpGenerated = true;
                    } else {
                        Snackbar.make(otpGenVerfyBtn, response.getString("message"), Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG,"JSON ERROR"+e.getMessage());
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", "" + error.getMessage());
                progressDialog.hideProgressBar();


            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("application-Key", ServiceConstants.KEY_application);
                return params;
            }
        };


        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                20,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }





    private void otp_Verification() {

        final String tag_json_obj = "json_obj_req";
        //   Log.e("Json request", object.toString());

        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();
        String user_text_otp = otpCodeET.getText().toString();
        String user_no = phoneNumberET.getText().toString();

        StringBuilder sbPostData = new StringBuilder(ServiceConstants.VERIFY_URL);
        sbPostData.append("authkey=" + ServiceConstants.AUTH_KEY);
        sbPostData.append("&mobile=" + user_no);
        sbPostData.append("&otp=" + user_text_otp);
        String data = sbPostData.toString();
        Log.e("data", "" + data);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, data, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressDialog.hideProgressBar();
                try {
                    String type = response.getString("type");

                    if (type.equalsIgnoreCase("success")) {
                        checkPhoneIsRegisteredOrNot();
                    } else {
                        String message = response.getString("message").replaceAll("_"," ");
                        Snackbar.make(otpGenVerfyBtn, message, Snackbar.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    Log.e(TAG,"JSON ERROR : "+e.getMessage());
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error : " + error.getMessage());
                progressDialog.hideProgressBar();

            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                20,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        AppController.getInstance().addToRequestQueue(jsonObjectRequest, tag_json_obj);
    }





    private void checkPhoneIsRegisteredOrNot(){

        try {
            JSONObject mainObject = new JSONObject();
            JSONObject headerObject = new JSONObject();

            if (Common.deviceId != null) {
                headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
            }
            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);

            mainObject.put(ServiceConstants.KEY_header, headerObject);
            mainObject.put(ServiceConstants.KEY_data, phoneNumberET.getText().toString());

            VolleyService(mainObject, ServiceConstants.SEARCH_USER_BY_MOBILE_NO_URL, mobileLoginType);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    private void facebookLogin() {
        fbLoginButton.setEnabled(false);
        List<String> permissionNeeds = Arrays.asList(context.getResources().getStringArray(R.array.permissions));

        LoginManager.getInstance().logInWithReadPermissions(getActivity(), permissionNeeds);

        LoginManager.getInstance().registerCallback(fbCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                Log.e("Authentication Token : ", "" + loginResult.getAccessToken().getToken());

                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try {
                            Log.e("Facebook Object", "" + object.toString());
                            JSONObject mainObject = new JSONObject();
                            JSONObject dataObject = new JSONObject();
                            JSONObject headerObject = new JSONObject();

                            if (Common.deviceId != null) {
                                headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
                            }
                            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);

                            dataObject.put(ServiceConstants.KEY_FBID, object.getString("id"));
                            dataObject.put(ServiceConstants.KEY_FirstName, object.getString("first_name"));
                            dataObject.put(ServiceConstants.KEY_Lastname, object.getString("last_name"));
                            dataObject.put(ServiceConstants.KEY_Email, object.getString("email"));
                            dataObject.put(ServiceConstants.KEY_UserType, ServiceConstants.user_type);

                            mainObject.put(ServiceConstants.KEY_header, headerObject);
                            mainObject.put(ServiceConstants.KEY_data, dataObject);

                            VolleyService(mainObject, ServiceConstants.SOCIAL_LOGIN_URL, fbLoginType);

                            fbLoginButton.setEnabled(true);
//                            imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                        } catch (Exception e) {
                            Snackbar.make(fbLoginButton, context.getString(R.string.fb_error), Snackbar.LENGTH_LONG).show();
                            Log.e("FB JSON ERROR",""+e.getMessage());
                            fbLoginButton.setEnabled(true);
                        }
                    }
                });


                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email,gender,birthday,picture");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Snackbar.make(fbLoginButton, context.getString(R.string.fb_login_cancelled), Snackbar.LENGTH_LONG).show();
                Log.e("FBLogin Canceled", "Facebook Login failed!!");
                fbLoginButton.setEnabled(true);
            }

            @Override
            public void onError(FacebookException error) {
                Snackbar.make(fbLoginButton, context.getString(R.string.fb_login_unsuccessful), Snackbar.LENGTH_LONG).show();
                Log.e("FBLogin Error", "Facebook Login failed!!"+ error.getMessage());
                fbLoginButton.setEnabled(true);
            }
        });
    }



    // Volley service method for background tasks.
    private void VolleyService(JSONObject jsonObject, String Url, final int type) {
        Log.e("URL", Url);
        Log.e("REQUEST BODY",""+jsonObject);
        // Tag used to cancel the request
        String tag_json_obj = "json_obj_req";

        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hideProgressBar();
                        Log.e("RESPONSE",""+response);
                        if(type == fbLoginType) {
                            getServiceResponse(response);
                        }
                        if(type == mobileLoginType){
                            try {
                                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                                if(serverResponse.getInt(ServiceConstants.KEY_ResponseCode) == ServiceConstants.success_code){
                                    getServiceResponse(response);
                                }else if(serverResponse.getInt(ServiceConstants.KEY_ResponseCode) == ServiceConstants.no_user_found_code){
                                    Bundle args = new Bundle();
                                    args.putString("user_phone_no",phoneNumberET.getText().toString());

                                    UserDetailsFragment userDetailsFrg = new UserDetailsFragment();
                                    userDetailsFrg.setArguments(args);

                                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                    transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                                    transaction.replace(R.id.container, userDetailsFrg);
                                    transaction.commit();
                                }else {
                                    Snackbar.make(otpGenVerfyBtn, serverResponse.getString(ServiceConstants.KEY_ResponseMessage), Snackbar.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                Log.e(TAG,"JSON ERROR : "+e.getMessage());
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", "" + error.getMessage());
                progressDialog.hideProgressBar();
                Snackbar.make(fbLoginButton, R.string.volley_error_message,Snackbar.LENGTH_LONG)
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

                    Snackbar.make(fbLoginButton, responseMessage, Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();


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

                    saveLoginDetails();
                }else {
                    Snackbar.make(fbLoginButton, responseMessage, Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
            } catch (JSONException e) {
                Log.e(TAG,""+e.getMessage());
            }
        }
    }



    private void saveLoginDetails()
    {
        Log.e("USER DETAILS",""+userId+"\n"+userToken+"\n"+userFirstName+"\n"+userLastName+"\n"+userEmail);
        PrefrencesClass.savePreference(context, Common.USERID, userId);
        PrefrencesClass.savePreference(context, Common.TOKEN, userToken);
        PrefrencesClass.savePreference(context, Common.USER_FIRST_NAME, userFirstName);
        PrefrencesClass.savePreference(context, Common.USER_LAST_NAME, userLastName);
        PrefrencesClass.savePreference(context, Common.USER_EMAIL, userEmail);

        Intent intent = new Intent(getContext(), SelectLocationActivity.class);
        startActivity(intent);
        getActivity().finish();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("RESULT",""+requestCode+"\n"+resultCode);
        fbCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
