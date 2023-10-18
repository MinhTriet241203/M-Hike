package com.example.m_hike;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

public class HikeFragment extends Fragment {

    Spinner difficultySpinner;
    TextInputLayout descriptionInputLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hike, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        difficultySpinner = requireView().findViewById(R.id.difficultySpinner);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext().getApplicationContext(), R.layout.drop_down_item, getResources().getStringArray(R.array.difficulty));
        difficultySpinner.setAdapter(arrayAdapter);
    }
}