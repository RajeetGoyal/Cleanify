package com.example.cleanify;

import static com.example.cleanify.utilities.Const.HOME_SCREEN_FRAGMENT_TAG;
import static com.example.cleanify.utilities.Const.PROFILE_PICTURE_LOADER_ID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.cleanify.loaders.ProfilePictureLoader;
import com.example.cleanify.utilities.Utils;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Constants
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private ImageView mUserProfilePictureImageView;
    private Uri mUserProfilePictureUri;

    private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
            new FirebaseAuthUIActivityResultContract(),
            this::onSignInResult
    );

    private final LoaderManager.LoaderCallbacks<Bitmap> userProfilePictureLoaderCallbacks = new
            LoaderManager.LoaderCallbacks<Bitmap>() {
                @NonNull
                @Override
                public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
                    URL userProfilePictureUrl = null;
                    try {
                        userProfilePictureUrl = new URL(mUserProfilePictureUri.toString());
                    } catch (MalformedURLException e) {
                        Log.e(LOG_TAG, "Malformed Url");
                    }
                    return new ProfilePictureLoader(MainActivity.this, userProfilePictureUrl);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap bitmap) {
                    mUserProfilePictureImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onLoaderReset(@NonNull Loader<Bitmap> loader) {
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Lookup for the firebase user
        // If user is null, then create the sign-in intent
        // Else, setup the main activity
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            FirebaseAuthentication.createSignInIntent(signInLauncher);
        } else {
            setupMainActivity(user);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        final NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_home);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            // Do nothing special
        } else if (id == R.id.nav_waste_pickup) {
            Intent intent = new Intent(MainActivity.this, PickupActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_icon:
                return false;
            case R.id.sign_out:
                if (Utils.isConnectedToInternet(this)) {
                    AuthUI.getInstance().signOut(this)
                            .addOnCompleteListener(task -> {
                                Toast.makeText(this, "Signed out successfully!", Toast.LENGTH_SHORT).show();
                            });
                    FirebaseAuthentication.createSignInIntent(signInLauncher);
                } else {
                    Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /***
     * This method handles the authentication result.
     * @param result Result of the firebase authentication
     */
    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            if (response != null && isProviderAuthentic(response)) {
                // Successfully signed in
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Signed in as guest!", Toast.LENGTH_SHORT).show();
            }

            // Set-up the UI
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) setupMainActivity(user);
        } else {
            // User cancelled the sign-in flow using back button. Close application.
            if (response == null) {
                finish();
            }
            // An error occurred during sign-in
            else {
                FirebaseAuthentication.createSignInIntent(signInLauncher);
                Toast.makeText(this, "Login failed. Please try again!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean isProviderAuthentic(IdpResponse response) {
        String providerType = response.getProviderType();
        if (providerType != null) {
            if (providerType.equals(GoogleAuthProvider.PROVIDER_ID)) return true;
            else if (providerType.equals(EmailAuthProvider.PROVIDER_ID)) return true;
            else return providerType.equals(PhoneAuthProvider.PROVIDER_ID);
        }
        return false;
    }

    private void setupMainActivity(FirebaseUser user) {
        setupNavigationDrawer(user);
        setupHomeScreenFragment();
    }

    private void setupNavigationDrawer(FirebaseUser user) {
        Toolbar toolbar = findViewById(R.id.toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Referring to the navigation header view
        View headerView = navigationView.getHeaderView(0);
        TextView userNameTextView = headerView.findViewById(R.id.user_name);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email);
        mUserProfilePictureImageView = headerView.findViewById(R.id.user_profile_picture);

        String userName = user.getDisplayName();
        if (userName != null) {
            userNameTextView.setText(userName);
        }
        String userEmail = user.getEmail();
        if (userEmail != null) {
            userEmailTextView.setText(userEmail);
        }
        mUserProfilePictureUri = user.getPhotoUrl();
        if (mUserProfilePictureUri != null) {
            if (Utils.isConnectedToInternet(this)) {
                LoaderManager mLoaderManager = getSupportLoaderManager();
                mLoaderManager.initLoader(PROFILE_PICTURE_LOADER_ID, null,
                        userProfilePictureLoaderCallbacks);
            }
        }

        TextView myAccountTextView = headerView.findViewById(R.id.my_profile);
        myAccountTextView.setOnClickListener(v -> {
            final FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft1 = fm.beginTransaction();
            ft1.replace(R.id.fragments_container, new MyProfileFragment(), "My_Profile");
            ft1.addToBackStack(null);
            ft1.commit();
            drawer.closeDrawer(GravityCompat.START);
        });
    }

    private void setupHomeScreenFragment() {
        // Setting up home screen cards fragment.
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.setReorderingAllowed(true);
        if (fm.findFragmentByTag(HOME_SCREEN_FRAGMENT_TAG) == null) {
            ft.replace(R.id.fragments_container, new FactCardsFragment(), HOME_SCREEN_FRAGMENT_TAG);
            ft.commit();
        }
    }

}