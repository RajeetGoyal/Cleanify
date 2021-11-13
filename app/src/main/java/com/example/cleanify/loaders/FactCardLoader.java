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

public class FactCardLoader extends AsyncTaskLoader<Bitmap> {

    private static final String LOG_TAG = FactCardLoader.class.getSimpleName();
    private static URL mUrl;

    public FactCardLoader(Context context, URL url) {
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
        try {
            urlConnection = (HttpURLConnection) mUrl.openConnection();
            if (urlConnection.getResponseCode() == 200) {
                bmp = BitmapFactory.decodeStream(urlConnection.getInputStream());
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        }catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage());
        }
        return bmp;
    }
}

