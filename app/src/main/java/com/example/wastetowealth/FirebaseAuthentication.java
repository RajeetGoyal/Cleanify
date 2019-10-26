package com.example.wastetowealth;

import android.app.Activity;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

abstract class FirebaseAuthentication {

    static final int RC_SIGN_IN = 1;
    private static final String LOG_TAG = FirebaseAuthentication.class.getSimpleName();

    public static void createSignInIntent(Activity activity) {
        // Using custom layout for authentication method picker.
        // We must provide a custom layout XML resource and configure at least one
        // provider button ID. It's important that that we set the button ID for every
        // provider that we have enabled.
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.authentication_layout)
                .setEmailButtonId(R.id.custom_email_signin_button)
                .setPhoneButtonId(R.id.custom_phone_signin_button)
                .setGoogleButtonId(R.id.custom_google_signin_button)
                .build();

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAuthMethodPickerLayout(customLayout)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }
}