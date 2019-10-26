package com.example.wastetowealth;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import com.example.wastetowealth.WastePickup.WastePickup;
import com.firebase.ui.auth.AuthUI;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private static final int PROFILE_PICTURE_LOADER_ID = 1;
    // Constants
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private ImageView mUserProfilePictureImageView;
    private Uri mUserProfilePictureUri;
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

        // Setting up navigation drawer.
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Referring to Navigation Header View (profile picture image view, name text view, e-mail
        // id text view) from layout.
        View headerView = navigationView.getHeaderView(0);
        TextView mUserName = headerView.findViewById(R.id.user_name);
        TextView mUserEmail = headerView.findViewById(R.id.user_email);
        mUserProfilePictureImageView = headerView.findViewById(R.id.user_profile_picture);

        // Member Variables
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        if (user == null) {
            FirebaseAuthentication.createSignInIntent(this);
        }

        if (user != null) {
            String userName = user.getDisplayName();
            if (userName != null) {
                mUserName.setText(userName);
            }
            String userEmail = user.getEmail();
            if (userEmail != null) {
                mUserEmail.setText(userEmail);
            }
            mUserProfilePictureUri = user.getPhotoUrl();
        }

        // Performing duties in the presence of internet connection.
        if (checkInternetConnectivity()) {
            if (mUserProfilePictureUri != null) {
                LoaderManager mLoaderManager = getSupportLoaderManager();
                mLoaderManager.initLoader(PROFILE_PICTURE_LOADER_ID, null,
                        userProfilePictureLoaderCallbacks);
            } else {
                Log.v(LOG_TAG, "Profile picture Uri is null");
            }
        }

        // Setting up home screen cards fragment.
        final FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.fragments_container, new FactCardsFragment(), "Home_Screen_Cards");
        ft.commit();


        TextView myAccountTextView = headerView.findViewById(R.id.my_account);
        myAccountTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fragments_container, new MyProfileFragment(), "My_Profile");
                ft.addToBackStack(null);
                ft.commit();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * It checks for the status of internet connectivity.
     *
     * @return boolean
     */
    private boolean checkInternetConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager) MainActivity.this.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * It checks for the result of the authentication intent.
     *
     * @param requestCode It is the code of the specific request.
     * @param resultCode  It tells about the status of the result of the request.
     * @param data        No information
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FirebaseAuthentication.RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // Sign-in succeeded, set up the UI
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                // Do nothing.
            }
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
            Intent intent = new Intent(MainActivity.this, WastePickup.class);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_icon:
                return false;
            case R.id.sign_out:
                if (checkInternetConnectivity()) {
                    AuthUI.getInstance().signOut(this);
                    FirebaseAuthentication.createSignInIntent(this);
                } else
                    Toast.makeText(this, R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}