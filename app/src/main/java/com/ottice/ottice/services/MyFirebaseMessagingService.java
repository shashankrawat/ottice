package com.ottice.ottice.services;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ottice.ottice.AppController;
import com.ottice.ottice.DashboardActivity;
import com.ottice.ottice.utils.Common;
import com.ottice.ottice.utils.NotificationUtils;
import com.ottice.ottice.utils.PrefrencesClass;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * TODO: Add a class header comment!
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            handleNotification(remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> message = remoteMessage.getData();
            try {
                JSONObject json = new JSONObject(message);
                Log.e("TITLE",""+json);
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Json Exception: " + e.getMessage());
            }
        }
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }



    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {

            if(AppController.currentFragment != null){
                Log.e("CURRENT FRAGMENT",""+AppController.currentFragment.get().getClass().getSimpleName());
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Common.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }
        }
    }



    private void handleDataMessage(JSONObject data) {

        NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
        Intent resultIntent = new Intent(getApplicationContext(), DashboardActivity.class);

        try {
            String title = data.getString("time");
            String message = data.getString("message");

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);


            resultIntent.putExtra("message", data.toString());
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);


            if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                if(AppController.isDashboardActivityActive) {
                    if (AppController.currentFragment != null) {
                        // app is in foreground, broadcast the push message
                        Intent pushNotification = new Intent(Common.PUSH_NOTIFICATION);
                        pushNotification.putExtra("message", message);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                    } else {
                        Intent pushNotification = new Intent(Common.NOTIFICATION_DATA_RECEIVER);
                        LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                        // play notification sound
                        notificationUtils.playNotificationSound();
                    }
                }else {
                    notificationUtils.showNotificationMessage(title, message, resultIntent, null);
                    // play notification sound
                    notificationUtils.playNotificationSound();

                    PrefrencesClass.savePreferenceBoolean(getApplicationContext(), Common.NOTIFICATION_DATA_RECEIVER, true);

                }
            } else {
                // app is in background, show the notification in notification tray
                notificationUtils.showNotificationMessage(title, message, resultIntent, null);
                // play notification sound
                notificationUtils.playNotificationSound();

                PrefrencesClass.savePreferenceBoolean(getApplicationContext(), Common.NOTIFICATION_DATA_RECEIVER, true);

            }
        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

}
