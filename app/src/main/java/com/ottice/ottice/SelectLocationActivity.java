package com.ottice.ottice;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.ottice.ottice.adapters.SelectCityListAdapter;
import com.ottice.ottice.models.CommonItemsBeanClass;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.AnimationClass;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.SearchLayout;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.ottice.ottice.utils.Common.CURRENT_LOCATION_ACCESS_REQUEST;

/**
 * TODO: Add a class header comment!
 */
public class SelectLocationActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private RecyclerView citiesList;
    private LinearLayout nearByLocation;
    private ArrayList<CommonItemsBeanClass> cityArrayList, searchCityList;
    private TextView cancelSearch, noResultTV;
    private EditText searchET;
    private RelativeLayout searchETLayout;
    private ImageButton clearAllButton;
    private SelectCityListAdapter adapter;
    private boolean isSearchOpened = false;
    private SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_location_activity);

        // Create an instance of GoogleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        init();

        gettingCities();

        nearByLocation.setOnClickListener(this);
        searchET.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.e("RESUME","resume");
        SearchLayout.setSearchActivity(SelectLocationActivity.this);
    }



    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }


    // method for getting cities from service
    private void gettingCities(){
        if(Utilities.isConnectedToInternet(SelectLocationActivity.this)) {
            JSONObject jsonValue = getRequestBodyJson();
            if(jsonValue != null){
                Log.e("JSONE ",""+jsonValue);
                VolleyService(jsonValue, ServiceConstants.GET_CITY_URL);
            }
        }else {
            Intent intent = new Intent(SelectLocationActivity.this, NoInternetActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void init() {
        context = this;
        citiesList = (RecyclerView) findViewById(R.id.citiesList);
        nearByLocation = (LinearLayout) findViewById(R.id.userLocationButtonLayout);
        cancelSearch = (TextView) findViewById(R.id.cancelSearch);
        searchET = (EditText) findViewById(R.id.searchBarET);
        clearAllButton = (ImageButton) findViewById(R.id.clearAllButton);
        noResultTV = (TextView) findViewById(R.id.noItemFoundTV);
        searchETLayout = (RelativeLayout) findViewById(R.id.searchETLayout);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.selectCitySwipeLayout);

        searchingMethod();

        refreshLayout.setOnRefreshListener(onSwipeRefreshDone());
    }




    private void searchingMethod() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(searchCityList != null) {
                    String searchText = searchET.getText().toString();
                    int textLength = searchText.length();

                    if (textLength >= 1) {
                        nearByLocation.setVisibility(View.GONE);
                        clearAllButton.setVisibility(View.VISIBLE);
                        // clear the initial data set
                        cityArrayList.clear();
                        int arraySize = searchCityList.size();

                        for (int j = 0; j < arraySize; j++) {
                            String cityName = searchCityList.get(j).getName();
                            if (cityName.toLowerCase().contains(searchText.toLowerCase())) {
                                cityArrayList.add(searchCityList.get(j));
                            }
                        }
                        if (cityArrayList.size() == 0) {
                            noResultTV.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        } else {
                            noResultTV.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        cityArrayList.clear();
                        noResultTV.setVisibility(View.GONE);
                        clearAllButton.setVisibility(View.GONE);
                        nearByLocation.setVisibility(View.VISIBLE);
                        cityArrayList.addAll(searchCityList);
                        adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        searchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    setSearchBarVisibility(false);
                }
                return false;
            }
        });


        cancelSearch.setOnClickListener(this);
        clearAllButton.setOnClickListener(this);
    }




    private SwipeRefreshLayout.OnRefreshListener onSwipeRefreshDone() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(searchET.getText().toString().equals("")) {
                    refreshLayout.setRefreshing(false);
                    gettingCities();
                }else {
                    refreshLayout.setRefreshing(false);
                    cancelSearch.performClick();
                    gettingCities();
                }
            }
        };
    }





    private void setUpListView() {
        citiesList.setHasFixedSize(true);
        citiesList.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        citiesList.setLayoutManager(layoutManager);
        adapter = new SelectCityListAdapter(context, cityArrayList);
        citiesList.setAdapter(adapter);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.userLocationButtonLayout:
                Log.e("Button Click","Button Click");
                if(Utilities.isConnectedToInternet(SelectLocationActivity.this)) {
                    if(Utilities.isLocationEnabled(context)) {
                        getCurrentLocation();
                    }else{
                        DialogBoxs dialogBox = new DialogBoxs(context);
                        dialogBox.networkEnablingDialogBox();
                    }
                }else {
                    Snackbar.make(v,R.string.no_connection_message,Snackbar.LENGTH_LONG).show();
                }
                break;
            case R.id.searchBarET:
                setSearchBarVisibility(true);
                break;
            case R.id.cancelSearch:
                Utilities.hideKeyboard(context,v);
                setSearchBarVisibility(false);
                searchET.setText("");
                cityArrayList.clear();
                noResultTV.setVisibility(View.GONE);
                nearByLocation.setVisibility(View.VISIBLE);
                cityArrayList.addAll(searchCityList);
                adapter.notifyDataSetChanged();
                break;
            case R.id.clearAllButton:
                searchET.setText("");
                noResultTV.setVisibility(View.GONE);
                break;
        }
    }


    private void setSearchBarVisibility(boolean isVisible){
        if(isVisible){
            isSearchOpened = true;
            searchET.setCursorVisible(true);
            if(searchET.isClickable()) {
                AnimationClass anim = new AnimationClass(context);
                anim.collapse(searchETLayout, searchET, cancelSearch);
            }

        }else {
            isSearchOpened = false;
            searchET.setCursorVisible(false);
            Utilities.hideKeyboard(SelectLocationActivity.this, searchET);
            AnimationClass anim = new AnimationClass(context);
            anim.expand(searchETLayout, searchET, cancelSearch);
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    // Runtime  permission callbacks
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CURRENT_LOCATION_ACCESS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                }
                break;
        }
    }


    // method for getting current location of user and showing it on map
    private void getCurrentLocation() {
        Log.e("GOOGLE API CLIENT",""+mGoogleApiClient);
        if(mGoogleApiClient != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                , CURRENT_LOCATION_ACCESS_REQUEST);
                    } else {
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}
                                , CURRENT_LOCATION_ACCESS_REQUEST);
                    }
                } else {
                    mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                    sendLocation();
                }
            } else {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                sendLocation();
            }

        }

    }



    private void sendLocation(){
        if (mLastLocation != null) {
            double lat = mLastLocation.getLatitude();
            double lng = mLastLocation.getLongitude();
            PrefrencesClass.savePreference(context,Common.CITY_NAME, getString(R.string.around_me));
            Intent intent = new Intent(SelectLocationActivity.this, DashboardActivity.class);
            intent.putExtra(Common.INTENT_KEY_Latitude,lat);
            intent.putExtra(Common.INTENT_KEY_Longitude,lng);
            startActivity(intent);
//                finish();
        }else {
            Snackbar.make(nearByLocation,"Something went wrong! No location found. Please try again",Snackbar.LENGTH_LONG)
                    .setAction("Action",null).show();
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

        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cityArrayList = getServiceValue(response);
                        if(cityArrayList != null) {
                            searchCityList = new ArrayList<>(cityArrayList);
                            setUpListView();
                        }
                        progressDialog.hideProgressBar();

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", "" + error.getMessage());
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


    private ArrayList<CommonItemsBeanClass> getServiceValue(JSONObject response) {
        Log.e("City Response",""+response);
        int responseCode;
        String responseMessage;
        ArrayList<CommonItemsBeanClass> citiesList = null;
        CommonItemsBeanClass cityItem;

        if(response != null){
            try{
                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                responseCode = serverResponse.getInt(ServiceConstants.KEY_ResponseCode);
                responseMessage = serverResponse.getString(ServiceConstants.KEY_ResponseMessage);

                if (responseCode == ServiceConstants.success_code) {
                    JSONObject responseData = response.getJSONObject(ServiceConstants.KEY_data);
                    if (responseData != null) {
                        JSONArray totalCityList = responseData.getJSONArray(ServiceConstants.KEY_ProviderCity);
                        if(totalCityList != null) {
                            int listSize = totalCityList.length();
                            if (listSize != 0) {
                                JSONObject cityObject;
                                citiesList = new ArrayList<>();
                                for (int i = 0; i < listSize; i++) {
                                    cityObject = totalCityList.getJSONObject(i);
                                    cityItem = new CommonItemsBeanClass();

                                    cityItem.setId(cityObject.getInt(ServiceConstants.KEY_id));
                                    cityItem.setName(cityObject.getString(ServiceConstants.KEY_CityName));
                                    cityItem.setTotalSpaces(cityObject.getInt(ServiceConstants.KEY_TotalLocation));

                                    citiesList.add(cityItem);
                                }
                                return citiesList;
                            } else {
                                return null;
                            }
                        }
                    }
                }else if(responseCode == ServiceConstants.error_code){
                    Toast.makeText(context, responseMessage, Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
            return null;
        }else {
            return null;
        }
    }


    @Override
    public void onBackPressed() {

        if(isSearchOpened) {
            setSearchBarVisibility(false);
        }else {
            super.onBackPressed();
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
                gettingCities();
                dialog.dismiss();
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}
