package com.example.cleanify;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.example.cleanify.data.FactCardUrls;
import com.example.cleanify.loaders.FactCardLoader;
import com.example.cleanify.utilities.Utils;

import java.net.MalformedURLException;
import java.net.URL;


public class FactCardsFragment extends Fragment {

    // Constants
    private final String LOG_TAG = FactCardsFragment.class.getSimpleName();
    private static final int HOME_SCREEN_CARD_LOADER_ID = 2;

    // Member variables
    private TextView mNoInternetConnectionTextView;
    private ImageView mFactCardsImageView;
    private ProgressBar mProgressBar;
    private LoaderManager mLoaderManager;

    public FactCardsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fact_cards, container,
                false);
    }

    // For setting app bar title as per fragment.
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle(R.string.app_name);

        mNoInternetConnectionTextView = requireActivity().
                findViewById(R.id.no_internet_connection_text_view);

        // Referring to home screen card image view
        mFactCardsImageView = requireActivity().findViewById(R.id.home_screen_card_image_view);

        // Referring to home screen progress bar
        mProgressBar = requireActivity().findViewById(R.id.progressBar);
    }

    @Override
    public void onResume() {
        super.onResume();
        LoadFactCard();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh_icon) {
            LoadFactCard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mLoaderManager != null) mLoaderManager.destroyLoader(HOME_SCREEN_CARD_LOADER_ID);
    }

    private void LoadFactCard() {
        // Checking internet connectivity and initializing loaders if connection is available.
        if (!Utils.isConnectedToInternet(requireActivity())) {
            mNoInternetConnectionTextView.setVisibility(View.VISIBLE);
            mFactCardsImageView.setVisibility(View.GONE);
            mNoInternetConnectionTextView.setText(R.string.no_internet_connection);
        } else {
            mFactCardsImageView.setVisibility(View.VISIBLE);
            mNoInternetConnectionTextView.setVisibility(View.GONE);
            mLoaderManager = LoaderManager.getInstance(requireActivity());
            mLoaderManager.restartLoader(HOME_SCREEN_CARD_LOADER_ID, null,
                    homeScreenCardLoaderCallbacks);
        }
    }

    private final LoaderManager.LoaderCallbacks<Bitmap> homeScreenCardLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<>() {
                @NonNull
                @Override
                public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
                    mNoInternetConnectionTextView.setVisibility(View.GONE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    URL homeScreenCardURL = null;
                    try {
                        homeScreenCardURL = new URL(FactCardUrls.getFactCardUrl());
                    } catch (MalformedURLException e) {
                        Log.e(LOG_TAG, "Malformed Uri");
                    }
                    return new FactCardLoader(requireActivity(), homeScreenCardURL);
                }

                @Override
                public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap bitmap) {
                    mProgressBar.setVisibility(View.GONE);
                    mFactCardsImageView.setVisibility(View.VISIBLE);
                    mFactCardsImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onLoaderReset(@NonNull Loader<Bitmap> loader) {
                    mFactCardsImageView.setImageBitmap(null);
                }
            };
}
