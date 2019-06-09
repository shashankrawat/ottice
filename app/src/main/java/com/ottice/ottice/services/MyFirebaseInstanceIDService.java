package com.ottice.ottice.services;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.PrefrencesClass;

/**
 * TODO: Add a class header comment!
 */

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("REFRESHED TOKEN", "" + refreshedToken);

        PrefrencesClass.savePreference(getApplicationContext(), Common.FIREBASE_NOTIFICATION_TOKEN, refreshedToken);
    }
}
