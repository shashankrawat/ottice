package com.ottice.ottice;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ottice.ottice.adapters.FilterAmenitiesAdapter;
import com.ottice.ottice.adapters.SpaceTypeRecyclerAdapter;
import com.ottice.ottice.models.CommonItemsBeanClass;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.RangeSeekBar;
import com.ottice.ottice.utils.RangeSeekBar.OnRangeSeekBarChangeListener;
import com.ottice.ottice.utils.RecyclerViewItemMargin;
import com.ottice.ottice.utils.Utilities;
import com.wefika.horizontalpicker.HorizontalPicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class FilterActivity extends AppCompatActivity implements View.OnClickListener, SpaceTypeRecyclerAdapter.RecyclerViewClickListener, FilterAmenitiesAdapter.AmenitiesItemClickListener {

    private Context context;
    private RecyclerView spaceTypeList, amenitiesList;
    private ProgressBar spaceTypeProgressBar, amenitiesProgressBar;
    private TextView retrySpaceType, cancelFilter, resetFilters, retryAmenities;
    private Button applyFilterButton;
    private RangeSeekBar priceRange;
    private String spaceTypeId;
    private ArrayList<String> amenitiesSelected = new ArrayList<>();
    private ArrayList<CommonItemsBeanClass> spaceTypeResult, amenitiesResult;
    private HorizontalPicker selectCapacityRequired;
    private CharSequence [] capacityValues;
    private boolean resetApplied;
    private final int SPACETYPE_ID = 1, AMENITIES_ID = 2;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_screen);

        spaceTypeId = Common.spaceTypeId;
        int amSize = Common.amenitiesList.size();
        if(amSize > 0){
            amenitiesSelected.addAll(Common.amenitiesList);
        }

        init();

        getSpaceType();
        getAmenities();

        setDefaultValue();

        priceRange.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                resetApplied = false;
            }
        });

        selectCapacityRequired.setOnItemSelectedListener(new HorizontalPicker.OnItemSelected() {
            @Override
            public void onItemSelected(int index) {
                resetApplied = false;
            }
        });
    }

    private void init() {
        context = this;
        spaceTypeList = (RecyclerView) findViewById(R.id.spaceTypeRecyclerView);
        spaceTypeProgressBar = (ProgressBar) findViewById(R.id.spaceTypeProgressBar);
        retrySpaceType = (TextView) findViewById(R.id.retrySpaceType);
        cancelFilter = (TextView) findViewById(R.id.filterCancelButton);
        resetFilters = (TextView) findViewById(R.id.filterResetButton);
        applyFilterButton = (Button) findViewById(R.id.applyFilterButton);
        priceRange = (RangeSeekBar) findViewById(R.id.priceRangeSeekBar);
        selectCapacityRequired = (HorizontalPicker) findViewById(R.id.noOfSeatPicker);
        amenitiesList = (RecyclerView) findViewById(R.id.amenitiesRecyclerView);
        retryAmenities = (TextView) findViewById(R.id.retryAmenities);
        amenitiesProgressBar = (ProgressBar) findViewById(R.id.amenitiesProgressBar);
        scrollView = (NestedScrollView) findViewById(R.id.filterScrollView);

        scrollView.setSmoothScrollingEnabled(true);
        amenitiesList.setNestedScrollingEnabled(false);
        spaceTypeList.setNestedScrollingEnabled(false);

        capacityValues = selectCapacityRequired.getValues();

        retrySpaceType.setOnClickListener(this);
        cancelFilter.setOnClickListener(this);
        resetFilters.setOnClickListener(this);
        applyFilterButton.setOnClickListener(this);
        retryAmenities.setOnClickListener(this);

        amenitiesList.setHasFixedSize(false);

        amenitiesList.setLayoutManager(new GridLayoutManager(context, 3));
        amenitiesList.addItemDecoration(new RecyclerViewItemMargin(30, 0, 30, 0));
    }


    private void setDefaultValue(){
        priceRange.setSelectedMinValue(Integer.parseInt(Common.minPrice));
        priceRange.setSelectedMaxValue(Integer.parseInt(Common.maxPrice));

        int index = 0;
         for(int i=0; i<capacityValues.length; i++){
             if(Common.capacity.equals(capacityValues[i])){
                 index = i;
                 break;
             }
         }
        selectCapacityRequired.setSelectedItem(index);
    }



    private void setSpaceTypeRecyclerView() {

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        spaceTypeList.setHasFixedSize(true);

        // use a linear layout manager
        spaceTypeList.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        SpaceTypeRecyclerAdapter spaceTypeRecyclerAdapter = new SpaceTypeRecyclerAdapter(context, spaceTypeResult, this);
        spaceTypeList.setAdapter(spaceTypeRecyclerAdapter);
    }



    private void setAmenitiesRecyclerView(){
        FilterAmenitiesAdapter amenitiesAdapter = new FilterAmenitiesAdapter(context, amenitiesResult, this);
        amenitiesList.setAdapter(amenitiesAdapter);
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
    private void VolleyService(JSONObject jsonObject, String Url, final int Id) {

        Log.e("URL", Url);
        // Tag used to cancel the request
        final String tag_json_obj = "json_obj_req";

        if(Id == SPACETYPE_ID) {
            spaceTypeProgressBar.setVisibility(View.VISIBLE);
            spaceTypeProgressBar.setIndeterminate(true);
        }else if(Id == AMENITIES_ID){
            amenitiesProgressBar.setVisibility(View.VISIBLE);
            amenitiesProgressBar.setIndeterminate(true);
        }

        final JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                Url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("SERVICE RESPONSE", response.toString());
                        if(Id == SPACETYPE_ID) {
                            spaceTypeProgressBar.setIndeterminate(false);
                            spaceTypeProgressBar.setVisibility(View.GONE);

                            spaceTypeResult = getServiceValue(response);
                            if (spaceTypeResult != null) {
                                spaceTypeList.setVisibility(View.VISIBLE);
                                setSpaceTypeRecyclerView();
                            }
                        }else if(Id == AMENITIES_ID){
                            amenitiesProgressBar.setIndeterminate(false);
                            amenitiesProgressBar.setVisibility(View.GONE);

                            amenitiesResult = getAmenitiesServiceValue(response);
                            if(amenitiesResult != null){
                                amenitiesList.setVisibility(View.VISIBLE);
                                setAmenitiesRecyclerView();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("VOLLEY ERROR", "" + error.getMessage());
                // hide the progress_bar dialog
                if(Id == SPACETYPE_ID) {
                    spaceTypeProgressBar.setIndeterminate(false);
                    spaceTypeProgressBar.setVisibility(View.GONE);
                    spaceTypeList.setVisibility(View.GONE);
                    retrySpaceType.setVisibility(View.VISIBLE);
                }else if(Id == AMENITIES_ID){
                    amenitiesProgressBar.setIndeterminate(false);
                    amenitiesProgressBar.setVisibility(View.GONE);
                    amenitiesList.setVisibility(View.GONE);
                    retryAmenities.setVisibility(View.VISIBLE);
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


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.retrySpaceType:
                retrySpaceType.setVisibility(View.GONE);
                getSpaceType();
                break;

            case R.id.filterCancelButton:
                finish();
                break;

            case R.id.filterResetButton:
                priceRange.setSelectedMinValue(0);
                priceRange.setSelectedMaxValue(20000);
                selectCapacityRequired.setSelectedItem(0);
                spaceTypeId = "1";
                getSpaceType();

                amenitiesSelected.clear();
                getAmenities();

                resetApplied = true;
                break;
            case R.id.applyFilterButton:

                Common.minPrice = String.valueOf(priceRange.getSelectedMinValue());
                Common.maxPrice = String.valueOf(priceRange.getSelectedMaxValue());
                Common.spaceTypeId = spaceTypeId;
                Common.capacity = String.valueOf(capacityValues[selectCapacityRequired.getSelectedItem()]);
                Common.amenitiesList = amenitiesSelected;
                Common.resetApplied = resetApplied;

                setResult(RESULT_OK);
                onBackPressed();
                break;

            case R.id.retryAmenities:
                retryAmenities.setVisibility(View.GONE);
                getAmenities();
                break;
        }
    }


    private void getSpaceType(){
        // getting data from service
        JSONObject jsonValue = getRequestBodyJson();
        if (jsonValue != null) {
            VolleyService(jsonValue, ServiceConstants.SPACE_TYPE_URL, SPACETYPE_ID);
        }
    }


    private void getAmenities(){
        // getting data from service
        JSONObject jsonValue = getRequestBodyJson();
        if (jsonValue != null) {
            VolleyService(jsonValue, ServiceConstants.GET_AMENITIES_URL, AMENITIES_ID);
        }
    }


    /** method for parsing the values returned from the service */
    private ArrayList<CommonItemsBeanClass> getServiceValue(JSONObject response) {

        int responseCode;
        ArrayList<CommonItemsBeanClass> resultList = null;
        CommonItemsBeanClass spaceTypeItem;

        if (response != null) {
            try {
                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                responseCode = serverResponse.getInt(ServiceConstants.KEY_ResponseCode);

                if (responseCode == ServiceConstants.success_code) {
                    JSONArray responseData = response.getJSONArray(ServiceConstants.KEY_data);
                    int listSize = responseData.length();
                    if (listSize != 0) {
                        JSONObject resultItemObject;
                        resultList = new ArrayList<>();
                        for (int i = 0; i < listSize; i++) {
                            resultItemObject = responseData.getJSONObject(i);
                            spaceTypeItem = new CommonItemsBeanClass();

                            int typeId = resultItemObject.getInt(ServiceConstants.KEY_Id);
                            spaceTypeItem.setId(typeId);
                            spaceTypeItem.setName(resultItemObject.getString(ServiceConstants.KEY_Name));
                            if(Utilities.isNotNull(resultItemObject.getString(ServiceConstants.KEY_ImageData))){
                                spaceTypeItem.setImageUri(resultItemObject.getString(ServiceConstants.KEY_ImageData));
                            }
                            if(Utilities.isNotNull(resultItemObject.getString(ServiceConstants.KEY_Space_Plan))){
                                spaceTypeItem.setPlanType(resultItemObject.getString(ServiceConstants.KEY_Space_Plan));
                            }

                            if(spaceTypeId.equals(String.valueOf(typeId))){
                                spaceTypeItem.setSelected(true);
                            }

                            resultList.add(spaceTypeItem);
                        }
                        return resultList;
                    }else {
                        return null;
                    }
                }
            } catch (JSONException e) {
                Log.e("VOLLEY ERROR",""+e.getMessage());
                return null;
            }
            return null;

        } else {
            return null;
        }
    }

    @Override
    public void onRecyclerItemClick(Object itemObject) {
        CommonItemsBeanClass spaceTypeItem = (CommonItemsBeanClass) itemObject;
        spaceTypeId = String.valueOf(spaceTypeItem.getId());
        resetApplied = false;
    }


    /** method for parsing the values returned from the service */
    private ArrayList<CommonItemsBeanClass> getAmenitiesServiceValue(JSONObject response) {

        Log.e("Amenities Response",""+response);
        int responseCode;
        ArrayList<CommonItemsBeanClass> resultList = null;
        CommonItemsBeanClass amenitiesItem;

        if (response != null) {
            try {
                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                responseCode = serverResponse.getInt(ServiceConstants.KEY_ResponseCode);

                if (responseCode == ServiceConstants.amenities_success_code) {
                    JSONArray responseData = response.getJSONArray(ServiceConstants.KEY_data);
                    int listSize = responseData.length();
                    if (listSize != 0) {
                        JSONObject resultItemObject;
                        resultList = new ArrayList<>();
                        for (int i = 0; i < listSize; i++) {
                            resultItemObject = responseData.getJSONObject(i);
                            amenitiesItem = new CommonItemsBeanClass();

                            int typeId = resultItemObject.getInt(ServiceConstants.KEY_Id);
                            amenitiesItem.setId(typeId);
                            amenitiesItem.setName(resultItemObject.getString(ServiceConstants.KEY_Name));
                            if(Utilities.isNotNull(resultItemObject.getString(ServiceConstants.KEY_ImageData))){
                                amenitiesItem.setImageUri(resultItemObject.getString(ServiceConstants.KEY_ImageData));
                            }
                            if(!amenitiesSelected.isEmpty()){
                                if(amenitiesSelected.contains(""+typeId)){
                                    amenitiesItem.setSelected(true);
                                }
                            }

                            resultList.add(amenitiesItem);
                        }
                        return resultList;
                    }else {
                        return null;
                    }
                }
            } catch (JSONException e) {
                Log.e("VOLLEY ERROR",""+e.getMessage());
                return null;
            }
            return null;

        } else {
            return null;
        }
    }

    @Override
    public void onAmenityClick(Object itemObject) {
        CommonItemsBeanClass amenitiesItem = (CommonItemsBeanClass) itemObject;
        if(!amenitiesItem.isSelected()){
            amenitiesSelected.add(""+amenitiesItem.getId());
        }else {
            if(amenitiesSelected.contains(""+amenitiesItem.getId())){
                amenitiesSelected.remove(""+amenitiesItem.getId());
            }
        }

        resetApplied = false;
    }
}
