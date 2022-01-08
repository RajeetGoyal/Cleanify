package com.example.cleanify;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.cleanify.viewmodels.MyProfileViewModel;

public class MyProfileFragment extends Fragment {

    private MyProfileViewModel viewModel;

    public MyProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        viewModel = new ViewModelProvider(this).get(MyProfileViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requireActivity().setTitle("My Profile");

        Observer<Bitmap> profilePictureObserver = bitmap -> {
            ImageView imageView = requireActivity()
                    .findViewById(R.id.imageView);
            imageView.setImageBitmap(bitmap);
        };
        viewModel.getProfilePicture().observe(requireActivity(), profilePictureObserver);

        Observer<String> nameObserver = s -> {
            EditText editText = requireActivity().findViewById(R.id.name_edit_text);
            editText.setText(s);
        };
        viewModel.getName().observe(requireActivity(), nameObserver);

        Observer<String> emailIdObserver = s -> {
            EditText editText = requireActivity().findViewById(R.id.email_address_edit_text);
            editText.setText(s);
        };
        viewModel.getEmailAddress().observe(requireActivity(), emailIdObserver);

        Observer<String> contactNumberObserver = s -> {
            EditText editText = requireActivity().findViewById(R.id.contact_number_edit_text);
            editText.setText(s);
        };
        viewModel.getContactNumber().observe(requireActivity(), contactNumberObserver);

        viewModel.loadProfileData();
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
    }
}