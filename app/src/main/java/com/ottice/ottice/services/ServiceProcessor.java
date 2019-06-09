package com.ottice.ottice.services;

import android.graphics.Bitmap;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.ottice.ottice.AppController;
import com.ottice.ottice.R;

import java.io.UnsupportedEncodingException;

/**
 * TODO: Add a class header comment!
 */
public class ServiceProcessor {

    private static Bitmap imageBitmap;

    public static void makeImageRequest(String url, final ImageView image) {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        // If you are using NetworkImageView
//        imgNetWorkView.setImageUrl(url, imageLoader);

        imageLoader.setBatchedResponseDelay(DefaultRetryPolicy.DEFAULT_MAX_RETRIES);

        // If you are using normal ImageView
        imageLoader.get(url, new ImageLoader.ImageListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Image Error", "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    // load image into imageview
                    image.setImageBitmap(response.getBitmap());
                }
            }
        });


        // Loading image with placeholder and error image
        imageLoader.get(url, ImageLoader.getImageListener(
                image, R.mipmap.placeholder, R.mipmap.placeholder_1));

        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                Log.d("DATA",""+data);
                // handle data, like converting it to xml, json, bitmap etc.,
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

    }




    public static Bitmap makeImageRequest(String url)
    {
        ImageLoader imageLoader = AppController.getInstance().getImageLoader();

        imageLoader.setBatchedResponseDelay(DefaultRetryPolicy.DEFAULT_MAX_RETRIES);

        // If you are using normal ImageView
        imageLoader.get(url, new ImageLoader.ImageListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Image Error", "Image Load Error: " + error.getMessage());
            }

            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean arg1) {
                if (response.getBitmap() != null) {
                    imageBitmap = response.getBitmap();
                }
            }
        });

        Cache cache = AppController.getInstance().getRequestQueue().getCache();
        Cache.Entry entry = cache.get(url);
        if(entry != null){
            try {
                String data = new String(entry.data, "UTF-8");
                Log.d("DATA",""+data);
                // handle data, like converting it to xml, json, bitmap etc.,
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        return imageBitmap;
    }



}
