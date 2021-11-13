package com.example.cleanify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.LocationRequest;

public class PickupActivity extends AppCompatActivity {

    private final String LOG_TAG = PickupActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waste_pickup);

//        // Initialize Places.
//        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_cloud_api_key));
//        // Create a new Places client instance.
//        PlacesClient placesClient = Places.createClient(this);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragments_container, new ChooseMapLocationFragment())
                .commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LocationRequest.PRIORITY_HIGH_ACCURACY) {
            switch (resultCode) {
                case Activity.RESULT_OK:
                    // All required changes were successfully made
                    Log.i(LOG_TAG, "onActivityResult: GPS Enabled by user");
                    break;
                case Activity.RESULT_CANCELED:
                    // The user was asked to change settings, but chose not to
                    Log.i(LOG_TAG, "onActivityResult: User rejected GPS request");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * To control the function  of back button.
     */
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}