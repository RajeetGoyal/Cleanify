package com.example.wastetowealth.WastePickup;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

class GeocoderLoader extends AsyncTaskLoader<List<Address>> {

    private final double mLatitude;
    private final double mLongitude;

    private static final String LOG_TAG = GeocoderLoader.class.getSimpleName();

    public GeocoderLoader(Context context, double latitude, double longitude) {
        super(context);
        mLatitude = latitude;
        mLongitude = longitude;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Nullable
    @Override
    public List<Address> loadInBackground() {
        List<Address> addresses = null;
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(mLatitude, mLongitude, 1);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error getting the address from the co-ordinates");
        }
        return addresses;
    }
}
