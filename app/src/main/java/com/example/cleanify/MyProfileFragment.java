package com.example.cleanify;

import static com.example.cleanify.utilities.Const.PROFILE_PICTURE_LOADER_ID;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;

import com.example.cleanify.loaders.ProfilePictureLoader;
import com.example.cleanify.utilities.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class MyProfileFragment extends Fragment {

    private final String LOG_TAG = MyProfileFragment.class.getSimpleName();
    private Uri mUserProfilePictureUri;
    private LoaderManager mLoaderManager;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    // For setting app bar title as per fragment.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle("My Profile");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();


        if (user != null) {
            // Profile picture
            mUserProfilePictureUri = user.getPhotoUrl();

            // User name
            String userName = user.getDisplayName();
            EditText editText = requireActivity().findViewById(R.id.editText);
            editText.setText(userName);

            // User email id
            String emailId = Objects.requireNonNull(user).getEmail();
            EditText editText2 = requireActivity().findViewById(R.id.editText2);
            editText2.setText(emailId);

            // User contact number
            String contactNumber = Objects.requireNonNull(user).getPhoneNumber();
            EditText editText3 = requireActivity().findViewById(R.id.editText3);
            editText3.setText(contactNumber);
        }

        if (Utils.isConnectedToInternet(requireActivity())) {
            /*
             This get method for loader manager should be called after referring views otherwise
             null object reference error
             will occur.
            */
            if (mUserProfilePictureUri != null) {
                mLoaderManager = LoaderManager.getInstance(requireActivity());
                mLoaderManager.initLoader(PROFILE_PICTURE_LOADER_ID, null,
                        userProfilePictureLoaderCallbacks);
            } else {
                Log.v(LOG_TAG, "Profile picture Uri is null");
            }
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.refresh_icon).setVisible(false);
        menu.findItem(R.id.sign_out).setVisible(false);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mLoaderManager != null) mLoaderManager.destroyLoader(PROFILE_PICTURE_LOADER_ID);
    }

    // Loader callbacks for user profile picture
    private final LoaderManager.LoaderCallbacks<Bitmap> userProfilePictureLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Bitmap>() {
        @NonNull
        @Override
        public androidx.loader.content.Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args){
            URL userProfilePictureUrl = null;
            try {
                userProfilePictureUrl = new URL(mUserProfilePictureUri.toString());
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Malformed Url");
            }
            return new ProfilePictureLoader(requireActivity(), userProfilePictureUrl);
        }
        @Override
        public void onLoadFinished(@NonNull androidx.loader.content.Loader<Bitmap> loader,
                                   Bitmap bitmap) {
            // Checking that a profile picture is available and if yes,
            // then setting it to user profile picture image view.
            if (bitmap != null) {
                ImageView imageView = requireActivity()
                        .findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }
        }
        @Override
        public void onLoaderReset(@NonNull androidx.loader.content.Loader<Bitmap> loader) {
        }
    };

}