package com.ottice.ottice;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
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
import com.ottice.ottice.adapters.SearchRecycleViewAdapter;
import com.ottice.ottice.models.CommonItemsBeanClass;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.RecyclerViewItemMargin;
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
public class SearchActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private TextView cancelSearch, nearByTV, listHeading, noCityFoundTV, retryButton;
    private EditText searchET;
    private ImageButton clearAllButton;
    private ArrayList<CommonItemsBeanClass> cityArrayList, searchList;
    private RecyclerView cityRecyclerView;
    private SearchRecycleViewAdapter adapter;
    private ProgressBar cityProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);


        // Create an instance of GoogleAPIClient
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        init();
        getProviderCities();

        nearByTV.setOnClickListener(this);
        retryButton.setOnClickListener(this);
    }


    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }



    private void init() {
        context = this;
        cancelSearch = (TextView) findViewById(R.id.cancelSearch);
        searchET = (EditText) findViewById(R.id.searchBarET);
        clearAllButton = (ImageButton) findViewById(R.id.clearAllButton);
        nearByTV = (TextView) findViewById(R.id.nearByText);
        listHeading = (TextView) findViewById(R.id.cityHeading);
        cityRecyclerView = (RecyclerView) findViewById(R.id.citiesRecyclerView);
        noCityFoundTV = (TextView) findViewById(R.id.noOtticeCityFound);
        cityProgressBar = (ProgressBar) findViewById(R.id.cityProgressBar);
        retryButton = (TextView) findViewById(R.id.cityRetry);

        searchingMethod();
    }


    private void getProviderCities() {

        if(Utilities.isConnectedToInternet(SearchActivity.this)) {
            JSONObject jsonValue = getRequestBodyJson();
            if(jsonValue != null){
                Log.e("JSONE ",""+jsonValue);
                VolleyService(jsonValue, ServiceConstants.GET_CITY_URL);
            }
        }else {
            Snackbar.make(cityRecyclerView,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                    .setAction("Action",null).show();
            retryButton.setVisibility(View.VISIBLE);
        }
    }


    // mehod for setting up recyclerview layout
    private void setupRecyclerView() {
        cityRecyclerView.setHasFixedSize(false);
        cityRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        cityRecyclerView.addItemDecoration(new RecyclerViewItemMargin(0, 0, 0, 30));

        adapter = new SearchRecycleViewAdapter(SearchActivity.this, cityArrayList);
        cityRecyclerView.setAdapter(adapter);
    }


    private void searchingMethod() {
        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(searchList != null) {
                    String searchText = searchET.getText().toString();
                    int textLength = searchText.length();

                    if (textLength >= 1) {
                        nearByTV.setVisibility(View.GONE);
                        listHeading.setVisibility(View.GONE);
                        clearAllButton.setVisibility(View.VISIBLE);
                        // clear the initial data set
                        cityArrayList.clear();
                        int arraySize = searchList.size();

                        for (int j = 0; j < arraySize; j++) {
                            String cityName = searchList.get(j).getName();
                            if (cityName.toLowerCase().contains(searchText.toLowerCase())) {
                                cityArrayList.add(searchList.get(j));
                            }
                        }
                        if (cityArrayList.size() == 0) {
                            noCityFoundTV.setVisibility(View.VISIBLE);
                            adapter.notifyDataSetChanged();
                        } else {
                            noCityFoundTV.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        cityArrayList.clear();
                        noCityFoundTV.setVisibility(View.GONE);
                        clearAllButton.setVisibility(View.GONE);
                        nearByTV.setVisibility(View.VISIBLE);
                        listHeading.setVisibility(View.VISIBLE);
                        cityArrayList.addAll(searchList);
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
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    Utilities.hideKeyboard(SearchActivity.this, textView);
                }
                return false;
            }
        });


        cancelSearch.setOnClickListener(this);
        clearAllButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.nearByText:
                if(Utilities.isConnectedToInternet(SearchActivity.this)) {
                    if(Utilities.isLocationEnabled(context)) {
                        getCurrentLocation();
                    }else{
                        DialogBoxs dialogBox = new DialogBoxs(context);
                        dialogBox.networkEnablingDialogBox();
                    }

                }else {
                    Snackbar.make(view,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                break;
            case R.id.cancelSearch:
                Utilities.hideKeyboard(context,view);
                onBackPressed();
                break;
            case R.id.clearAllButton:
                searchET.setText("");
                noCityFoundTV.setVisibility(View.GONE);
                cityArrayList.clear();
                cityArrayList.addAll(searchList);
                adapter.notifyDataSetChanged();
                break;

            case R.id.cityRetry:
                retryButton.setVisibility(View.GONE);
                getProviderCities();
                break;
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

        cityProgressBar.setVisibility(View.VISIBLE);
        cityProgressBar.setIndeterminate(true);

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        cityProgressBar.setIndeterminate(false);
                        cityProgressBar.setVisibility(View.GONE);

                        cityArrayList = getServiceValue(response);
                        if(cityArrayList != null) {
                            searchList = new ArrayList<>(cityArrayList);
                            cityRecyclerView.setVisibility(View.VISIBLE);
                            setupRecyclerView();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", "" + error.getMessage());
                cityProgressBar.setIndeterminate(false);
                cityProgressBar.setVisibility(View.GONE);
                cityRecyclerView.setVisibility(View.GONE);
                retryButton.setVisibility(View.VISIBLE);

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



    // method for getting current location of user and showing it on map
    private void getCurrentLocation() {
        if(mGoogleApiClient != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                        Toast.makeText(SearchActivity.this, "To show your nearby providers we need to access your location", Toast.LENGTH_LONG).show();
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
            Intent intent = new Intent();
            intent.putExtra(Common.INTENT_KEY_Latitude, lat);
            intent.putExtra(Common.INTENT_KEY_Longitude, lng);
            setResult(RESULT_OK, intent);
            Utilities.hideKeyboard(SearchActivity.this, searchET);
            onBackPressed();
        }else {
            Snackbar.make(searchET,"Something went wrong! No location found. Please try again",Snackbar.LENGTH_LONG)
                    .setAction("Action",null).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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
}
