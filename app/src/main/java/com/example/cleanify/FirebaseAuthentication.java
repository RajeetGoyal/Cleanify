package com.example.cleanify;

import android.content.Intent;

import androidx.activity.result.ActivityResultLauncher;

import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

abstract class FirebaseAuthentication {

    public static void createSignInIntent(ActivityResultLauncher<Intent> signInLauncher) {
        // Using custom layout for authentication method picker.
        // We must provide a custom layout XML resource and configure at least one
        // provider button ID. It's important that that we set the button ID for every
        // provider that we have enabled.
        AuthMethodPickerLayout customLayout = new AuthMethodPickerLayout
                .Builder(R.layout.authentication_layout)
                .setEmailButtonId(R.id.custom_email_sign_in_button)
                .setPhoneButtonId(R.id.custom_phone_sign_in_button)
                .setGoogleButtonId(R.id.custom_google_sign_in_button)
                .setAnonymousButtonId(R.id.skip_sign_in_text_view)
                .build();

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());

        // Create and launch sign-in intent
        Intent signInIntent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAuthMethodPickerLayout(customLayout)
                .setAvailableProviders(providers)
                .setTheme(R.style.AppTheme)
                .build();
        signInLauncher.launch(signInIntent);
    }
}