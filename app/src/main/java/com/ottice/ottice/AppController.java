package com.ottice.ottice;

import android.app.Application;
import android.support.v4.app.Fragment;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.ottice.ottice.utils.LruBitmapCache;

import java.lang.ref.WeakReference;

/**
 * TODO: Add a class header comment!
 */
public class AppController extends Application{

    private static final String TAG = AppController.class.getSimpleName();
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static WeakReference<Fragment> currentFragment;
    public static boolean isDashboardActivityActive;

    private static AppController mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    // AppController Instance method
    public static synchronized AppController getInstance(){
        return mInstance;
    }

    // volley Request Queue creating method
    public RequestQueue getRequestQueue(){
        if(mRequestQueue == null){
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    // volley Image loading method
    public ImageLoader getImageLoader(){
        getRequestQueue();
        if(mImageLoader == null){
            mImageLoader = new ImageLoader(this.mRequestQueue, new LruBitmapCache());
        }
        return this.mImageLoader;
    }

    // volley method to add a request to the queue
    public <T> void addToRequestQueue(Request<T> req, String tag){
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    // volley method to cancel the pending request in the queue
    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }
}
