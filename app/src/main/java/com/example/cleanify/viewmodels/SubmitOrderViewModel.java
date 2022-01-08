package com.example.cleanify.viewmodels;

import android.location.Address;
import android.location.Geocoder;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cleanify.R;
import com.example.cleanify.data.AppDatabase;
import com.example.cleanify.data.Order;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

public class SubmitOrderViewModel extends ViewModel {
    private MutableLiveData<String> address;
    private MutableLiveData<String> pinCode;
    private MutableLiveData<String> city;
    private MutableLiveData<Boolean> isOrderSubmitted;

    public MutableLiveData<String> getAddress() {
        if (address == null) {
            address = new MutableLiveData<>();
        }
        return address;
    }

    public MutableLiveData<String> getPinCode() {
        if (pinCode == null) {
            pinCode = new MutableLiveData<>();
        }
        return pinCode;
    }

    public MutableLiveData<String> getCity() {
        if (city == null) {
            city = new MutableLiveData<>();
        }
        return city;
    }

    public MutableLiveData<Boolean> getIsOrderSubmitted() {
        if (isOrderSubmitted == null) {
            isOrderSubmitted = new MutableLiveData<>();
        }
        return isOrderSubmitted;
    }

    public void loadAddress(Geocoder geocoder, double latitude, double longitude) {
        Observable<List<Address>> observable = Observable.create(subscriber -> {
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                subscriber.onNext(addresses);
                subscriber.onCompleted();
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(List<Address> addresses) {
                getAddress().postValue(addresses.get(0).getAddressLine(0));
                getPinCode().postValue(addresses.get(0).getPostalCode());
                getCity().postValue(addresses.get(0).getLocality());
            }
        });
    }

    public void placeOrder(Order order) {
        Observable<Boolean> observable = AppDatabase.placeOrder(order);
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Boolean isSuccessful) {
                        getIsOrderSubmitted().postValue(isSuccessful);
                    }
                });
    }

}
