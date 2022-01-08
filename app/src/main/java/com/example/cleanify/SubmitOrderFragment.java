package com.example.cleanify;


import android.annotation.SuppressLint;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cleanify.data.AppDatabase;
import com.example.cleanify.data.Order;
import com.example.cleanify.utilities.Const;
import com.example.cleanify.viewmodels.SubmitOrderViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;

/**
 * A simple {@link Fragment} subclass.
 */
public class SubmitOrderFragment extends Fragment {
    // Pickup Order fields
    private TextInputEditText mNameField, mContactField, mAddressField, mLandmarkField, mPinCodeField,
            mCityField;
    private AutoCompleteTextView mDayField, mTimeField;
    private TextInputLayout mNameFieldLayout;
    private TextInputLayout mContactFieldLayout;
    private TextInputLayout mAddressFieldLayout;
    private TextInputLayout mPinCodeFieldLayout;
    private TextInputLayout mCityFieldLayout;
    private TextInputLayout mDayFieldLayout;
    private TextInputLayout mTimeFieldLayout;
    private Double mLatitude, mLongitude;

    private SubmitOrderViewModel viewModel;

    public SubmitOrderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(this).get(SubmitOrderViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_place_pickup_order, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get coordinates data
        Bundle arguments = getArguments();
        if (arguments != null) {
            mLatitude = arguments.getDouble(Const.LATITUDE_KEY);
            mLongitude = arguments.getDouble(Const.LONGITUDE_KEY);
        }

        setupTextInputFields();

        setupSpinnerFields();

        Button button = requireActivity().findViewById(R.id.submit_button);
        button.setOnClickListener(v -> onSubmitButtonClick());

        Observer<String> addressObserver = s -> mAddressField.setText(s);
        viewModel.getAddress().observe(requireActivity(), addressObserver);

        Observer<String> pinCodeObserver = s -> mPinCodeField.setText(s);
        viewModel.getPinCode().observe(requireActivity(), pinCodeObserver);

        Observer<String> cityObserver = s -> mCityField.setText(s);
        viewModel.getCity().observe(requireActivity(), cityObserver);

        Observer<Boolean> orderStatusObserver = isSuccessful -> {
            if (isSuccessful) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle(R.string.pickup_order_success_title);
                builder.setMessage(R.string.pickup_order_success_message);
                builder.setNeutralButton("OK", (dialogInterface, i) -> requireActivity().finish());
                builder.show();
            }
        };
        viewModel.getIsOrderSubmitted().observe(requireActivity(), orderStatusObserver);

