package com.ottice.ottice;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.services.ServiceProcessor;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.PrefrencesClass;
import com.ottice.ottice.utils.Utilities;

import org.json.JSONException;
import org.json.JSONObject;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.ottice.ottice.utils.Common.CAMERA_EXTERNAL_STORAGE_PERMISSION;

/**
 * TODO: Add a class header comment!
 */

public class UpdateProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private Context context;
    private EditText firstName, middleName, lastName, emailET;
    private LinearLayout successLayout;
    private RelativeLayout updatingItemsLayout;
    private Button updateButton, doneButton;
    private boolean isUpdatedSuccessFully = false;
    private String fName, lName, mName, email, profileImageString, imageUri;
    private CircleImageView userProfileImage;
    private Bitmap image;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_profile_screen);

        Intent passedIntent = getIntent();
        if(passedIntent != null) {
            fName = passedIntent.getStringExtra("firstName");
            lName = passedIntent.getStringExtra("lastName");
            mName = passedIntent.getStringExtra("middleName");
            email = passedIntent.getStringExtra("email");
            imageUri = passedIntent.getStringExtra("user_image");
        }
        init();

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    private void init() {
        context = this;
        firstName = (EditText) findViewById(R.id.newFirstName);
        middleName = (EditText) findViewById(R.id.newMiddleName);
        lastName = (EditText) findViewById(R.id.newLastName);
        emailET = (EditText) findViewById(R.id.newEmail);
        userProfileImage = (CircleImageView) findViewById(R.id.userProfileImage);

        updatingItemsLayout = (RelativeLayout) findViewById(R.id.updatingItemsLayout);
        successLayout = (LinearLayout) findViewById(R.id.profileUpdateSuccessLayout);
        updateButton = (Button) findViewById(R.id.updateButton);
        doneButton = (Button) findViewById(R.id.updateProfileDoneButton);


        updateButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        userProfileImage.setOnClickListener(this);

        setProfileValues();

    }

    private void setProfileValues() {
        if( fName != null && !fName.equalsIgnoreCase("null")){
            firstName.setText(fName);
        }
        if(lName != null && !lName.equalsIgnoreCase("null")){
            lastName.setText(lName);
        }
        if(mName != null && !mName.equalsIgnoreCase("null")){
            middleName.setText(mName);
        }
        if(email != null && !email.equalsIgnoreCase("null")){
            emailET.setText(email);
        }
        if(imageUri != null && !imageUri.equalsIgnoreCase("null")){
            ServiceProcessor.makeImageRequest(imageUri, userProfileImage);
        }
    }



    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.updateButton:
                // checking internet connection
                if(Utilities.isConnectedToInternet(context)) {
                    updateProfile();
                    Utilities.hideKeyboard(UpdateProfileActivity.this, updateButton);
                }else {
                    Snackbar.make(view,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                break;

            case R.id.updateProfileDoneButton:
                setResult(RESULT_OK);
                finish();
                break;


            case R.id.userProfileImage:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(UpdateProfileActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , CAMERA_EXTERNAL_STORAGE_PERMISSION);
                        } else {
                            ActivityCompat.requestPermissions(UpdateProfileActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , CAMERA_EXTERNAL_STORAGE_PERMISSION);
                        }
                    } else {
                        ImagePickerOptionDialog picker  = new ImagePickerOptionDialog();
                        picker.show(getSupportFragmentManager(), picker.getTag());
                    }
                } else {
                    ImagePickerOptionDialog picker  = new ImagePickerOptionDialog();
                    picker.show(getSupportFragmentManager(), picker.getTag());
                }
                break;
        }
    }

    private void updateProfile() {
        // getting data from service
        JSONObject jsonValue = getRequestBodyJson();
        if (jsonValue != null) {
            Log.e("JSON VALUE", jsonValue.toString());
            VolleyService(jsonValue, ServiceConstants.UPDATE_USER_DETAILS_URL);
        }
    }


    // method for creating request body json object for service
    private JSONObject getRequestBodyJson() {
        fName = firstName.getText().toString();
        lName = lastName.getText().toString();
        mName = middleName.getText().toString();
        email = emailET.getText().toString();


        if(Utilities.isNotNull(fName) && Utilities.isNotNull(email))
        {
            if(Utilities.validate(email)) {
                JSONObject mainObject = new JSONObject();
                JSONObject headerObject = new JSONObject();
                JSONObject dataObject = new JSONObject();

                try {
                    if (Common.deviceId != null) {
                        headerObject.put(ServiceConstants.KEY_DeviceId, Common.deviceId);
                    }
                    headerObject.put(ServiceConstants.KEY_Platform, ServiceConstants.Platform_Value);
                    headerObject.put(ServiceConstants.KEY_Token, PrefrencesClass.getPreferenceValue(context, Common.TOKEN));

                    dataObject.put(ServiceConstants.KEY_FirstName, fName);

                    if (Utilities.isNotNull(mName)) {
                        dataObject.put(ServiceConstants.KEY_MiddleName, mName);
                    }
                    if(Utilities.isNotNull(lName)){
                        dataObject.put(ServiceConstants.KEY_LastName, lName);
                    }else {
                        dataObject.put(ServiceConstants.KEY_LastName, "");
                    }

                    if(profileImageString != null) {
                        dataObject.put(ServiceConstants.KEY_ImageString, profileImageString);
                    }
                    dataObject.put(ServiceConstants.KEY_Email, email);

                    mainObject.put(ServiceConstants.KEY_header, headerObject);
                    mainObject.put(ServiceConstants.KEY_data, dataObject);

                    return mainObject;
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }else {
                Snackbar.make(updateButton,"Email address is not valid.",Snackbar.LENGTH_LONG).show();
                return null;
            }
        }else {
            Snackbar.make(updateButton,"Please fill the mandatory fields.",Snackbar.LENGTH_LONG).show();
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
                Snackbar.make(updateButton,R.string.volley_error_message,Snackbar.LENGTH_LONG)
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


    private void getServiceValue(JSONObject response) {
        int responseCode;

        if(response != null){
            try {
                JSONObject serverResponse = response.getJSONObject(ServiceConstants.KEY_response);
                responseCode = serverResponse.getInt(ServiceConstants.KEY_ResponseCode);

                if (responseCode == ServiceConstants.success_code) {
                    updatingItemsLayout.setVisibility(View.GONE);
                    successLayout.setVisibility(View.VISIBLE);
                    isUpdatedSuccessFully = true;
                }else {
                    Snackbar.make(updateButton,serverResponse.getString(ServiceConstants.KEY_ResponseMessage),Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if(isUpdatedSuccessFully){
            setResult(RESULT_OK);
            finish();
        }else {
            super.onBackPressed();
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case CAMERA_EXTERNAL_STORAGE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("PERMISSION",""+grantResults[0]);
                    ImagePickerOptionDialog picker  = new ImagePickerOptionDialog();
                    picker.show(getSupportFragmentManager(), picker.getTag());
                }
                break;
        }
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Common.GALLERY_REQUEST){
            if(resultCode == RESULT_OK && data != null){
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = context.getContentResolver().query(selectedImageUri, projection, null, null, null);

                if (cursor != null) {
                    cursor.moveToFirst();
                    int column_index = cursor.getColumnIndex(projection[0]);

                    String selectedImagePath = cursor.getString(column_index);
                    Log.e("Image Path",""+selectedImagePath);
                    cursor.close();

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    final int REQUIRED_SIZE = 150;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;

                    image = BitmapFactory.decodeFile(selectedImagePath, options);

                    if(image != null){
                        userProfileImage.setImageBitmap(image);
                    }

                    profileImageString = Utilities.getStringImage(image);


                }
            }
        }

        if(requestCode == Common.CAMERA_REQUEST){
            if(resultCode == RESULT_OK && data != null){
                try {
                    image = (Bitmap) data.getExtras().get("data");

                    if(image != null){
                        userProfileImage.setImageBitmap(image);
                    }

                    profileImageString = Utilities.getStringImage(image);

                }catch (Exception e){
                    Log.e("camera Exception",""+e.getMessage());
                }

            }
        }
    }





    public static class ImagePickerOptionDialog extends BottomSheetDialogFragment implements View.OnClickListener {

        private Dialog mDialog;

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void setupDialog(Dialog dialog, int style) {
            super.setupDialog(dialog, style);
            mDialog = dialog;

            View contentView = View.inflate(getContext(), R.layout.image_picker_option_dialog, null);
            mDialog.setContentView(contentView);

            Button galleryButton = (Button) contentView.findViewById(R.id.galleryOptionButton);
            Button cameraButton = (Button) contentView.findViewById(R.id.cameraOptionButton);

            galleryButton.setOnClickListener(this);
            cameraButton.setOnClickListener(this);
        }




        @Override
        public void onClick(View view) {
            switch (view.getId())
            {
                case R.id.galleryOptionButton:
                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    galleryIntent.setType("image/*");
                    getActivity().startActivityForResult(galleryIntent, Common.GALLERY_REQUEST);
                    mDialog.dismiss();
                    break;
                case R.id.cameraOptionButton:
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    getActivity().startActivityForResult(cameraIntent, Common.CAMERA_REQUEST);
                    mDialog.dismiss();
                    break;
            }
        }
    }
}
