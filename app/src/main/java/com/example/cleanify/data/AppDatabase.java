package com.example.cleanify.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import rx.Observable;

public class AppDatabase {

    public static FirebaseUser getUser() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        return firebaseAuth.getCurrentUser();
    }

    public static String getUserId() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            return user.getUid();
        }
        return null;
    }

    private static DatabaseReference getDatabaseReference() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        return database.getReference();
    }

    public static Observable<Boolean> placeOrder(Order order) {
        return Observable.create(subscriber -> {
            String userId = getUserId();
            if (userId != null) {
                getDatabaseReference().child("users").child(userId).setValue(order)
                        .addOnSuccessListener(aVoid -> {
                            subscriber.onNext(true);
                            subscriber.onCompleted();
                        })
                        .addOnFailureListener(subscriber::onError);
            } else {
                subscriber.onCompleted();
            }
        });
    }

}
