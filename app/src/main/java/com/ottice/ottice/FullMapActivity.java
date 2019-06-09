package com.ottice.ottice;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ottice.ottice.models.ProvidersBeanClass;
import com.ottice.ottice.services.ServiceProcessor;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.Utilities;

import java.util.ArrayList;

/**
 * TODO: Add a class header comment!
 */

public class FullMapActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener {

    private static final String TAG = FullMapActivity.class.getSimpleName();
    private ArrayList<ProvidersBeanClass> providerList = null;
    private GoogleMap mMap;
    private Context context;
    private ImageView dismissButton;
    private ImageView infoImage;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_fullview_layout);

        Utilities.setSystemBarsTransparentFully(FullMapActivity.this);

        Bundle args = getIntent().getExtras();
        providerList = args.getParcelableArrayList("ProviderList");
        if (providerList != null) {
            Log.e("mapItemList",""+providerList.size());
            for(int i=0;i<providerList.size();i++){
                Log.e("LAT LNG",""+providerList.get(i).getId());
            }
        }

        getMapView();

        init();

    }


    private void init() {
        context = this;
        dismissButton = (ImageView) findViewById(R.id.dismissButton);
        dismissButton.setOnClickListener(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT){
            Utilities.setMargins(dismissButton, 0,
                    getResources().getDimensionPixelOffset(R.dimen.dp_10),
                    getResources().getDimensionPixelOffset(R.dimen.dp_10),
                    0);
        }else {
            Utilities.setMargins(dismissButton, 0,
                    getResources().getDimensionPixelOffset(R.dimen.dp_30),
                    getResources().getDimensionPixelOffset(R.dimen.dp_10),
                    0);
        }
    }



    private void getMapView() {
        // initializing google map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if(isGoogleMapsInstalled()){
            if(mMap != null){
                try {
                    // Customise the styling of the base map using a JSON object defined
                    // in a raw resource file.
                    boolean success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_json));

                    if (!success) {
                        Log.e(TAG, "Style parsing failed.");
                    }
                } catch (Resources.NotFoundException e) {
                    Log.e(TAG, "Can't find style. Error: ", e);
                }
                mMap.getUiSettings().setZoomControlsEnabled(true);

                if(providerList != null){
                    markProviders(providerList);
                    mMap.setInfoWindowAdapter(new CustomInfoWindow());
                }

                mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        if(Utilities.isConnectedToInternet(context)) {
                            Log.e("info id", "" + marker.getId());
                            ProvidersBeanClass pr = (ProvidersBeanClass) marker.getTag();

                            ActivityOptions options = null;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                options = ActivityOptions.
                                        makeSceneTransitionAnimation((Activity) context, infoImage, context.getString(R.string.space_image_transition));
                            }
                            Intent providerIntent = new Intent(context, SpaceDescriptionActivity.class);
                            providerIntent.putExtra("spaceId", pr.getId());
                            providerIntent.putExtra("spacePlan",pr.getSpacePlan());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                if (options != null) {
                                    startActivity(providerIntent, options.toBundle());
                                }else{
                                    startActivity(providerIntent);
                                }
                            }else {
                                startActivity(providerIntent);
                            }
                        }else {
                            Snackbar.make(dismissButton,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                                    .setAction("Action",null).show();
                        }
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        return true;
                    }
                });
            }
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("installGoogleMaps");
            builder.setCancelable(false);
            builder.setPositiveButton("install", getGoogleMapsListener());
            AlertDialog dialog = builder.create();
            dialog.show();
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.dismissButton:
                onBackPressed();
                break;
        }
    }



    public boolean isGoogleMapsInstalled() {
        try {
            getPackageManager().getApplicationInfo(
                    "com.google.android.apps.maps", 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }




    // method to add pointers of providers on map
    private void markProviders(ArrayList<ProvidersBeanClass> result)
    {
        int noOfProviders = result.size();
        Log.e("SIZE",""+noOfProviders);

        if(noOfProviders > 0) {

            //zooming to first provider of list
            ProvidersBeanClass firstProvider = result.get(0);
            LatLng latLngToZoom = new LatLng(firstProvider.getLatitude(), firstProvider.getLongitude());
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLngToZoom));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12.5f));

            for (int i = 0; i < noOfProviders; i++){
                ProvidersBeanClass providerLatLng = result.get(i);
                if(providerLatLng != null) {
                    Log.e("LAT LNG",""+String.valueOf(providerLatLng.getLatitude())+"\n"+String.valueOf(providerLatLng.getLongitude()));
                    LatLng providerLocation = new LatLng(providerLatLng.getLatitude(), providerLatLng.getLongitude());
                    if (mMap != null) {
                        mMap.addMarker(new MarkerOptions().position(providerLocation).title(providerLatLng.getAddress()).icon(BitmapDescriptorFactory.fromResource(R.mipmap.dashboard_pin_pink)).snippet(providerLatLng.getCity())).setTag(providerLatLng);
                    }
                }
            }

        }
    }


    private class CustomInfoWindow implements GoogleMap.InfoWindowAdapter{

        CustomInfoWindow(){
        }
        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return prepareInfoView(marker);
        }

        private View prepareInfoView(Marker marker){
            View view;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.marker_info_view_layout, null);
            infoImage = (ImageView) view.findViewById(R.id.infoImage);
            TextView title = (TextView) view.findViewById(R.id.mapTitleText);
            TextView snippet = (TextView) view.findViewById(R.id.mapSnippetText);

            ProvidersBeanClass infoItem = (ProvidersBeanClass) marker.getTag();
            ServiceProcessor.makeImageRequest(infoItem.getImageData(),infoImage);
            title.setText(infoItem.getSpaceName());
            snippet.setText(infoItem.getAddress());

            return view;
        }
    }



    public android.content.DialogInterface.OnClickListener getGoogleMapsListener() {
        return new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=com.google.android.apps.maps"));
                startActivity(intent);
            }

        };

    }
}
