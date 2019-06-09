package com.ottice.ottice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ottice.ottice.adapters.AmenitiesRecyclerAdapter;
import com.ottice.ottice.adapters.GoodForRecyclerAdapter;
import com.ottice.ottice.adapters.SpaceImagesViewPagerAdapter;
import com.ottice.ottice.models.CommonItemsBeanClass;
import com.ottice.ottice.models.ProvidersBeanClass;
import com.ottice.ottice.models.ReviewRatingBeanClass;
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

import me.relex.circleindicator.CircleIndicator;

/**
 * TODO: Add a class header comment!
 */
public class SpaceDescriptionActivity extends AppCompatActivity implements View.OnClickListener {

    private int spaceId;
    private Context context;
    private ViewPager spaceImages;
    private CircleIndicator pageIndicator;
    private Button bookNowButton;
    private TextView bookTour, viewReviews, total_rating, total_reviews;
    private TextView spaceTitle, spaceType, seatsAvailable, spaceAddress, spaceDescription, goodForComingSoonText;
    private String spacePlan, UserId, reviews;
    private int seatsCount;
    private long price;
    private float avg_rate;
    private RecyclerView amenitiesRecycler, goodForRecycler;
    private RatingBar ratingBar;
    private ArrayList<ReviewRatingBeanClass>reviewRatingArrayList;
    private NestedScrollView detailsScroll;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.space_description_screen);

        // make system bar transparent
        Utilities.setSystemBarsTransparentFully(SpaceDescriptionActivity.this);

        init();

        Intent args = getIntent();
        spaceId = args.getIntExtra("spaceId",0);
        spacePlan = args.getStringExtra("spacePlan");

        ratingBar.setRating(avg_rate);
        total_reviews.setText(reviews);

        if(Utilities.isConnectedToInternet(SpaceDescriptionActivity.this)){
            getProviderDetails();
        }else {
            checkInternetConnection();
        }

        bookNowButton.setOnClickListener(this);
        bookTour.setOnClickListener(this);
        viewReviews.setOnClickListener(this);
    }



    // method for initializing your views
    private void init() {
        context = this;
        spaceImages = (ViewPager) findViewById(R.id.spaceImages);
        pageIndicator = (CircleIndicator) findViewById(R.id.pageIndicator);
        bookNowButton = (Button) findViewById(R.id.bookNowButton);
        bookTour = (TextView)findViewById(R.id.book_tour_button);
        spaceTitle = (TextView) findViewById(R.id.spaceTitle);
        spaceType = (TextView) findViewById(R.id.spaceTypeInfo);
        seatsAvailable = (TextView) findViewById(R.id.availableSeatsInfo);
        spaceAddress = (TextView) findViewById(R.id.spaceAddressInfo);
        spaceDescription = (TextView) findViewById(R.id.spaceDescriptionInfo);
        amenitiesRecycler = (RecyclerView) findViewById(R.id.amenities);
        goodForRecycler = (RecyclerView) findViewById(R.id.goodFor);
        goodForComingSoonText = (TextView) findViewById(R.id.goodForComingSoonText);
        viewReviews = (TextView)findViewById(R.id.view_review_text);
        total_rating = (TextView)findViewById(R.id.total_rate);
        ratingBar = (RatingBar) findViewById(R.id.average_rating);
        total_reviews = (TextView)findViewById(R.id.total_reviews);
        detailsScroll = (NestedScrollView) findViewById(R.id.detailsScrollView);

        detailsScroll.setSmoothScrollingEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            spaceImages.setNestedScrollingEnabled(false);
        }

        Utilities.initializeRatingBars(context, ratingBar);
    }


    private void getProviderDetails(){
        bookNowButton.setEnabled(false);
        // getting data from service
        JSONObject jsonValue = getRequestBodyJson();
        if (jsonValue != null) {
            Log.e("JSON VALUE", jsonValue.toString());
            VolleyService(jsonValue, ServiceConstants.PROVIDER_SPACE_INFO_URL);
        }
    }


    // method for creating request body json object for service
    private JSONObject getRequestBodyJson() {
        JSONObject mainObject = new JSONObject();
        JSONObject headerObject = new JSONObject();
        JSONObject dataObject = new JSONObject();

        try {
            if (Common.deviceId != null) {
                headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
            }
            headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);
            if(spaceId != 0) {
                dataObject.put(ServiceConstants.KEY_Id, spaceId);
            }
            if(Utilities.isNotNull(spacePlan)){
                dataObject.put(ServiceConstants.KEY_Space_Plan, spacePlan);
            }

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
                        bookNowButton.setEnabled(true);
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



    /** method for parsing the values returned from the service */
    private void getServiceValue(JSONObject response) {

        int responseCode;
        ArrayList<String> spaceImagesList = null;
        ArrayList<ProvidersBeanClass.Amenities> amenitiesArrayList = null;
        ProvidersBeanClass.Amenities amenitiesItem;
        ArrayList<CommonItemsBeanClass> goodForList;
        CommonItemsBeanClass goodForItem;

        if (response != null) {
            try {
                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                responseCode = serverResponse.getInt(ServiceConstants.KEY_ResponseCode);

                if (responseCode == ServiceConstants.success_code) {
                    JSONObject responseData = response.getJSONObject(ServiceConstants.KEY_data);
                    if (responseData != null) {
                        JSONArray providerSpaceImages = responseData.getJSONArray(ServiceConstants.KEY_ProviderSpaceImages);
                        int listSize = providerSpaceImages.length();
                        if(listSize > 0){
                            spaceImagesList = new ArrayList<>();
                            for(int i=0; i<listSize; i++){
                                JSONObject imageItem = providerSpaceImages.getJSONObject(i);
                                spaceImagesList.add(imageItem.getString(ServiceConstants.KEY_ImageData));
                            }
                        }

                        String title = responseData.getString(ServiceConstants.KEY_SpaceName);
                        String description = responseData.getString(ServiceConstants.KEY_Description);
                        String rate = responseData.getString(ServiceConstants.KEY_AverageRating);
                        if(Float.parseFloat(rate)>0) {
                            total_rating.setText(rate + " " + getString(R.string.rate));
                        }else {
                            total_rating.setText(getString(R.string.no) + " " + getString(R.string.rate));
                        }

                        avg_rate = Float.parseFloat(rate);
                        ratingBar.setRating(avg_rate);

                        reviews=responseData.getString(ServiceConstants.KEY_Total_Reviews);
                        if(Integer.parseInt(reviews)>0) {
                            total_reviews.setText(reviews + " " + getString(R.string.reviews));
                        }else {
                            total_reviews.setText(getString(R.string.no) + " " + getString(R.string.reviews));
                        }
                        String address = responseData.getString(ServiceConstants.KEY_Address);
                        seatsCount = responseData.getInt(ServiceConstants.KEY_TotalSeat);
                        String totalSeats;
                        if(seatsCount > 1){
                            totalSeats = seatsCount +" "+ getString(R.string.seats);
                        }else {
                            totalSeats = seatsCount +" "+ getString(R.string.seat);
                        }

                        if(Utilities.isNotNull(title)){
                            spaceTitle.setText(title);
                        }
                        if(Utilities.isNotNull(description)){
                            spaceDescription.setText(description);
                        }
                        if(Utilities.isNotNull(address)){
                            spaceAddress.setText(address);
                        }
                        if(Utilities.isNotNull(totalSeats)){
                            seatsAvailable.setText(totalSeats);
                        }

                        JSONArray officeTypeArray = responseData.getJSONArray(ServiceConstants.KEY_OfficeType);
                        int noOfTypes = officeTypeArray.length();
                        if(noOfTypes != 0) {
                            String officeTypes = "";
                            for (int i = 0; i < noOfTypes; i++) {
                                JSONObject officeTypeObject = officeTypeArray.getJSONObject(i);
                                officeTypes += officeTypeObject.getString(ServiceConstants.KEY_Name);
                                if (i != noOfTypes - 1) {
                                    officeTypes = officeTypes + ", ";
                                }
                                price = officeTypeObject.getLong(ServiceConstants.KEY_Price);
                            }
                            if (Utilities.isNotNull(officeTypes)) {
                                spaceType.setText(officeTypes);
                            }
                        }


                        JSONArray amenitiesArray = responseData.getJSONArray(ServiceConstants.KEY_Amenities);
                        int arraySize = amenitiesArray.length();
                        if (arraySize != 0) {
                            amenitiesArrayList = new ArrayList<>();
                            for (int k = 0; k < arraySize; k++) {
                                JSONObject amenitiesObject = amenitiesArray.getJSONObject(k);
                                amenitiesItem = new ProvidersBeanClass.Amenities();

                                amenitiesItem.setAmenitiesImageData(amenitiesObject.getString(ServiceConstants.KEY_ImageData));
                                amenitiesItem.setAmenityName(amenitiesObject.getString(ServiceConstants.KEY_Name));

                                amenitiesArrayList.add(amenitiesItem);
                            }

                            setAmenitiesRecycler(amenitiesArrayList);
                        }



                        JSONArray goodForArray = responseData.getJSONArray(ServiceConstants.KEY_GoodFor);
                        int goodForArraySize = goodForArray.length();
                        if(goodForArraySize != 0){
                            goodForList = new ArrayList<>();
                            for(int j = 0; j< goodForArraySize; j++){
                                JSONObject goodForObject = goodForArray.getJSONObject(j);
                                goodForItem = new CommonItemsBeanClass();

                                goodForItem.setId(goodForObject.getInt(ServiceConstants.KEY_id));
                                goodForItem.setName(goodForObject.getString(ServiceConstants.KEY_Name).trim());

                                goodForList.add(goodForItem);
                            }

                            setGoodForRecycler(goodForList);
                        }
                        else {
                            goodForRecycler.setVisibility(View.GONE);
                            goodForComingSoonText.setVisibility(View.VISIBLE);
                        }


                        JSONArray reviewRatingArray=responseData.getJSONArray(ServiceConstants.KEY_Reviewrating);
                        int reviewRatingArraySize=reviewRatingArray.length();
                        if (reviewRatingArraySize!=0) {
                            reviewRatingArrayList=new ArrayList<>();
                            for (int k = 0; k < reviewRatingArraySize; k++) {
                                JSONObject reviewratingObject = reviewRatingArray.getJSONObject(k);

                                ReviewRatingBeanClass reviewRatingBeanClass=new ReviewRatingBeanClass();
                                reviewRatingBeanClass.setReviews(reviewratingObject.getString(ServiceConstants.KEY_ReviewText));
                                reviewRatingBeanClass.setRating(reviewratingObject.getString(ServiceConstants.KEY_Rating));
                                reviewRatingBeanClass.setUsername(reviewratingObject.getString(ServiceConstants.KEY_UserName));

                                reviewRatingArrayList.add(reviewRatingBeanClass);

                            }
                            Log.e("rate_list",""+reviewRatingArrayList.size());
                        }
                        else {
                            Log.e("no rating","no rating reviews found");
                        }
                    }
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }

            if(spaceImagesList != null){
                setSpaceImagesViewPager(spaceImagesList);
            }
        }

    }



    private void setAmenitiesRecycler(ArrayList<ProvidersBeanClass.Amenities> amenitiesArrayList) {
        amenitiesRecycler.setHasFixedSize(true);
        amenitiesRecycler.setNestedScrollingEnabled(false);
        amenitiesRecycler.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        amenitiesRecycler.addItemDecoration(new RecyclerViewItemMargin(0,30,0,30));
        AmenitiesRecyclerAdapter amenitiesAdapter = new AmenitiesRecyclerAdapter(context, amenitiesArrayList, Common.INFO_SCREEN_ID);
        amenitiesRecycler.setAdapter(amenitiesAdapter);
    }


    private void setSpaceImagesViewPager(ArrayList<String> spaceImagesList) {

        SpaceImagesViewPagerAdapter spaceImagesViewPagerAdapter = new SpaceImagesViewPagerAdapter(context, spaceImagesList);
        spaceImages.setAdapter(spaceImagesViewPagerAdapter);
        pageIndicator.setViewPager(spaceImages);
    }


    private void setGoodForRecycler(ArrayList<CommonItemsBeanClass> goodForList) {
        goodForComingSoonText.setVisibility(View.GONE);
        goodForRecycler.setVisibility(View.VISIBLE);

        goodForRecycler.setHasFixedSize(true);
        goodForRecycler.setNestedScrollingEnabled(false);
        goodForRecycler.setLayoutManager(new GridLayoutManager(context, 2));

        GoodForRecyclerAdapter goodForAdapter = new GoodForRecyclerAdapter(context, goodForList);
        goodForRecycler.setAdapter(goodForAdapter);
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
                getProviderDetails();
            }
        });

        alertDialog.show();
    }


    // on No internet connection dialog popup
    public void checkInternetConnection()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setTitle(context.getResources().getString(R.string.no_internet_connection));
        alertDialog.setMessage(context.getResources().getString(R.string.no_connection_message));
        alertDialog.setButton(DialogInterface.BUTTON_NEUTRAL,context.getResources().getString(R.string.retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Utilities.isConnectedToInternet(context)) {
                    dialog.dismiss();
                    getProviderDetails();
                } else {
                    dialog.dismiss();
                    checkInternetConnection();
                }
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                finish();
            }
        });
        alertDialog.show();
    }


    @Override
    public void onClick(View view) {
        UserId = PrefrencesClass.getPreferenceValue(context, Common.USERID);

        switch (view.getId())
        {
            case R.id.bookNowButton:
                if(Utilities.isNotNull(UserId)) {
                    navigateToBookingActivity();
                }
                else{
                    Intent intent = new Intent(SpaceDescriptionActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.book_tour_button:

                if(Utilities.isNotNull(UserId)) {
                    navigatetoBookTour();
                }
                else{
                    Intent intent = new Intent(SpaceDescriptionActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.view_review_text:
                navigatetoAllReview();

        }
    }



    private void navigateToBookingActivity() {

        Intent intent = new Intent(SpaceDescriptionActivity.this, BookingActivity.class);
        intent.putExtra(Common.INTENT_KEY_PlanType, spacePlan);
        intent.putExtra(Common.INTENT_KEY_TotalSeats, seatsCount);
        intent.putExtra(Common.INTENT_KEY_Price, price);
        intent.putExtra(Common.INTENT_KEY_SpaceId, spaceId);
        startActivityForResult(intent, Common.BOOKING_REQUEST);
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Common.BOOKING_REQUEST){
            if(resultCode == RESULT_OK && data != null){
                Intent intent = new Intent(SpaceDescriptionActivity.this, PaymentSuccessFailureActivity.class);
                intent.putExtra(Common.INTENT_KEY_paymentResult, data.getStringExtra("payu_response_data"));
                intent.putExtra(Common.INTENT_KEY_SpaceId, spaceId);
                startActivity(intent);
                finish();
            }
        }

    }

    private void navigatetoBookTour() {
        Intent intent=new Intent(SpaceDescriptionActivity.this,Book_tourActivity.class);
        startActivity(intent);
    }

    private void navigatetoAllReview(){
        Intent intent=new Intent(SpaceDescriptionActivity.this,LoadReviewsActivity.class);
        intent.putExtra("list",reviewRatingArrayList);
        startActivity(intent);

    }
}
