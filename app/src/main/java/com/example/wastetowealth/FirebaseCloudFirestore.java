package com.example.wastetowealth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;

public abstract class FirebaseCloudFirestore {

    private static final String LOG_TAG = FirebaseCloudFirestore.class.getSimpleName();

    private static double mLatitude;
    private static double mLongitude;

    public static double getLatitude() {
        return mLatitude;
    }
    public static double getLongitude() {
        return mLongitude;
    }
    public static void setLatitude(double latitude) {
        mLatitude = latitude;
    }
    public static void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public static void sendDataToFirebase(Queue<String> queue, final Activity activity, final ProgressDialog progressDialog) {
        String name = queue.remove();
        String contactDetails = queue.remove();
        String address = queue.remove();
        String landmark = queue.remove();
        String city = queue.remove();
        String pinCode = queue.remove();
        String day = queue.remove();
        String time = queue.remove();

        // Create a new user with the pickup details.
        Map<String, Object> user = new HashMap<>();
        user.put("mLatitude", mLatitude);
        user.put("mLongitude", mLongitude);
        user.put("name", name);
        user.put("contact_details", contactDetails);
        user.put("address", address);
        user.put("landmark", landmark);
        user.put("city", city);
        user.put("pin_code", pinCode);
        user.put("day_slot", day);
        user.put("time_slot", time);
        Log.v(LOG_TAG, name + " " + contactDetails + " " + address + " "
                + city + " " + pinCode + " " + day + " " + time);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // Add a new document with a generated ID
        db.collection("pickup_orders")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(LOG_TAG, "DocumentSnapshot added with ID: " +
                                documentReference.getId());
                        progressDialog.hide();
                        AlertDialog.Builder builder = new AlertDialog.
                                Builder(Objects.requireNonNull(activity));
                        builder.setTitle(R.string.pickup_order_successful_dialog_title);
                        builder.setMessage(R.string.pickup_order_successful_dialog_message);
                        builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                activity.finish();
                            }
                        });
                        builder.show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.hide();
                        Log.w(LOG_TAG, "Error adding document", e);
                    }
                });
    }
}