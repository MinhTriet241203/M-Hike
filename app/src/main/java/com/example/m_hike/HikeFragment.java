package com.example.m_hike;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class HikeFragment extends Fragment implements androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener {

    //Hike Info
    int hikeId = 0;
    Hike hike1;
    TextView hikeTitle;
    TextInputLayout nameInputLayout, locationInputLayout, lengthInputLayout, descriptionInputLayout,
            otherEquipmentInputLayout, participantsInputLayout, durationInputLayout;
    TextInputEditText nameInput, locationInput, lengthInput, descriptionInput,
            otherEquipmentInput, participantsInput, durationInput;
    DatePicker datePicker;
    SwitchCompat parkingSwitch;
    Spinner difficultySpinner;
    CheckBox satelliteCB, polesCB, headlampCB, waterCB, gpsCB, batteryCB, otherCB;
    ImageButton deleteBtn, backBtn;
    Button saveBtn, doneBtn, observationBtn;
    MHikeDatabase db;
    ExecutorService executors = Executors.newSingleThreadExecutor();
    Helper helper;


    //Observation View
    ConstraintLayout observationInputArea;
    LinearLayout addedObservationArea;
    Spinner observationTypeSpinner;
    EditText dateInput, timeInput, commentInput;
    TextView observationType, observationDateTime, observationTitle;
    ImageView imageView;
    private boolean new_observation = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(this.getArguments() != null)
            hikeId = this.getArguments().getInt("hike_id");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_hike, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Titles
        hikeTitle = requireView().findViewById(R.id.hikeTitle);
        observationTitle = requireView().findViewById(R.id.observationTitle);
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
        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<>(requireContext().getApplicationContext(), R.layout.drop_down_item, getResources().getStringArray(R.array.difficulty));
        difficultySpinner.setAdapter(difficultyAdapter);
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
        doneBtn = requireView().findViewById(R.id.doneBtn);
        observationBtn = requireView().findViewById(R.id.observationBtn);
        deleteBtn = requireView().findViewById(R.id.deleteBtn);
        backBtn = requireView().findViewById(R.id.backBtn);
        //Database
        db = MHikeDatabase.getInstance(requireActivity().getApplicationContext());
        helper = new Helper(getParentFragmentManager());
        //Area for observation
        observationInputArea = requireView().findViewById(R.id.observationInputArea);
        addedObservationArea = requireView().findViewById(R.id.addedObservationArea);
        //Layout for observation
        View observationInputView = getLayoutInflater().inflate(R.layout.observation_input_layout, observationInputArea, false);
        //Observation input
        observationTypeSpinner = observationInputView.findViewById(R.id.observationTypeSpinner);
        dateInput = observationInputView.findViewById(R.id.dateInput);
        timeInput = observationInputView.findViewById(R.id.timeInput);
        Button pickDateBtn = observationInputView.findViewById(R.id.pickDateBtn);
        Button pickTimeBtn = observationInputView.findViewById(R.id.pickTimeBtn);
        commentInput = observationInputView.findViewById(R.id.commentInput);
        imageView = observationInputView.findViewById(R.id.imageView);

        //Show other EditText
        otherCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                otherEquipmentInputLayout.setVisibility(View.VISIBLE);
            } else {
                otherEquipmentInputLayout.setVisibility(View.GONE);
            }
        });

        //Insert
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
                if (equipments.length() == 0) {
                    Toast.makeText(requireContext(), "Please specify equipments for the hike", Toast.LENGTH_SHORT).show();
                } else if (equipments.length() > 1) {
                    equipments = equipments.substring(0, equipments.length() - 1);
                    int participants = Integer.parseInt(Objects.requireNonNull(participantsInput.getText()).toString());
                    double duration = Double.parseDouble(Objects.requireNonNull(durationInput.getText()).toString());
                    Hike hike = new Hike(name, location, date, parking, length, difficulty, description, equipments, participants, duration);
                    try{
                        insertHike(hike);
                        Toast.makeText(requireContext(), "Successfully saved " + hike.getHikeName(), Toast.LENGTH_SHORT).show();
                        observationBtn.setVisibility(View.VISIBLE);
                        saveBtn.setVisibility(View.INVISIBLE);
                    } catch (Exception exception) {
                        Toast.makeText(requireContext(), "Error saving " + hike.getHikeName(), Toast.LENGTH_SHORT).show();
                    }
                }
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

        //Done
        doneBtn.setOnClickListener(v -> helper.replaceFragment(new HomeFragment(), null, "home"));

        //Delete
        deleteBtn.setOnClickListener(this::showPopup);

        //Back
        backBtn.setOnClickListener(v -> helper.replaceFragment(new HomeFragment(), null, "home"));

        //Populate form for update operation
        if(hikeId != 0) {
            deleteBtn.setVisibility(View.VISIBLE);
            LiveData<Hike> hike = db.hikeDao().getHikeById(hikeId);
            hike.observe(getViewLifecycleOwner(), hike1 -> {
                this.hike1 = hike1;
                hikeTitle.setText(String.format(getResources().getString(R.string.update2), hike1.getHikeName()));
                nameInput.setText(hike1.getHikeName());
                locationInput.setText(hike1.getLocation());
                String[] dateStr = hike1.getDate().split("/", 3);
                datePicker.init(Integer.parseInt(dateStr[2]), Integer.parseInt(dateStr[1]) - 1, Integer.parseInt(dateStr[0]), null);
                if(hike1.getParking()) {
                    parkingSwitch.setChecked(true);
                }
                lengthInput.setText(String.format(Locale.getDefault(),"%.2f", hike1.getLength()));
                difficultySpinner.setSelection(hike1.getDifficulty() - 1);
                if(hike1.getDescription() != null)
                    descriptionInput.setText(hike1.getDescription());
                if(hike1.getEquipments() != null) {
                    String[] eq = hike1.getEquipments().split(",", 0);
                    List<String> equipment = Arrays.stream(eq).collect(Collectors.toList());
                    if(equipment.contains("satellite")){
                        satelliteCB.setChecked(true);
                        equipment.remove(0);
                    }
                    if(equipment.contains("poles")) {
                        polesCB.setChecked(true);
                        equipment.remove(0);
                    }
                    if(equipment.contains("headlamp")){
                        headlampCB.setChecked(true);
                        equipment.remove(0);
                    }
                    if(equipment.contains("water")){
                        waterCB.setChecked(true);
                        equipment.remove(0);
                    }
                    if(equipment.contains("gps")){
                        gpsCB.setChecked(true);
                        equipment.remove(0);
                    }
                    if(equipment.contains("battery")){
                        batteryCB.setChecked(true);
                        equipment.remove(0);
                    }
                    if(equipment.size() > 0){
                        otherCB.setChecked(true);
                        StringBuilder eSB = new StringBuilder();
                        for (String extraEquipment: equipment) {
                            eSB.append(extraEquipment.trim()).append(", ");
                        }
                        String extraEquipments = eSB.substring(0, eSB.length() - 2);
                        otherEquipmentInput.setText(extraEquipments);
                    }
                }
                participantsInput.setText(String.format(Locale.getDefault(),"%d", hike1.getParticipants()));
                durationInput.setText(String.format(Locale.getDefault(),"%.2f", hike1.getDuration()));
                saveBtn.setText(R.string.update);

                saveBtn.setOnClickListener(v -> {
                    if(!isEmpty(nameInput) && !isEmpty(locationInput) && !isEmpty(lengthInput) && !isEmpty(participantsInput) && !isEmpty(durationInput)) {
                        String name = Objects.requireNonNull(nameInput.getText()).toString();
                        hike1.setHikeName(name);
                        String location = Objects.requireNonNull(locationInput.getText()).toString();
                        hike1.setLocation(location);
                        int day = datePicker.getDayOfMonth();
                        int month = datePicker.getMonth() + 1;
                        int year = datePicker.getYear();
                        String date = day + "/" + month + "/" + year;
                        hike1.setDate(date);
                        boolean parking = parkingSwitch.isChecked();
                        hike1.setParking(parking);
                        double length = Double.parseDouble(Objects.requireNonNull(lengthInput.getText()).toString());
                        hike1.setLength(length);
                        String difficultyString = difficultySpinner.getSelectedItem().toString();
                        int difficulty = Integer.parseInt(difficultyString.substring(0, 1));
                        hike1.setDifficulty(difficulty);
                        String description = "";
                        if(!isEmpty(descriptionInput))
                            description = Objects.requireNonNull(descriptionInput.getText()).toString();
                        hike1.setDescription(description);
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
                        hike1.setEquipments(equipments);
                        int participants = Integer.parseInt(Objects.requireNonNull(participantsInput.getText()).toString());
                        hike1.setParticipants(participants);
                        double duration = Double.parseDouble(Objects.requireNonNull(durationInput.getText()).toString());
                        hike1.setDuration(duration);
                        try{
                            updateHike(hike1);
                            helper.replaceFragment(new HomeFragment(), null, "home");
                            Toast.makeText(requireContext(), "Successfully updated hike " + hike1.getHikeName(), Toast.LENGTH_SHORT).show();
                        } catch (Exception exception) {
                            Toast.makeText(requireContext(), "Error updating " + hike1.getHikeName(), Toast.LENGTH_SHORT).show();
                        }
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
            });
        }

        //Add observation to hike
        observationBtn.setOnClickListener(v -> {
            if(new_observation && !isEmpty(nameInput)) {
                //Save observation
                LiveData<Hike> insertedHike = db.hikeDao().getHikeByName(Objects.requireNonNull(nameInput.getText()).toString());
                insertedHike.observe(getViewLifecycleOwner(), hike -> {
                    hikeId = hike.getHikeId();
                    String type = observationTypeSpinner.getSelectedItem().toString();
                    String dateTime = dateInput.getText().toString() + ", " + timeInput.getText().toString();
                    String comment = null;
                    if(!isEmpty(commentInput))
                        comment = commentInput.getText().toString();
                    Observation observation = new Observation(type, dateTime, comment, null, hikeId);
                    try{
                        insertObservation(observation);
                    } catch (Exception e) {
                        Toast.makeText(requireContext(), "Error adding observation", Toast.LENGTH_SHORT).show();
                    }
                    Toast.makeText(requireContext(), "Successfully saved new observation", Toast.LENGTH_SHORT).show();

                    View addedObservationItem = getLayoutInflater().inflate(R.layout.observation_added_layout, addedObservationArea, false);
                    observationType = addedObservationItem.findViewById(R.id.observationType);
                    observationDateTime = addedObservationItem.findViewById(R.id.observationDateTime);

                    //Show added observation to UI
                    observationType.setText(type);
                    observationDateTime.setText(dateTime);

                    //UI
                    observationTitle.setVisibility(View.VISIBLE);
                    observationInputArea.removeAllViews();
                    addedObservationArea.addView(addedObservationItem);
                });

                //Set up for next observation
                new_observation = false;
                observationBtn.setText(R.string.add_observation);
            } else {
                //Set spinner adapter
                ArrayAdapter<String> observationTypeAdapter = new ArrayAdapter<>(requireContext().getApplicationContext(), R.layout.drop_down_item, getResources().getStringArray(R.array.observation_type));
                observationTypeSpinner.setAdapter(observationTypeAdapter);

                //Set text current date time
                DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                dateInput.setText(LocalDateTime.now().format(dateFormatter));
                timeInput.setText(LocalDateTime.now().format(timeFormatter));

                //Setup date and time button click
                pickDateBtn.setOnClickListener(dateDialog -> {
                    DatePickerDialog dialog = new DatePickerDialog(requireContext(),
                            (datePicker, year, month, dayOfMonth) -> dateInput.setText(LocalDate.of(year, month + 1, dayOfMonth).format(dateFormatter)),
                            LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
                    dialog.show();
                });
                pickTimeBtn.setOnClickListener(timeDialog -> {
                    TimePickerDialog dialog = new TimePickerDialog(requireContext(),
                            (view1, hourOfDay, minute) -> timeInput.setText(LocalTime.of(hourOfDay, minute).format(timeFormatter)),
                            LocalTime.now().getHour(), LocalTime.now().getMinute(), true);
                    dialog.show();
                });

                //Add view to layout
                observationInputArea.addView(observationInputView);
                observationBtn.setText(R.string.save_observation);
                new_observation = true;
            }
        });
    }

    private boolean isEmpty(EditText input) {
        return input.getText().toString().trim().length() == 0;
    }

    private void insertHike(Hike hike) {
        Runnable insertHike = () -> db.hikeDao().insertHike(hike);
        executors.execute(insertHike);
    }

    private void updateHike(Hike hike) {
        Runnable updateHike = () -> db.hikeDao().updateHike(hike);
        executors.execute(updateHike);
    }

    private void deleteHike(Hike hike) {
        Runnable deleteHike = () -> db.hikeDao().deleteHike(hike);
        executors.execute(deleteHike);
    }

    private void insertObservation(Observation observation) {
        Runnable insertObservation = () -> db.observationDao().insertObservation(observation);
        executors.execute(insertObservation);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.confirmation_delete);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.yesItem) {
            deleteHike(hike1);
            helper.replaceFragment(new HomeFragment(), null, "home");
            Toast.makeText(requireContext(), "Successfully deleted hike " + hike1.getHikeName(), Toast.LENGTH_SHORT).show();
            return true;
        } else return item.getItemId() == R.id.noItem;
    }
}