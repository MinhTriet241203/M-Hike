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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

public class HikeFragment extends Fragment {

    Spinner difficultySpinner;
    TextInputLayout otherInputLayout, durationInputLayout;
    TextInputEditText durationInput;
    CheckBox otherCB;

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
        otherCB = requireView().findViewById(R.id.otherCB);
        otherInputLayout = requireView().findViewById(R.id.otherEquipmentInputLayout);
        durationInput = requireView().findViewById(R.id.durationInput);
        durationInputLayout = requireView().findViewById(R.id.durationInputLayout);

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(requireContext().getApplicationContext(), R.layout.drop_down_item, getResources().getStringArray(R.array.difficulty));
        difficultySpinner.setAdapter(arrayAdapter);

        otherCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                otherInputLayout.setVisibility(View.VISIBLE);
            } else {
                otherInputLayout.setVisibility(View.GONE);
            }
        });
    }

    private boolean isEmpty(EditText input) {
        return input.getText().toString().trim().length() == 0;
    }
}