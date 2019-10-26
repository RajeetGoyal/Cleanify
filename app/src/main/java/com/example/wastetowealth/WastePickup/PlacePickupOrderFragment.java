package com.example.wastetowealth.WastePickup;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.wastetowealth.FirebaseCloudFirestore;
import com.example.wastetowealth.R;
import com.google.android.material.textfield.TextInputEditText;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlacePickupOrderFragment extends Fragment {

    private String LOG_TAG = PlacePickupOrderFragment.class.getSimpleName();
    private static final int GEOCODER_LOADER_ID = 3;
    private ProgressDialog mProgressDialog;
    // Pickup Order Input fields
    private Spinner mDaySpinner;
    private Spinner mTimeSpinner;
    private LoaderManager mLoaderManager;
    private TextInputEditText mNameField, mNumberField, mAddressField, mLandmarkField, mPinCodeField,
            mCityField;

    public PlacePickupOrderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_pickup_order, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        mNameField = Objects.requireNonNull(getActivity()).findViewById(R.id.name_field);
        mNumberField = getActivity().findViewById(R.id.number_field);
        mAddressField = getActivity().findViewById(R.id.address_field);
        mLandmarkField = getActivity().findViewById(R.id.landmark_field);
        mPinCodeField = getActivity().findViewById(R.id.pin_code_field);
        mCityField = getActivity().findViewById(R.id.city_field);
        super.onActivityCreated(savedInstanceState);
        setUpSpinner();
        mLoaderManager = getLoaderManager();
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if(isConnected) {
            mLoaderManager.initLoader(GEOCODER_LOADER_ID, null, geocoderLoaderCallbacks);
        }
        setUpSpinner();
        Button button = Objects.requireNonNull(getActivity()).findViewById(R.id.submit_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });
    }

    private void setUpSpinner() {
        mDaySpinner = Objects.requireNonNull(getActivity()).findViewById(R.id.day_spinner);
        ArrayAdapter<CharSequence> dayAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.day_options, android.R.layout.simple_spinner_item);
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDaySpinner.setAdapter(dayAdapter);

        mTimeSpinner = getActivity().findViewById(R.id.time_spinner);
        ArrayAdapter<CharSequence> timeSlotAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.time_slot_options, android.R.layout.simple_spinner_item);
        timeSlotAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mTimeSpinner.setAdapter(timeSlotAdapter);
    }

    private void onButtonClick() {
        final Queue<String> queue = new LinkedList<>();
        String name = Objects.requireNonNull(mNameField.getText()).toString();
        queue.add(name);
        String contactDetails = Objects.requireNonNull(mNumberField.getText()).toString();
        queue.add(contactDetails);
        String address = Objects.requireNonNull(mAddressField.getText()).toString();
        queue.add(address);
        String landmark = Objects.requireNonNull(mLandmarkField.getText()).toString();
        queue.add(landmark);
        String city = Objects.requireNonNull(mCityField.getText()).toString();
        queue.add(city);
        String pinCode = Objects.requireNonNull(mPinCodeField.getText()).toString();
        queue.add(pinCode);
        String day = mDaySpinner.getSelectedItem().toString();
        queue.add(day);
        String time = mTimeSpinner.getSelectedItem().toString();
        queue.add(time);

        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getActivity()));
        builder.setMessage(R.string.pickup_request_confirmation_dialog);
        builder.setPositiveButton(R.string.yes_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mProgressDialog = new ProgressDialog(getContext());
                mProgressDialog.setMessage("Please wait..");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
                FirebaseCloudFirestore.sendDataToFirebase(queue, getActivity(), mProgressDialog);
            }
        });
        builder.setNegativeButton(R.string.no_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        builder.show();
    }

    private final LoaderManager.LoaderCallbacks<List<Address>> geocoderLoaderCallbacks = new
            LoaderManager.LoaderCallbacks<List<Address>>() {
        @NonNull
        @Override
        public Loader<List<Address>> onCreateLoader(int id, @Nullable Bundle args) {
            //Log.v("Data", " " + FirebaseCloudFirestore.getLatitude() + " " + FirebaseCloudFirestore.getLongitude());
            return new GeocoderLoader(getContext(), FirebaseCloudFirestore.getLatitude(), FirebaseCloudFirestore.getLongitude());
        }
        @Override
        public void onLoadFinished(@NonNull Loader<List<Address>> loader, List<Address> data) {
            if (data != null) {
                mAddressField.setText(data.get(0).getAddressLine(0));
                mPinCodeField.setText(data.get(0).getPostalCode());
                mCityField.setText(data.get(0).getLocality());
            }
        }
        @Override
        public void onLoaderReset(@NonNull Loader<List<Address>> loader) {

        }
    };

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
        mLoaderManager.destroyLoader(GEOCODER_LOADER_ID);
    }
}