package com.example.cleanify.viewmodels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cleanify.data.FactCardUrls;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.schedulers.Schedulers;

public class FactCardsViewModel extends ViewModel {

    private static final String TAG = FactCardsViewModel.class.getSimpleName();

    MutableLiveData<Bitmap> factCard;
    MutableLiveData<Boolean> isLoading;
    MutableLiveData<Boolean> isConnectedToInternet;

    public MutableLiveData<Bitmap> getFactCard() {
        if (factCard == null) {
            factCard = new MutableLiveData<>();
        }
        return factCard;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        if (isLoading == null) {
            isLoading = new MutableLiveData<>();
        }
        return isLoading;
    }

    public MutableLiveData<Boolean> getIsConnectedToInternet() {
        if (isConnectedToInternet == null) {
            isConnectedToInternet = new MutableLiveData<>();
        }
        return isConnectedToInternet;
    }



    public void loadFactCard() {
        Observable<Bitmap> myObservable = Observable.create(
                sub -> {
                    isLoading.postValue(true);
                    URL factCardUrl;
                    HttpURLConnection urlConnection;
                    Bitmap bmp;
                    try {
                        factCardUrl = new URL(FactCardUrls.getFactCardUrl());
                        urlConnection = (HttpURLConnection) factCardUrl.openConnection();
                        if (urlConnection.getResponseCode() == 200) {
                            bmp = BitmapFactory.decodeStream(urlConnection.getInputStream());
                            sub.onNext(bmp);
                        } else {
                            Log.e(TAG, "Error response code: " + urlConnection.getResponseCode());
                        }
                        sub.onCompleted();
                    } catch (IOException e) {
                        sub.onError(e);
                    }
                }
        );

        myObservable
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<>() {
                    @Override
                    public void onCompleted() {
                        isLoading.postValue(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        factCard.postValue(bitmap);
                    }
                });
    }

}
