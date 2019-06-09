package com.ottice.ottice;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.ottice.ottice.services.ServiceConstants;
import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.Utilities;

/**
 * Created by shashank.rawat on 25-07-2017.
 */

public class PrivacyPolicyViewActivity extends AppCompatActivity {

    private Context context;
    private WebView privacyPolicyView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy_policy_view_screen);

        init();

        if (Utilities.isConnectedToInternet(context)) {
            loadwebview();
        } else {
            checkInternetConnection();
        }

    }

    private void init() {
        context = this;

        privacyPolicyView = (WebView) findViewById(R.id.privacyPolicyView);

        privacyPolicyView.loadUrl(ServiceConstants.PRIVACY_POLICY_URL);
    }



    private void loadwebview() {
        privacyPolicyView.getSettings().setJavaScriptEnabled(true);

        privacyPolicyView.loadUrl(ServiceConstants.PRIVACY_POLICY_URL);
        // setting progress_bar dialog
        final DialogBoxs progressDialog = new DialogBoxs(context);

        privacyPolicyView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                progressDialog.showProgressBarDialog();
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, final String url) {
                progressDialog.hideProgressBar();
            }
        });

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
                    loadwebview();
                } else {
                    dialog.dismiss();
                    checkInternetConnection();
                }
            }
        });
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }
}
