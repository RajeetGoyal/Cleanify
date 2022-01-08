package com.example.cleanify;

import android.graphics.Bitmap;
import android.os.Bundle;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.loader.app.LoaderManager;

import com.example.cleanify.utilities.Utils;
import com.example.cleanify.viewmodels.FactCardsViewModel;


public class FactCardsFragment extends Fragment {

    private FactCardsViewModel viewModel;

    private TextView mNoInternetConnectionTextView;
    private ImageView mFactCardsImageView;
    private ProgressBar mProgressBar;

    public FactCardsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        // Get the view model
        viewModel = new ViewModelProvider(this).get(FactCardsViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fact_cards, container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle(R.string.app_name);

        mFactCardsImageView = requireActivity().findViewById(R.id.home_screen_card_image_view);
        mProgressBar = requireActivity().findViewById(R.id.progressBar);
        mNoInternetConnectionTextView = requireActivity().findViewById(R.id.no_internet_connection_text_view);

        // Create the observer (Updates the UI)
        final Observer<Boolean> progressBarObserver = isLoading -> {
            if (isLoading) mProgressBar.setVisibility(View.VISIBLE);
            else mProgressBar.setVisibility(View.GONE);
        };
        // Subscribe the observer
        viewModel.getIsLoading().observe(requireActivity(), progressBarObserver);

        final Observer<Boolean> internetConnectionObserver = isConnectedToInternet -> {
            if (isConnectedToInternet) mNoInternetConnectionTextView.setVisibility(View.GONE);
            else {
                mNoInternetConnectionTextView.setVisibility(View.VISIBLE);
                mNoInternetConnectionTextView.setText(R.string.no_internet_connection);
            }
        };
        viewModel.getIsConnectedToInternet().observe(requireActivity(), internetConnectionObserver);


        final Observer<Bitmap> factCardObserver = bitmap -> {
            mFactCardsImageView.setVisibility(View.VISIBLE);
            mFactCardsImageView.setImageBitmap(bitmap);
        };
        viewModel.getFactCard().observe(requireActivity(), factCardObserver);

        loadFactCard();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.refresh_icon) {
            loadFactCard();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadFactCard() {
        if (Utils.isConnectedToInternet(requireActivity())) {
            viewModel.loadFactCard();
        } else {
            viewModel.getIsConnectedToInternet().postValue(false);
        }
    }
}
