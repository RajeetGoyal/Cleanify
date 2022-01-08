package com.example.cleanify.viewmodels;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.cleanify.data.AppDatabase;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.Observable;
import rx.Observer;
import rx.schedulers.Schedulers;

public class MyProfileViewModel extends ViewModel {

    private static final String TAG = MyProfileViewModel.class.getSimpleName();

    MutableLiveData<Bitmap> profilePicture;
    MutableLiveData<String> name;
    MutableLiveData<String> emailAddress;
    MutableLiveData<String> contactNumber;

    public MutableLiveData<Bitmap> getProfilePicture() {
        if (profilePicture == null) {
            profilePicture = new MutableLiveData<>();
        }
        return profilePicture;
    }

    public MutableLiveData<String> getName() {
        if (name == null) {
            name = new MutableLiveData<>();
        }
        return name;
    }

    public MutableLiveData<String> getEmailAddress() {
        if (emailAddress == null) {
            emailAddress = new MutableLiveData<>();
        }
        return emailAddress;
    }

    public MutableLiveData<String> getContactNumber() {
        if (contactNumber == null) {
            contactNumber = new MutableLiveData<>();
        }
        return contactNumber;
    }

    public void loadProfileData() {
        FirebaseUser user = AppDatabase.getUser();

        if (user != null) {
            // Profile picture
            Uri profilePictureUri = user.getPhotoUrl();
            loadProfilePicture(profilePictureUri);

            // User name
            String name = user.getDisplayName();
            if (name != null) getName().setValue(name);

            // User email id
            String emailAddress = user.getEmail();
            if (emailAddress != null) getEmailAddress().setValue(emailAddress);

            // User contact number
            String contactNumber = user.getPhoneNumber();
            if (contactNumber != null) getEmailAddress().setValue(contactNumber);
        }
    }

    private void loadProfilePicture(Uri profilePictureUri) {
        Observable<Bitmap> myObservable = Observable.create(
                sub -> {
                    URL url;
                    HttpURLConnection urlConnection;
                    Bitmap bmp;
                    try {
                        url = new URL(String.valueOf(profilePictureUri));
                        urlConnection = (HttpURLConnection) url.openConnection();
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
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        profilePicture.postValue(bitmap);
                    }
                });

    }
}
