package com.example.m_hike;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HikeFragment extends Fragment {

    TextInputLayout nameInputLayout, locationInputLayout, lengthInputLayout, descriptionInputLayout,
            otherEquipmentInputLayout, participantsInputLayout, durationInputLayout;
    TextInputEditText nameInput, locationInput, lengthInput, descriptionInput,
            otherEquipmentInput, participantsInput, durationInput;
    DatePicker datePicker;
    SwitchCompat parkingSwitch;
    Spinner difficultySpinner;
    CheckBox satelliteCB, polesCB, headlampCB, waterCB, gpsCB, batteryCB, otherCB;
    Button saveBtn, cancelBtn, observationBtn;
    MHikeDatabase db;
    ExecutorService executors = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hike, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Input Layout
        nameInputLayout = requireView().findViewById(R.id.nameInputLayout);
        locationInputLayout = requireView().findViewById(R.id.locationInputLayout);
        lengthInputLayout = requireView().findViewById(R.id.lengthInputLayout);
        descriptionInputLayout = requireView().requireViewById(R.id.descriptionInputLayout);
        otherEquipmentInputLayout = requireView().findViewById(R.id.otherEquipmentInputLayout);
        participantsInputLayout = requireView().findViewById(R.id.participantsInputLayout);
        durationInputLayout = requireView().findViewById(R.id.durationInputLayout);
        //Input Edit Text
        nameInput = requireView().findViewById(R.id.nameInput);
        locationInput = requireView().findViewById(R.id.locationInput);
        lengthInput = requireView().findViewById(R.id.lengthInput);
        descriptionInput = requireView().findViewById(R.id.descriptionInput);
        otherEquipmentInput = requireView().findViewById(R.id.otherEquipmentInput);
        participantsInput = requireView().findViewById(R.id.participantsInput);
        durationInput = requireView().findViewById(R.id.durationInput);
        //Date Picker
        datePicker = requireView().findViewById(R.id.datePicker);
        //Switch
        parkingSwitch = requireView().findViewById(R.id.parkingSwitch);
        //Spinner
        difficultySpinner = requireView().findViewById(R.id.difficultySpinner);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(requireContext().getApplicationContext(), R.layout.drop_down_item, getResources().getStringArray(R.array.difficulty));
        difficultySpinner.setAdapter(arrayAdapter);
        //Checkbox
        satelliteCB = requireView().findViewById(R.id.satelliteCB);
        polesCB = requireView().findViewById(R.id.polesCB);
        headlampCB = requireView().findViewById(R.id.headlampCB);
        waterCB = requireView().findViewById(R.id.waterCB);
        gpsCB = requireView().findViewById(R.id.gpsCB);
        batteryCB = requireView().findViewById(R.id.batteryCB);
        otherCB = requireView().findViewById(R.id.otherCB);
        //Button
        saveBtn = requireView().findViewById(R.id.saveBtn);
        cancelBtn = requireView().findViewById(R.id.cancelBtn);
        observationBtn = requireView().findViewById(R.id.observationBtn);
        //Database
        db = MHikeDatabase.getInstance(requireActivity().getApplicationContext());

        otherCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                otherEquipmentInputLayout.setVisibility(View.VISIBLE);
            } else {
                otherEquipmentInputLayout.setVisibility(View.GONE);
            }
        });

        saveBtn.setOnClickListener(v -> {
            if(!isEmpty(nameInput) && !isEmpty(locationInput) && !isEmpty(lengthInput) && !isEmpty(participantsInput) && !isEmpty(durationInput)) {
                String name = Objects.requireNonNull(nameInput.getText()).toString();
                String location = Objects.requireNonNull(locationInput.getText()).toString();
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();
                String date = day + "/" + month + "/" + year;
                boolean parking = parkingSwitch.isChecked();
                double length = Double.parseDouble(Objects.requireNonNull(lengthInput.getText()).toString());
                String difficultyString = difficultySpinner.getSelectedItem().toString();
                int difficulty = Integer.parseInt(difficultyString.substring(0, 1));
                String description = "";
                if(!isEmpty(descriptionInput))
                    description = Objects.requireNonNull(descriptionInput.getText()).toString();
                StringBuilder equipmentsBuilder = new StringBuilder();
                if(satelliteCB.isChecked())
                    equipmentsBuilder.append("satellite,");
                if(polesCB.isChecked())
                    equipmentsBuilder.append("poles,");
                if(headlampCB.isChecked())
                    equipmentsBuilder.append("headlamp,");
                if(waterCB.isChecked())
                    equipmentsBuilder.append("water,");
                if(gpsCB.isChecked())
                    equipmentsBuilder.append("gps,");
                if(batteryCB.isChecked())
                    equipmentsBuilder.append("battery,");
                if(otherCB.isChecked()) {
                    if(isEmpty(otherEquipmentInput)) {
                        otherEquipmentInputLayout.setError("Please specify other equipments.");
                    } else {
                        String[] otherEquipments = Objects.requireNonNull(otherEquipmentInput.getText()).toString().split(",");
                        for (String e:otherEquipments) {
                            if(!Objects.equals(e, "") && e != null){
                                e = e.trim();
                                equipmentsBuilder.append(e).append(",");
                            }
                        }
                    }
                }
                String equipments = equipmentsBuilder.toString();
                equipments = equipments.substring(0, equipments.length() - 1);
                int participants = Integer.parseInt(Objects.requireNonNull(participantsInput.getText()).toString());
                double duration = Double.parseDouble(Objects.requireNonNull(durationInput.getText()).toString());
                Hike hike = new Hike(name, location, date, parking, length, difficulty, description, equipments, participants, duration);
                insertHike(hike);
            } else {
                if(isEmpty(nameInput))
                    nameInputLayout.setError("Please enter hike name");
                if(isEmpty(locationInput))
                    locationInputLayout.setError("Please enter hike location");
                if(isEmpty(lengthInput))
                    lengthInputLayout.setError("Please enter hike length");
                if(isEmpty(participantsInput))
                    participantsInputLayout.setError("Please enter number of participants");
                if(isEmpty(durationInput))
                    durationInputLayout.setError("Please enter hike duration");
            }
        });

        cancelBtn.setOnClickListener(v -> getAllHikes());

//        locationInput.setOnFocusChangeListener((v, hasFocus) -> {
//            if(hasFocus) {
//                Intent i = new Intent(requireContext(), MapActivity.class);
//                startActivity(i);
//            }
//        });
    }

    private boolean isEmpty(EditText input) {
        return input.getText().toString().trim().length() == 0;
    }

    private void insertHike(Hike hike) {
        Runnable insertHike = () -> db.hikeDao().insertHike(hike);
        executors.execute(insertHike);
    }

    private void getAllHikes() {
        Runnable getAllHikes = () -> {
            List<Hike> hikeList = db.hikeDao().getAllHikes();
        };
        executors.execute(getAllHikes);
    }
}