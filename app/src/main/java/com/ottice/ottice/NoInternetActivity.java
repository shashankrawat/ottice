package com.ottice.ottice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.ottice.ottice.utils.DialogBoxs;
import com.ottice.ottice.utils.Utilities;

/**
 * TODO: Add a class header comment!
 */

public class NoInternetActivity extends AppCompatActivity implements View.OnClickListener {

    private Button retryButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_internet_screen);

        init();

    }

    private void init() {
        retryButton = (Button) findViewById(R.id.retryButton);

        retryButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.retryButton:
                if(Utilities.isConnectedToInternet(NoInternetActivity.this)){
                    Intent intent = new Intent(NoInternetActivity.this, SelectLocationActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Snackbar.make(view,R.string.no_connection_message,Snackbar.LENGTH_LONG)
                            .setAction("Action",null).show();
                }
                break;
        }
    }
}
