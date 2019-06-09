package com.ottice.ottice.fragments;


import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ottice.ottice.AppController;
import com.ottice.ottice.FilterActivity;
import com.ottice.ottice.FullMapActivity;
import com.ottice.ottice.R;
import com.ottice.ottice.SearchActivity;
import com.ottice.ottice.adapters.DashboardRecyclerAdapter;
import com.ottice.ottice.models.ProvidersBeanClass;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.RecyclerViewItemMargin;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * TODO: Add a class header comment!
 */

public class HomeFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback{

    // Global variables
    private static final String TAG = HomeFragment.class.getSimpleName();
    private Context context;
    private RecyclerView recyclerView;
    private GoogleMap mMap;
    private double user_Lat, user_Lng;
    private int cityId, remainingHours = 12;
    private String planType = "Hourly";
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout mAppBarLayout, dateNDurationLayout;
    private TextView searchBar, placeHeading, noResultText, planTypeHeading, dateSelected, startTimeSelected, durationSelected;
    private ArrayList<ProvidersBeanClass> result = null;
    private FloatingActionButton filterButton, scrollUpButton;
    private RelativeLayout selectDate, selectStartTime, selectDuration;
    private ImageView planTypeImage;
    private String[] startTimeArray;
    private String currentDateStr, currentTimeStr, responseMessage;
    private Activity activity;
    private NestedScrollView nestedScrollView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        user_Lat = args.getDouble(Common.INTENT_KEY_Latitude, 0);
        user_Lng = args.getDouble(Common.INTENT_KEY_Longitude, 0);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;
        if(container == null){
            return null;
        }else {
            view = inflater.inflate(R.layout.home_screen, container, false);
            return view;
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        collapsingToolbarLayout = (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        searchBar = (TextView) view.findViewById(R.id.dashboardSearchBar);
        placeHeading = (TextView) view.findViewById(R.id.placesAtTextView);
        mAppBarLayout = (AppBarLayout) view.findViewById(R.id.appbar);
        noResultText = (TextView) view.findViewById(R.id.noProvidersText);
        filterButton = (FloatingActionButton) view.findViewById(R.id.filterButton);
        dateNDurationLayout = (AppBarLayout) view.findViewById(R.id.dateNDurationLayout);
        planTypeHeading = (TextView) view.findViewById(R.id.planTypeHeading);
        planTypeImage = (ImageView) view.findViewById(R.id.planTypeImage);
        selectDate = (RelativeLayout) view.findViewById(R.id.selectDate);
        selectStartTime = (RelativeLayout) view.findViewById(R.id.selectStartTime);
        selectDuration = (RelativeLayout) view.findViewById(R.id.selectDuration);
        dateSelected = (TextView) view.findViewById(R.id.date);
        startTimeSelected = (TextView) view.findViewById(R.id.startTime);
        durationSelected = (TextView) view.findViewById(R.id.duration);
        scrollUpButton = (FloatingActionButton) view.findViewById(R.id.scrollUpButton);
        nestedScrollView = (NestedScrollView) view.findViewById(R.id.nestedScrollView);
        setupRecyclerView();

        setDefaultStartValues();

        loadData();

        searchBar.setOnClickListener(this);
        filterButton.setOnClickListener(this);
        selectDate.setOnClickListener(this);
        selectStartTime.setOnClickListener(this);
        selectDuration.setOnClickListener(this);
        scrollUpButton.setOnClickListener(this);


        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int topMargin = Utilities.getStatusBarHeight(context);
                    if (verticalOffset <= -480 + topMargin) {
                        dateNDurationLayout.setPadding(0,topMargin,0,0);
                        changeStatusBarColor(true);
                    } else {
                        dateNDurationLayout.setPadding(0,0,0,0);
                        changeStatusBarColor(false);
                    }
                }
            }
        });


        nestedScrollView.setSmoothScrollingEnabled(true);
        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(scrollY > 50){
                    scrollUpButton.setVisibility(View.VISIBLE);
                    filterButton.setVisibility(View.GONE);
                }else {
                    scrollUpButton.setVisibility(View.GONE);
                    filterButton.setVisibility(View.VISIBLE);
                }
            }
        });

    }





    // mehod for setting up recyclerview layout
    private void setupRecyclerView() {
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new RecyclerViewItemMargin(30, 40, 20, 40));
    }



    private void setDefaultStartValues() {
        currentDateStr = Common.showingDateFormat.format(Common.currentDate);
        currentTimeStr = Common.showingTimeFormat.format(Common.currentDate);

        dateSelected.setText(Common.startDateStr);
        startTimeSelected.setText(Common.startTimeStr);

        startTimeArray = getTimeArray(Common.startDateStr);

        Common.startDateTime = Utilities.serviceTypeDateAndTimeFormat(Common.startDateStr+" "+Common.startTimeStr);

        setDurationValue();
    }





    private void loadData() {
        if (Utilities.isConnectedToInternet(context)) {

            // initializing google map
            SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.mapFrame);
            mapFragment.getMapAsync(this);
            getProviders();
        } else {
            checkInternetConnection();
        }

    }




    private void getProviders() {

        cityId = PrefrencesClass.getPreferenceInt(getContext(), Common.CITY_ID);
        String cityName = PrefrencesClass.getPreferenceValue(getContext(), Common.CITY_NAME);
        placeHeading.setText(cityName);


        Log.e(TAG, "Space Type id : "+ Common.spaceTypeId);
        switch (Common.spaceTypeId) {
            case "1":
                planType = "Hourly";
                planTypeImage.setImageResource(R.mipmap.hourly_icon);
                if (Common.duration > 1) {
                    durationSelected.setText(String.valueOf(Common.duration) + " " + getString(R.string.hours));
                } else {
                    durationSelected.setText(String.valueOf(Common.duration) + " " + getString(R.string.hour));
                }
                break;
            case "2":
                planType = "Daily";
                planTypeImage.setImageResource(R.mipmap.daily_icon);
                if (Common.duration > 1) {
                    durationSelected.setText(String.valueOf(Common.duration) + " " + getString(R.string.days));
                } else {
                    durationSelected.setText(String.valueOf(Common.duration) + " " + getString(R.string.day));
                }
                break;
            case "3":
                planType = "Monthly";
                planTypeImage.setImageResource(R.mipmap.daily_icon);
                if (Common.duration > 1) {
                    durationSelected.setText(String.valueOf(Common.duration) + " " + getString(R.string.months));
                } else {
                    durationSelected.setText(String.valueOf(Common.duration) + " " + getString(R.string.month));
                }
                break;
            case "4":
                planType = "Yearly";
                planTypeImage.setImageResource(R.mipmap.daily_icon);
                if (Common.duration > 1) {
                    durationSelected.setText(String.valueOf(Common.duration) + " " + getString(R.string.years));
                } else {
                    durationSelected.setText(String.valueOf(Common.duration) + " " + getString(R.string.year));
                }
                break;
        }


        if (Common.resetApplied) {
            filterButton.setImageResource(R.mipmap.empty_filter);
        } else {
            filterButton.setImageResource(R.mipmap.applied_filter);
        }



        // getting data from service
        JSONObject jsonValue = getRequestBodyJson();
        Log.e(TAG, "REQUEST : " + jsonValue);
        if (jsonValue != null) {
            VolleyService(jsonValue, ServiceConstants.FILTER_SPACES_URL);
        }
    }





    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dashboardSearchBar:
                ActivityOptions options = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    options = ActivityOptions.
                            makeSceneTransitionAnimation(getActivity(), searchBar, getString(R.string.transition_name));
                }
                Intent intent = new Intent(context, SearchActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    if (options != null) {
                        startActivityForResult(intent, Common.SEARCH_CITY_REQUEST, options.toBundle());
                    } else {
                        startActivityForResult(intent, Common.SEARCH_CITY_REQUEST);
                    }
                } else {
                    startActivityForResult(intent, Common.SEARCH_CITY_REQUEST);
                }
                break;

            case R.id.filterButton:
                Intent filterIntent = new Intent(context, FilterActivity.class);
                startActivityForResult(filterIntent, Common.FILTER_REQUEST);
                break;

            case R.id.selectDate:
                pickerDialogBox();
                break;

            case R.id.selectStartTime:
                pickerDialogBox();
                break;

            case R.id.selectDuration:
                pickerDialogBox();
                break;

            case R.id.scrollUpButton:
                nestedScrollView.fullScroll(0);
                mAppBarLayout.setExpanded(true);
                break;
        }
    }





    @Override
    public void onResume() {
        super.onResume();

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            Utilities.setMargins(searchBar,
                    getResources().getDimensionPixelOffset(R.dimen.dp_30),
                    getResources().getDimensionPixelOffset(R.dimen.dp_6),
                    getResources().getDimensionPixelOffset(R.dimen.dp_30),
                    0);
        } else {
            Utilities.setMargins(searchBar,
                    getResources().getDimensionPixelOffset(R.dimen.dp_30),
                    getResources().getDimensionPixelOffset(R.dimen.dp_30),
                    getResources().getDimensionPixelOffset(R.dimen.dp_30),
                    0);
        }
    }




    private String[] getTimeArray(String choosenDate){
        ArrayList<String> defaultTimeArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.start_timings)));
        int defaultSize = defaultTimeArray.size();

        String[] timeArray;

        if(choosenDate.equalsIgnoreCase(currentDateStr)){
            int index = defaultTimeArray.indexOf(currentTimeStr);
            int newArraySize = 0;
            if(index == -1){
                newArraySize = defaultSize;
                index = 0;
            }else if (index >= 0){
                newArraySize = defaultSize - index;
            }

            timeArray = new String [newArraySize];
            for(int i=0; i< newArraySize; i++){
                timeArray[i] = defaultTimeArray.get(index++);
            }
            return timeArray;
        }else {
            timeArray = new String[defaultSize];
            timeArray = defaultTimeArray.toArray(timeArray);
            return timeArray;
        }
    }




    private void setDurationValue(){
        if(planType.equalsIgnoreCase(getString(R.string.hourly))) {
            if(Common.duration > 1) {
                durationSelected.setText(String.valueOf(Common.duration) +" "+getString(R.string.hours));
            }else {
                durationSelected.setText(String.valueOf(Common.duration) +" "+getString(R.string.hour));
            }
        }else if(planType.equalsIgnoreCase(getString(R.string.daily))){
            if(Common.duration > 1) {
                durationSelected.setText(String.valueOf(Common.duration) +" "+getString(R.string.days));
            }else {
                durationSelected.setText(String.valueOf(Common.duration) +" "+getString(R.string.day));
            }
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
                    loadData();
                } else {
                    dialog.dismiss();
                    checkInternetConnection();
                }
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }



    private void changeStatusBarColor(boolean isCollapsed) {
        Window window = getActivity().getWindow();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isCollapsed) {
                // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(ContextCompat.getColor(context, R.color.color_6f6f6f));
                }
            } else {
                window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(Color.TRANSPARENT);
                }
            }
        }
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        if (mMap != null) {
            mMap.setBuildingsEnabled(true);
            mMap.getUiSettings().setAllGesturesEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);

            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getContext(), R.raw.style_json));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
            }

            if (user_Lat != 0 && user_Lng != 0) {
                // Add a marker to the current location of user, and move the camera.
                final LatLng myLocation = new LatLng(user_Lat, user_Lng);
                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(myLocation).icon(BitmapDescriptorFactory.fromResource(R.mipmap.dashboard_pin_green)));
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 12.5f));
                        }
                    }, 1000);
                }
            }

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    callingMapFullView();
                }
            });

            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    callingMapFullView();
                    return true;
                }
            });
        }
    }




    private void pickerDialogBox() {
        final Dialog dialog = new Dialog(context);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.picker_dialog_layout);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.picker_dialogbox_background));

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);

        TextView durationHeading = (TextView) dialog.findViewById(R.id.pickerDurationHeading);
        Button cancelButton = (Button) dialog.findViewById(R.id.pickerCancelButton);
        Button selectButton = (Button) dialog.findViewById(R.id.pickerSelectButton);
        final NumberPicker startDatePicker = (NumberPicker) dialog.findViewById(R.id.startDatePicker);
        final NumberPicker startTimePicker = (NumberPicker) dialog.findViewById(R.id.startTimePicker);
        final NumberPicker durationPicker = (NumberPicker) dialog.findViewById(R.id.durationPicker);


        if(planType.equalsIgnoreCase("Hourly")){
            remainingHours = getRemainingHours(Common.startTimeStr, startTimeArray.length);
            durationHeading.setText(getString(R.string.hours));
        }else if(planType.equalsIgnoreCase("daily")){
            durationHeading.setText(getString(R.string.days));
        }else if(planType.equalsIgnoreCase("monthly")){
            durationHeading.setText(getString(R.string.months));
        }else if(planType.equalsIgnoreCase("yearly")){
            durationHeading.setText(getString(R.string.years));
        }
        setDurationPickerValues(durationPicker);
        durationPicker.setWrapSelectorWheel(false);
        durationPicker.setValue(Common.duration);


        startTimePicker.setMinValue(0);
        startTimePicker.setWrapSelectorWheel(false);
        startTimePicker.setMaxValue(startTimeArray.length - 1);
        startTimePicker.setDisplayedValues(startTimeArray);
        startTimePicker.setValue(Utilities.getIndex(startTimeArray, Common.startTimeStr));


        startDatePicker.setWrapSelectorWheel(false);
        startDatePicker.setMinValue(0);
        startDatePicker.setMaxValue(Common.dateArray.length - 1);
        startDatePicker.setDisplayedValues(Common.dateArray);
        startDatePicker.setValue(Utilities.getIndex(Common.dateArray, Common.startDateStr));

        startDatePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                startTimePicker.setDisplayedValues(null);
                startTimeArray = getTimeArray(Common.dateArray[i1]);
                startTimePicker.setMaxValue(startTimeArray.length - 1);
                startTimePicker.setDisplayedValues(startTimeArray);
                startTimePicker.setValue(Utilities.getIndex(startTimeArray, Common.startTimeStr));

                if(planType.equalsIgnoreCase("Hourly")){
                    remainingHours = getRemainingHours(Common.startTimeStr, startTimeArray.length);
                }
                setDurationPickerValues(durationPicker);
            }
        });

        startTimePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                if(planType.equalsIgnoreCase("Hourly")){
                    remainingHours = getRemainingHours(startTimeArray[i1], startTimeArray.length);
                }

                setDurationPickerValues(durationPicker);
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Common.duration = durationPicker.getValue();

                setDurationValue();

                Common.startDateStr = Common.dateArray[startDatePicker.getValue()];
                Common.startTimeStr = startTimeArray[startTimePicker.getValue()];
                Common.startDateTime = Utilities.serviceTypeDateAndTimeFormat(Common.startDateStr+" "+Common.startTimeStr);
                dateSelected.setText(Common.startDateStr);
                startTimeSelected.setText(Common.startTimeStr);
                loadData();
                dialog.dismiss();
            }
        });

        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }




    private int getRemainingHours(String s, int size) {
        return size - Utilities.getIndex(startTimeArray, s);
    }




    private void callingMapFullView() {
        ActivityOptions options = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            options = ActivityOptions.
                    makeSceneTransitionAnimation(getActivity(), collapsingToolbarLayout, getString(R.string.map_transition_name));
        }
        Intent intent = new Intent(context, FullMapActivity.class);
        intent.putParcelableArrayListExtra("ProviderList", result);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (options != null) {
                startActivity(intent, options.toBundle());
            } else {
                startActivity(intent);
            }
        } else {
            startActivity(intent);
        }
    }



    private void setDurationPickerValues(NumberPicker durationPicker) {
        if(planType.equalsIgnoreCase("Hourly")){
            durationPicker.setMinValue(1);
            durationPicker.setMaxValue(remainingHours);
        }else if(planType.equalsIgnoreCase("Daily")){
            durationPicker.setMinValue(1);
            durationPicker.setMaxValue(Common.remainingDays);
        }else if(planType.equalsIgnoreCase("Monthly")){
            durationPicker.setMinValue(1);
            durationPicker.setMaxValue(11);
        }else if(planType.equalsIgnoreCase("Yearly")){
            durationPicker.setMinValue(1);
            durationPicker.setMaxValue(5);
        }
    }



    // method for creating request body json object for service
    private JSONObject getRequestBodyJson() {
        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();
        JSONObject dataObject = new JSONObject();
        JSONArray priceRangeArray = new JSONArray();
        JSONArray amenitiesArray = new JSONArray();

        try {
            if (Common.deviceId != null) {
                headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
            }
            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);

            priceRangeArray.put(Common.minPrice);
            priceRangeArray.put(Common.maxPrice);

            Log.e("LAT_LNG",""+user_Lat+"\n"+user_Lat);
            if(user_Lat != 0 && user_Lng != 0) {
                dataObject.put(ServiceConstants.KEY_Latitude, "" + user_Lat);
                dataObject.put(ServiceConstants.KEY_Longitude, "" + user_Lng);
            }else{
                dataObject.put(ServiceConstants.KEY_CityId, "" + cityId);
            }
            dataObject.put(ServiceConstants.KEY_PriceRange, priceRangeArray);
            dataObject.put(ServiceConstants.KEY_SpaceTypeId, Common.spaceTypeId);
            dataObject.put(ServiceConstants.KEY_Space_Plan, planType);
            dataObject.put(ServiceConstants.KEY_Duration, Common.duration);
            dataObject.put(ServiceConstants.KEY_StartDateTime, Common.startDateTime);

            int amenitiesListSize = Common.amenitiesList.size();
            if(amenitiesListSize == 0){
                amenitiesArray.put("");
            }else if(amenitiesListSize > 0){
                for(int i=0; i<amenitiesListSize; i++){
                    amenitiesArray.put(Common.amenitiesList.get(i));
                }
            }
            dataObject.put(ServiceConstants.KEY_amenities, amenitiesArray);

            String capacityText = Common.capacity;
            int capacityValue;
            if (capacityText.contains("+")) {
                capacityText = capacityText.replace("+", "");
            }
            capacityValue = Integer.parseInt(capacityText);
            dataObject.put(ServiceConstants.KEY_Capacity, capacityValue);

            mainObject.put(ServiceConstants.KEY_header, headerObject);
            mainObject.put(ServiceConstants.KEY_data, dataObject);

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

        // setting progress_bar bar dialog
        final DialogBoxs progressDialog = new DialogBoxs(context);
        progressDialog.showProgressBarDialog();

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        activity = getActivity();
                        if(activity != null && isAdded()) {
                            Log.e("SERVICE RESPONSE", response.toString());
                            result = getServiceValue(response);
                            Log.e("RESULT", "" + result);
                            if (result != null) {
                                int resultSize = result.size();
                                Log.e("RESULT SIZE", "" + resultSize);
                                if (resultSize != 0) {
                                    String headingStr = null;
                                    if (resultSize > 1) {
                                        headingStr = planType + " (" + result.size() + " " + getResources().getString(R.string.spaces) + ")";
                                    } else if (resultSize == 1) {
                                        headingStr = planType + " (" + result.size() + " " + getResources().getString(R.string.space) + ")";
                                    }
                                    planTypeHeading.setText(headingStr);
                                    noResultText.setVisibility(View.GONE);
                                    recyclerView.setVisibility(View.VISIBLE);
                                    DashboardRecyclerAdapter adapter = new DashboardRecyclerAdapter(getActivity(), result);
                                    recyclerView.setAdapter(adapter);
                                    markProviders(result);
                                }
                                if(!PrefrencesClass.getPreferenceBoolean(context, Common.DASHBOARD_FIRST_TIME_VISITED))
                                {
                                    DialogBoxs showCitySearchHelpBox = new DialogBoxs(context);
                                    showCitySearchHelpBox.showSearchHelp();
                                }

                            } else {
                                if (responseMessage != null && !responseMessage.equalsIgnoreCase("null")) {
                                    noResultText.setText(responseMessage);
                                }else {
                                    noResultText.setText(getString(R.string.err_no_space_found));
                                }
                                noResultText.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                planTypeHeading.setText(planType + " (" + 0 + " " + getResources().getString(R.string.space) + ")");
                            }
                        }
                        progressDialog.hideProgressBar();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", "" + error.getMessage());
                activity = getActivity();
                if(activity != null && isAdded()) {
                    // hide the progress_bar dialog
                    progressDialog.hideProgressBar();

                    retryDialog();
                }
            }
        });

        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(
                3000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);
    }



    /**
     * method for parsing the values returned from the service
     */
    private ArrayList<ProvidersBeanClass> getServiceValue(JSONObject response) {

        int responseCode;
        ArrayList<ProvidersBeanClass> providersList;
        ArrayList<ProvidersBeanClass.Amenities> amenitiesArrayList;
        ProvidersBeanClass providerItem;
        ProvidersBeanClass.Amenities amenitiesItem;

        if (response != null) {
            try {
                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                responseCode = serverResponse.getInt(ServiceConstants.KEY_ResponseCode);

                if (responseCode == ServiceConstants.success_code) {
                    JSONObject responseData = response.getJSONObject(ServiceConstants.KEY_data);
                    if (responseData != null) {
                        JSONArray totalProvidersList = responseData.getJSONArray(ServiceConstants.KEY_ProviderList);
                        int listSize = totalProvidersList.length();
                        Log.e("LIST SIZE",""+listSize);
                        if (listSize != 0) {
                            JSONObject providerObject;
                            providersList = new ArrayList<>();
                            for (int i = 0; i < listSize; i++) {
                                providerObject = totalProvidersList.getJSONObject(i);
                                providerItem = new ProvidersBeanClass();

                                providerItem.setId(providerObject.getInt(ServiceConstants.KEY_Id));
                                providerItem.setProviderId(providerObject.getString(ServiceConstants.KEY_ProviderId));
                                providerItem.setSpaceName(providerObject.getString(ServiceConstants.KEY_SpaceName));
                                providerItem.setState(providerObject.getString(ServiceConstants.KEY_State));
                                providerItem.setCity(providerObject.getString(ServiceConstants.KEY_City));
                                providerItem.setAddress(providerObject.getString(ServiceConstants.KEY_Address));
                                providerItem.setLatitude(providerObject.getDouble(ServiceConstants.KEY_Latitude));
                                providerItem.setLongitude(providerObject.getDouble(ServiceConstants.KEY_Longitude));
                                providerItem.setImageData(providerObject.getString(ServiceConstants.KEY_imagedata));
                                providerItem.setSeats(providerObject.getInt(ServiceConstants.KEY_Seat));
                                providerItem.setRating(providerObject.getString(ServiceConstants.KEY_AverageRating));

                                JSONArray officeTypeArray = providerObject.getJSONArray(ServiceConstants.KEY_OfficeType);
                                int noOfTypes = officeTypeArray.length();
                                if (noOfTypes != 0) {
                                    for (int j = 0; j < noOfTypes; j++) {
                                        JSONObject officeTypeObject = officeTypeArray.getJSONObject(j);

                                        providerItem.setTypeId(officeTypeObject.getInt(ServiceConstants.KEY_Id));
                                        providerItem.setTypeName(officeTypeObject.getString(ServiceConstants.KEY_Name));
                                        providerItem.setTypeImageData(officeTypeObject.getString(ServiceConstants.KEY_ImageData));
                                        providerItem.setSpacePlan(officeTypeObject.getString(ServiceConstants.KEY_Space_Plan));
                                        providerItem.setPrice(officeTypeObject.getLong(ServiceConstants.KEY_Price));
                                    }
                                }


                                JSONArray amenitiesArray = providerObject.getJSONArray(ServiceConstants.KEY_Amenities);
                                int arraySize = amenitiesArray.length();
                                if (arraySize != 0) {
                                    amenitiesArrayList = new ArrayList<>();
                                    for (int k = 0; k < arraySize; k++) {
                                        JSONObject amenitiesObject = amenitiesArray.getJSONObject(k);
                                        amenitiesItem = new ProvidersBeanClass.Amenities();

                                        amenitiesItem.setAmenitiesImageData(amenitiesObject.getString(ServiceConstants.KEY_ImageData));

                                        amenitiesArrayList.add(amenitiesItem);
                                    }

                                    providerItem.setAmenitiesList(amenitiesArrayList);
                                }

                                providersList.add(providerItem);
                            }
                            return providersList;
                        } else {
                            responseMessage = responseData.getString(ServiceConstants.KEY_status);
                            return null;
                        }
                    }
                }
            } catch (JSONException e) {
                Log.e("VOLLEY ERROR", "" + e.getMessage());
                return null;
            }
            return null;

        } else {
            return null;
        }
    }

    // method to add pointers of providers on map
    private void markProviders(ArrayList<ProvidersBeanClass> result) {
        int noOfProviders = result.size();

        if (noOfProviders > 0) {
            ProvidersBeanClass firstProvider = result.get(noOfProviders - 1);

            final LatLng latLngToZoom = new LatLng(firstProvider.getLatitude(), firstProvider.getLongitude());
            Log.e("MAP", "" + mMap);
            if (mMap != null) {
                if (user_Lat == 0 && user_Lng == 0) {
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngToZoom, 12.5f));
                        }
                    }, 1000);
                }
            }

            for (int i = 0; i < noOfProviders; i++) {
                ProvidersBeanClass providerLatLng = result.get(i);
                Log.e("LAT LNG", "" + providerLatLng.getLatitude() + "," + providerLatLng.getLongitude());
                LatLng providerLocation = new LatLng(providerLatLng.getLatitude(), providerLatLng.getLongitude());
                if (mMap != null) {
                    mMap.addMarker(new MarkerOptions().position(providerLocation).icon(BitmapDescriptorFactory.fromResource(R.mipmap.dashboard_pin_pink)));
                }
            }
        }
    }



    // on Volley Error dialog popup
    private void retryDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(context.getResources().getString(R.string.retry_dialog_title));
        alertDialog.setMessage(context.getResources().getString(R.string.retry_dialog_message));
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL, context.getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loadData();
                dialog.dismiss();
            }
        });

        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Common.SEARCH_CITY_REQUEST){
            if (resultCode == Activity.RESULT_OK) {
                if (mMap != null) {
                    mMap.clear();
                }
                if(data != null) {
                    user_Lat = data.getDoubleExtra(Common.INTENT_KEY_Latitude, 0);
                    user_Lng = data.getDoubleExtra(Common.INTENT_KEY_Longitude, 0);
                }

                loadData();
            }
        }

        if (requestCode == Common.FILTER_REQUEST) {

            if(resultCode == Activity.RESULT_OK) {
                if (mMap != null) {
                    mMap.clear();
                }

                loadData();
            }
        }

        if(requestCode == Common.SPACE_BOOK_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                loadData();
            }
        }
    }
}
