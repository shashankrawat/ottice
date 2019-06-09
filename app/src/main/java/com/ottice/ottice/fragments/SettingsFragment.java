package com.ottice.ottice.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ottice.ottice.IntroductionScreenActivity;
import com.ottice.ottice.PrivacyPolicyViewActivity;
import com.ottice.ottice.R;
import com.ottice.ottice.utils.Utilities;

import org.w3c.dom.Text;

/**
 * TODO: Add a class header comment!
 */

public class SettingsFragment extends Fragment implements View.OnClickListener {

    private Context context;
    private AppBarLayout settingsAppbar;
    private Button appWorkingButton, privacyPolicyButton;
    private TextView callSupportButton, emailSupportButton;
    private String supportNumber;

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
            view = inflater.inflate(R.layout.settings_screen, container, false);
            return view;
        }
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        context = getContext();
        settingsAppbar = (AppBarLayout) view.findViewById(R.id.settingsAppBar);
        appWorkingButton = (Button) view.findViewById(R.id.helpButton);
        privacyPolicyButton = (Button) view.findViewById(R.id.privacyPolicyButton);
        callSupportButton = (TextView) view.findViewById(R.id.contact);
        emailSupportButton = (TextView) view.findViewById(R.id.emailContact);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settingsAppbar.setPadding(0, Utilities.getStatusBarHeight(context), 0, 0);
        }

        appWorkingButton.setOnClickListener(this);
        privacyPolicyButton.setOnClickListener(this);
        callSupportButton.setOnClickListener(this);
        emailSupportButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.helpButton:
                Intent intent = new Intent(getActivity(), IntroductionScreenActivity.class);
                startActivity(intent);
                break;

            case R.id.privacyPolicyButton:
                Intent privacyIntent = new Intent(getActivity(), PrivacyPolicyViewActivity.class);
                startActivity(privacyIntent);
                break;

            case R.id.contact:
                supportNumber = callSupportButton.getText().toString();
                int PERMISSION_ALL = 1;
                String[] PERMISSIONS = {Manifest.permission.CALL_PHONE};

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CALL_PHONE)) {
                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                        } else {
                            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, PERMISSION_ALL);
                        }
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + supportNumber));//change the number
                        startActivity(callIntent);
                    }
                }else {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + supportNumber));//change the number
                    startActivity(callIntent);
                }

                break;

            case R.id.emailContact:
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                String[] recipients={"xyz@gmail.com"};
                emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
                emailIntent.putExtra(Intent.EXTRA_SUBJECT,"");
                emailIntent.putExtra(Intent.EXTRA_TEXT,"");
                emailIntent.setType("text/html");
                startActivity(Intent.createChooser(emailIntent, "Send mail"));
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //Checking the request code of our request
        if (requestCode == 1) {

            //If permission is granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + supportNumber));
                startActivity(callIntent);
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(context, "Please accept the permissions to continue", Toast.LENGTH_LONG).show();
            }
        }
    }
}
