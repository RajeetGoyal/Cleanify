package com.example.cleanify.loaders;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProfilePictureLoader extends AsyncTaskLoader<Bitmap> {

    private static final String LOG_TAG = ProfilePictureLoader.class.getSimpleName();
    private static URL mUrl;

    public ProfilePictureLoader(Context context, URL url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public Bitmap loadInBackground() {
        Bitmap bmp = null;
        HttpURLConnection urlConnection;
        Log.e(LOG_TAG, mUrl.toString());
        try {
            urlConnection = (HttpURLConnection) mUrl.openConnection();
            if (urlConnection.getResponseCode() == 200) {
                bmp = BitmapFactory.decodeStream(urlConnection.getInputStream());
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e) {
            Log.e(LOG_TAG, "Problem parsing the profile image from the url");
        }
        return bmp;
    }
}
