package com.example.wastetowealth;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;


public class FactCardsFragment extends Fragment {

    // Constants
    private final String LOG_TAG = FactCardsFragment.class.getSimpleName();
    private static final int HOME_SCREEN_CARD_LOADER_ID = 2;

    // Member variables
    private TextView mNoInternetConnectionTextView;
    private ImageView mHomeScreenCardImageView;
    private ArrayList<String> mCardsStringUrlList;
    private ProgressBar mProgressBar;
    private Random mRandom;
    private URL mHomeScreenCardURL;
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
        Objects.requireNonNull(getActivity()).setTitle("WasteToWealth");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mNoInternetConnectionTextView = Objects.requireNonNull(getActivity()).
                findViewById(R.id.no_internet_connection_text_view);

        // Referring to home screen card image view
        mHomeScreenCardImageView = getActivity().findViewById(R.id.home_screen_card_image_view);

        // Array list of url's of home screen cards
        mCardsStringUrlList = new ArrayList<>();
        mCardsStringUrlList.add("https://i.imgur.com/yhUBzaU.jpg");
        mCardsStringUrlList.add("https://i.imgur.com/prWG1Zw.jpg");
        mCardsStringUrlList.add("https://i.imgur.com/i5Vj3Tn.jpg");
        mCardsStringUrlList.add("https://i.imgur.com/6kazxKu.jpg");
        mCardsStringUrlList.add("https://i.imgur.com/ibJPSeV.jpg");

        // Referring to home screen progress bar
        mProgressBar = getActivity().findViewById(R.id.progressBar);

        // Making an object of a random picker to pick a random home screen card.
        mRandom = new Random();
        LoadFactCard();
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_icon:
                LoadFactCard();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void LoadFactCard() {
         /*
        Checking internet connectivity and initializing loaders if connection is available.
        */
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        if(!isConnected) {
            mNoInternetConnectionTextView.setVisibility(View.VISIBLE);
            mHomeScreenCardImageView.setVisibility(View.GONE);
            mNoInternetConnectionTextView.setText(R.string.no_internet_connection);
        } else {
            /*
            This get method for loader manager should be called after referring views otherwise
            null object reference error will occur.
             */
            mHomeScreenCardImageView.setVisibility(View.VISIBLE);
            mNoInternetConnectionTextView.setVisibility(View.GONE);
            mLoaderManager = getLoaderManager();
            mLoaderManager.restartLoader(HOME_SCREEN_CARD_LOADER_ID, null,
                    homeScreenCardLoaderCallbacks);
        }
    }

    private final LoaderManager.LoaderCallbacks<Bitmap> homeScreenCardLoaderCallbacks =
            new LoaderManager.LoaderCallbacks<Bitmap>() {
        @NonNull
        @Override
        public Loader<Bitmap> onCreateLoader(int id, @Nullable Bundle args) {
            mNoInternetConnectionTextView.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
            try {
                mHomeScreenCardURL = new URL(mCardsStringUrlList.get(mRandom.nextInt(5)));
            } catch (MalformedURLException e) {
                Log.e(LOG_TAG, "Malformed Uri");
            }
            return new FactCardLoader(getActivity(), mHomeScreenCardURL);
        }
        @Override
        public void onLoadFinished(@NonNull Loader<Bitmap> loader, Bitmap bitmap) {
            mProgressBar.setVisibility(View.GONE);
            mHomeScreenCardImageView.setVisibility(View.VISIBLE);
            mHomeScreenCardImageView.setImageBitmap(bitmap);

        }
        @Override
        public void onLoaderReset(@NonNull Loader<Bitmap> loader) {
            mHomeScreenCardImageView.setImageBitmap(null);
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        if (mLoaderManager != null) mLoaderManager.destroyLoader(HOME_SCREEN_CARD_LOADER_ID);
    }
}