        Geocoder geocoder = new Geocoder(requireActivity(), Locale.getDefault());
        viewModel.loadAddress(geocoder, mLatitude, mLongitude);
    }

    @Override
    public void onDestroyOptionsMenu() {
        super.onDestroyOptionsMenu();
    }

    private void onSubmitButtonClick() {
        boolean isValidationSuccessful = validateFields();
        boolean isUserLoggedIn = validateAuthentication();

        if (isValidationSuccessful && isUserLoggedIn) {
            Order order = createOrder();
            viewModel.placeOrder(order);
        }
    }

    /***
     * @return An {@link Order} object that contains the data from input fields.
     */
    private Order createOrder() {
        Order order = new Order();
        order.setName(String.valueOf(mNameField.getText()));
        order.setContactNumber(String.valueOf(mContactField.getText()));
        order.setAddress(String.valueOf(mAddressField.getText()));
        order.setLandmark(String.valueOf(mLandmarkField.getText()));
        order.setLatitude(mLatitude);
        order.setLongitude(mLongitude);
        order.setPinCode(String.valueOf(mPinCodeField.getText()));
        order.setCity(String.valueOf(mCityField.getTag()));
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.ENGLISH);
            order.setDateTime(dateFormat.format(new Date()));
        } catch (Exception ignored) {
        }
        order.setDaySlot(String.valueOf(mDayField.getText()));
        order.setTimeSlot(String.valueOf(mTimeField.getText()));
        return order;
    }

    /***
     * Set view references for the text input fields
     */
    private void setupTextInputFields() {
        mNameField = requireActivity().findViewById(R.id.name_field);
        mContactField = requireActivity().findViewById(R.id.number_field);
        mAddressField = requireActivity().findViewById(R.id.address_field);
        mLandmarkField = requireActivity().findViewById(R.id.landmark_field);
        mPinCodeField = requireActivity().findViewById(R.id.pin_code_field);
        mCityField = requireActivity().findViewById(R.id.city_field);

        mNameFieldLayout = requireActivity().findViewById(R.id.name_input_layout);
        mContactFieldLayout = requireActivity().findViewById(R.id.contact_input_layout);
        mAddressFieldLayout = requireActivity().findViewById(R.id.address_input_layout);
        mPinCodeFieldLayout = requireActivity().findViewById(R.id.pin_code_input_layout);
        mCityFieldLayout = requireActivity().findViewById(R.id.city_input_layout);
        mDayFieldLayout = requireActivity().findViewById(R.id.day_input_layout);
        mTimeFieldLayout = requireActivity().findViewById(R.id.time_input_layout);
    }

    /***
     * Set view references for the spinner fields.
     * Update spinner options based on the current time of the day.
     */
    private void setupSpinnerFields() {
        // Initialize day spinner
        List<String> dayList = getDayList();
        mDayField = requireActivity().findViewById(R.id.day_field);
        ArrayAdapter<String> dayArrayAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, dayList);
        mDayField.setAdapter(dayArrayAdapter);

        // Initialize time spinner
        List<String> timeList = getTimeList();
        mTimeField = requireActivity().findViewById(R.id.time_field);
        ArrayAdapter<String> timeArrayAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_dropdown_item, timeList);
        mTimeField.setAdapter(timeArrayAdapter);

        // Default state of time field
        mTimeFieldLayout.setEnabled(false);

        // Set day spinner options based on the time of the day.
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour >= 15) {
            dayList.remove(getString(R.string.today));
        }
        dayArrayAdapter.notifyDataSetChanged();


        // Setup time field options based on selected day and current time.
        mDayField.setOnItemClickListener((parent, view, position, id) -> {
            String selectedItem = String.valueOf(mDayField.getText());
            if (selectedItem.equals("")) {
                mTimeFieldLayout.setEnabled(false);
            }
            else if (selectedItem.equals(getString(R.string.today))) {
                mTimeFieldLayout.setEnabled(true);
                if (hour >= 12) {
                    timeList.remove(getString(R.string.morning_slot_1));
                    timeList.remove(getString(R.string.morning_slot_2));
                    timeList.remove(getString(R.string.afternoon_slot));
                } else if (hour >= 9) {
                    timeList.remove(getString(R.string.morning_slot_1));
                    timeList.remove(getString(R.string.morning_slot_2));
                } else if (hour >= 6) {
                    timeList.remove(getString(R.string.morning_slot_1));
                }
                timeArrayAdapter.notifyDataSetChanged();
            }
            else if (selectedItem.equals(getString(R.string.tomorrow))) {
                mTimeFieldLayout.setEnabled(true);
                timeList.clear();
                timeList.addAll(getTimeList());
                timeArrayAdapter.notifyDataSetChanged();
            }
        });

    }

    private boolean validateAuthentication() {
        return AppDatabase.getUser() != null;
    }

    /***
     * This method validates the data of input fields.
     * @return if fields are correct (true), else (false)
     */
    @SuppressLint("ClickableViewAccessibility")
    private boolean validateFields() {
        boolean result = true;

        String name = String.valueOf(mNameField.getText());
        if (name.length() == 0) {
            mNameFieldLayout.setError(getResources().getString(R.string.required));
            mNameField.setOnTouchListener((v, event) -> {
                // Required to reset the size of the field
                mNameFieldLayout.setErrorEnabled(false);
                // Required to remove the error message
                mNameFieldLayout.setError(null);
                return false;
            });

            result = false;
        }

        String contactNumber = String.valueOf(mContactField.getText());
        if (contactNumber.length() == 0) {
            mContactFieldLayout.setError(getResources().getString(R.string.required));
            mContactField.setOnTouchListener((v, event) -> {
                mContactFieldLayout.setErrorEnabled(false);
                mContactFieldLayout.setError(null);
                return false;
            });

            result = false;
        }

        String address = String.valueOf(mAddressField.getText());
        if (address.length() == 0) {
            mAddressFieldLayout.setError(getResources().getString(R.string.required));
            mAddressField.setOnTouchListener((v, event) -> {
                mAddressFieldLayout.setErrorEnabled(false);
                mAddressFieldLayout.setError(null);
                return false;
            });

            result = false;
        }

        String pinCode = String.valueOf(mPinCodeField.getText());
        if (pinCode.length() == 0) {
            mPinCodeFieldLayout.setError(getResources().getString(R.string.required));
            mPinCodeField.setOnTouchListener((v, event) -> {
                mPinCodeFieldLayout.setErrorEnabled(false);
                mPinCodeFieldLayout.setError(null);
                return false;
            });

            result = false;
        }

        String city = String.valueOf(mCityField.getText());
        if (city.length() == 0) {
            mCityFieldLayout.setError(getResources().getString(R.string.required));
            mCityField.setOnTouchListener((v, event) -> {
                mCityFieldLayout.setErrorEnabled(false);
                mCityFieldLayout.setError(null);
                return false;
            });

            result = false;
        }

        String day = String.valueOf(mDayField.getText());
        if (day.length() == 0) {
            mDayFieldLayout.setError(getResources().getString(R.string.required));
            mDayField.setOnTouchListener((v, event) -> {
                mDayFieldLayout.setErrorEnabled(false);
                mDayFieldLayout.setError(null);
                return false;
            });
            // Required to handle the click of the dropdown icon
            mDayFieldLayout.setEndIconOnClickListener(v -> {
                mDayFieldLayout.setErrorEnabled(false);
                mDayFieldLayout.setError(null);
                mDayField.showDropDown();
            });
            result = false;
        }

        String time = String.valueOf(mTimeField.getText());
        if (time.length() == 0) {
            mTimeFieldLayout.setError(getResources().getString(R.string.required));
            mTimeField.setOnTouchListener((v, event) -> {
                mTimeFieldLayout.setErrorEnabled(false);
                mTimeFieldLayout.setError(null);
                return false;
            });
            // Required to handle the click of the dropdown icon
            mTimeFieldLayout.setEndIconOnClickListener(v -> {
                mTimeFieldLayout.setErrorEnabled(false);
                mTimeFieldLayout.setError(null);
                mTimeField.showDropDown();
            });
            result = false;
        }

        return result;
    }

    private List<String> getDayList() {
        List<String> dayList = new ArrayList<>();
        dayList.add(getString(R.string.today));
        dayList.add(getString(R.string.tomorrow));
        return dayList;
    }

    private List<String> getTimeList() {
        List<String> timeList = new ArrayList<>();
        timeList.add(getString(R.string.morning_slot_1));
        timeList.add(getString(R.string.morning_slot_2));
        timeList.add(getString(R.string.afternoon_slot));
        timeList.add(getString(R.string.evening_slot));
        return timeList;
    }
}