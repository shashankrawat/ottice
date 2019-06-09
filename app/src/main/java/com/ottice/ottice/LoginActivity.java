package com.ottice.ottice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.ottice.ottice.fragments.LoginFragment;

/**
 * Created by shashank.rawat on 13-07-2017.
 */

public class LoginActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        if(findViewById(R.id.container) != null){
            if(savedInstanceState != null){
                return;
            }
            LoginFragment loginFragment = new LoginFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, loginFragment).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.container);
        fragment.onActivityResult(requestCode, resultCode, data);
    }

}
