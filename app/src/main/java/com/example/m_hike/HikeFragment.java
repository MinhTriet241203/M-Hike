package com.example.m_hike;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
    int observationId = 0;
    Hike hike;
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
    TextView observationType, observationDateTime, observationTitle, observationIdText;
    ImageView imageView, observationImage;
    ImageButton deleteObservationBtn;
    private boolean new_observation = false, image_set = false;
    ActivityResultLauncher<Intent> resultLauncher;
    Bitmap imageBitmap;
    View observationInputView;
    Button pickDateBtn;
    Button pickTimeBtn;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(this.getArguments() != null)
            hikeId = this.getArguments().getInt("hike_id");
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    try{
                        Uri imageURI = Objects.requireNonNull(result.getData()).getData();
                        imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().getContentResolver(), Objects.requireNonNull(imageURI)));
                        imageView.setImageBitmap(imageBitmap);
                        image_set = true;
                    } catch (Exception exception) {
                        Toast.makeText(requireContext(), "Error converting image to Bitmap", Toast.LENGTH_SHORT).show();
                    }
                }
        );
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
        observationInputView = getLayoutInflater().inflate(R.layout.observation_input_layout, observationInputArea, false);
        //Observation input
        observationTypeSpinner = observationInputView.findViewById(R.id.observationTypeSpinner);
        dateInput = observationInputView.findViewById(R.id.dateInput);
        timeInput = observationInputView.findViewById(R.id.timeInput);
        pickDateBtn = observationInputView.findViewById(R.id.pickDateBtn);
        pickTimeBtn = observationInputView.findViewById(R.id.pickTimeBtn);
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

        //Done
        doneBtn.setOnClickListener(v -> helper.replaceFragment(new HomeFragment(), null, "home"));

        //Delete
        deleteBtn.setOnClickListener(this::showPopup);

        //Back
        backBtn.setOnClickListener(v -> helper.replaceFragment(new HomeFragment(), null, "home"));

        //Insert hike
        saveBtn.setOnClickListener(v -> setUpSaveOrUpdateHike("save"));

        //Populate form for update operation
        if(hikeId != 0) {
            deleteBtn.setVisibility(View.VISIBLE);
            saveBtn.setText(R.string.update);
            saveBtn.setOnClickListener(v -> setUpSaveOrUpdateHike("update"));
            observationBtn.setVisibility(View.VISIBLE);
            //Set data to widgets
            LiveData<Hike> hike = db.hikeDao().getHikeById(hikeId);
            hike.observe(getViewLifecycleOwner(), updateHike -> {
                this.hike = updateHike;
                hikeTitle.setText(String.format(getResources().getString(R.string.update2), this.hike.getHikeName()));
                nameInput.setText(this.hike.getHikeName());
                locationInput.setText(this.hike.getLocation());
                String[] dateStr = this.hike.getDate().split("/", 3);
                datePicker.init(Integer.parseInt(dateStr[2]), Integer.parseInt(dateStr[1]) - 1, Integer.parseInt(dateStr[0]), null);
                if(this.hike.getParking()) {
                    parkingSwitch.setChecked(true);
                }
                lengthInput.setText(String.format(Locale.getDefault(),"%.2f", this.hike.getLength()));
                difficultySpinner.setSelection(this.hike.getDifficulty() - 1);
                if(this.hike.getDescription() != null)
                    descriptionInput.setText(this.hike.getDescription());
                String[] eq = Objects.requireNonNull(this.hike.getEquipments()).split(",", 0);
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
                participantsInput.setText(String.format(Locale.getDefault(),"%d", this.hike.getParticipants()));
                durationInput.setText(String.format(Locale.getDefault(),"%.2f", this.hike.getDuration()));
            });
            //Show observations
            LiveData<List<Observation>> observationsList = db.observationDao().getObservationsForHike(hikeId);
            observationsList.observe(getViewLifecycleOwner(), observations -> {
                addedObservationArea.removeAllViews();
                for (Observation observation:observations) {
                    if(observation.getObservationImage() != null) {
                        View addedObservationImageItem = getLayoutInflater().inflate(R.layout.observation_added_image_layout, addedObservationArea, false);
                        observationType = addedObservationImageItem.findViewById(R.id.observationType);
                        observationDateTime = addedObservationImageItem.findViewById(R.id.observationDateTime);
                        deleteObservationBtn = addedObservationImageItem.findViewById(R.id.deleteObservationBtn);
                        observationImage = addedObservationImageItem.findViewById(R.id.observationImage);
                        observationIdText = addedObservationImageItem.findViewById(R.id.observationIdText);
                        observationType.setText(observation.getObservationType());
                        observationDateTime.setText(String.format("at %s", observation.getObservationTime()));
                        observationIdText.setText(String.valueOf(observation.getObservationId()));
                        observationImage.setImageBitmap(loadFromInternalStorage(observation.getObservationImage()));
                        deleteObservationBtn.setOnClickListener(v1 -> {
                            TextView idText = addedObservationImageItem.findViewById(R.id.observationIdText);
                            Observation observationDelete = db.observationDao().getObservationById(Integer.parseInt(idText.getText().toString()));
                            deleteObservation(observationDelete);
                            addedObservationArea.removeView(addedObservationImageItem);
                        });
                        addedObservationArea.addView(addedObservationImageItem);
                    } else {
                        View addedObservationItem = getLayoutInflater().inflate(R.layout.observation_added_layout, addedObservationArea, false);
                        observationType = addedObservationItem.findViewById(R.id.observationType);
                        observationDateTime = addedObservationItem.findViewById(R.id.observationDateTime);
                        deleteObservationBtn = addedObservationItem.findViewById(R.id.deleteObservationBtn);
                        observationIdText = addedObservationItem.findViewById(R.id.observationIdText);

                        observationType.setText(observation.getObservationType());
                        observationDateTime.setText(String.format("at %s", observation.getObservationTime()));
                        observationIdText.setText(String.valueOf(observation.getObservationId()));
                        deleteObservationBtn.setOnClickListener(v1 -> {
                            TextView idText = addedObservationItem.findViewById(R.id.observationIdText);
                            Observation observationDelete = db.observationDao().getObservationById(Integer.parseInt(idText.getText().toString()));
                            deleteObservation(observationDelete);
                            addedObservationArea.removeView(addedObservationItem);
                        });
                        addedObservationArea.addView(addedObservationItem);
                    }
                }
            });
        }

        //Add observation to hike
        observationBtn.setOnClickListener(v -> setUpObservationInput());
    }

    private void setUpSaveOrUpdateHike(String operation) {
        if(!isEmpty(nameInput) && !isEmpty(locationInput) && !isEmpty(lengthInput) && !isEmpty(participantsInput) && !isEmpty(durationInput)) {
            String name = Objects.requireNonNull(nameInput.getText()).toString().trim();
            String location = Objects.requireNonNull(locationInput.getText()).toString().trim();
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1;
            int year = datePicker.getYear();
            String date = day + "/" + month + "/" + year;
            boolean parking = parkingSwitch.isChecked();
            double length = Double.parseDouble(Objects.requireNonNull(lengthInput.getText()).toString().trim());
            String difficultyString = difficultySpinner.getSelectedItem().toString().trim();
            int difficulty = Integer.parseInt(difficultyString.substring(0, 1));
            String description = "";
            if(!isEmpty(descriptionInput))
                description = Objects.requireNonNull(descriptionInput.getText()).toString().trim();
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
            if (equipments.length() == 0)
                Toast.makeText(requireContext(), "Please specify equipments for the hike", Toast.LENGTH_SHORT).show();
            equipments = equipments.substring(0, equipments.length() - 1);
            int participants = Integer.parseInt(Objects.requireNonNull(participantsInput.getText()).toString().trim());
            double duration = Double.parseDouble(Objects.requireNonNull(durationInput.getText()).toString().trim());
            if(operation.equals("save")) {
                try{
                    hike = new Hike(name, location, date, parking, length, difficulty, description, equipments, participants, duration);
                    hikeId = (int) db.hikeDao().insertHike(hike);
                    hike.setHikeId(hikeId);
                    Toast.makeText(requireContext(), "Successfully saved " + hike.getHikeName() + " with id " + hikeId, Toast.LENGTH_SHORT).show();
                    observationBtn.setVisibility(View.VISIBLE);
                    saveBtn.setText(R.string.update);
                    saveBtn.setOnClickListener(v -> setUpSaveOrUpdateHike("update"));
                } catch (Exception exception) {
                    Toast.makeText(requireContext(), "Error saving " + hike.getHikeName(), Toast.LENGTH_SHORT).show();
                }
            } else if(operation.equals("update")) {
                this.hike.setHikeName(name);
                this.hike.setLocation(location);
                this.hike.setDate(date);
                this.hike.setParking(parking);
                this.hike.setLength(length);
                this.hike.setDifficulty(difficulty);
                this.hike.setDescription(description);
                this.hike.setEquipments(equipments);
                this.hike.setParticipants(participants);
                this.hike.setDuration(duration);

                try{
                    updateHike(this.hike);
                    Toast.makeText(requireContext(), "Successfully updated hike " + this.hike.getHikeName(), Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    Toast.makeText(requireContext(), "Error updating " + this.hike.getHikeName(), Toast.LENGTH_SHORT).show();
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
    }

    private void setUpObservationInput() {
        if(new_observation && hikeId != 0) {
            //Save observation
            String type = observationTypeSpinner.getSelectedItem().toString().trim();
            String dateTime = dateInput.getText().toString().trim() + ", " + timeInput.getText().toString().trim();
            String comment = null, image = null;
            if(!isEmpty(commentInput))
                comment = commentInput.getText().toString();
            if(image_set) {
                image = saveToInternalStorage(imageBitmap, type);
            }
            Observation observation = new Observation(type, dateTime, comment, image, hikeId);
            try{
                observationId = (int) db.observationDao().insertObservation(observation);
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error saving observation", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(requireContext(), "Successfully saved observation with id " + observationId, Toast.LENGTH_SHORT).show();
            if(image_set) {
                View addedObservationImageItem = getLayoutInflater().inflate(R.layout.observation_added_image_layout, addedObservationArea, false);
                observationType = addedObservationImageItem.findViewById(R.id.observationType);
                observationDateTime = addedObservationImageItem.findViewById(R.id.observationDateTime);
                deleteObservationBtn = addedObservationImageItem.findViewById(R.id.deleteObservationBtn);
                observationImage = addedObservationImageItem.findViewById(R.id.observationImage);
                observationIdText = addedObservationImageItem.findViewById(R.id.observationIdText);

                observationType.setText(type);
                observationDateTime.setText(String.format("at %s", dateTime));
                observationIdText.setText(String.valueOf(observationId));
                observationImage.setImageBitmap(imageBitmap);
                deleteObservationBtn.setOnClickListener(v1 -> {
                    TextView idText = addedObservationImageItem.findViewById(R.id.observationIdText);
                    Observation observationDelete = db.observationDao().getObservationById(Integer.parseInt(idText.getText().toString()));
                    deleteObservation(observationDelete);
                    addedObservationArea.removeView(addedObservationImageItem);
                });
                addedObservationArea.addView(addedObservationImageItem);
            } else {
                View addedObservationItem = getLayoutInflater().inflate(R.layout.observation_added_layout, addedObservationArea, false);
                observationType = addedObservationItem.findViewById(R.id.observationType);
                observationDateTime = addedObservationItem.findViewById(R.id.observationDateTime);
                deleteObservationBtn = addedObservationItem.findViewById(R.id.deleteObservationBtn);
                observationIdText = addedObservationItem.findViewById(R.id.observationIdText);

                observationType.setText(type);
                observationDateTime.setText(String.format("at %s", dateTime));
                observationIdText.setText(String.valueOf(observationId));
                deleteObservationBtn.setOnClickListener(v1 -> {
                    TextView idText = addedObservationItem.findViewById(R.id.observationIdText);
                    Observation observationDelete = db.observationDao().getObservationById(Integer.parseInt(idText.getText().toString()));
                    deleteObservation(observationDelete);
                    addedObservationArea.removeView(addedObservationItem);
                });
                addedObservationArea.addView(addedObservationItem);
            }

            //Set up for next observation
            new_observation = false;
            image_set = false;
            observationTitle.setVisibility(View.VISIBLE);
            observationInputArea.removeAllViews();
            observationBtn.setText(R.string.add_observation);
        } else {
            //Set up to input new observation
            //Set spinner adapter
            ArrayAdapter<String> observationTypeAdapter = new ArrayAdapter<>(requireContext().getApplicationContext(),
                    R.layout.drop_down_item, getResources().getStringArray(R.array.observation_type));
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
            imageView.setImageResource(R.drawable.ic_baseline_add_photo);

            imageView.setOnClickListener(v12 -> pickImage());

            //Add view to layout
            observationInputArea.addView(observationInputView);
            observationBtn.setText(R.string.save_observation);
            new_observation = true;
        }
    }

    private boolean isEmpty(EditText input) {
        return input.getText().toString().trim().length() == 0;
    }

    private void pickImage() {
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private String saveToInternalStorage(Bitmap bitmapImage, String observationName){
        File directory = requireContext().getFilesDir();
        String fileName = observationName + "_" + System.currentTimeMillis() + ".png";
        File path = new File(directory, fileName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Error saving image", Toast.LENGTH_SHORT).show();
        } finally {
            try {
                assert fos != null;
                fos.close();
            } catch (IOException e) {
                Toast.makeText(requireContext(), "Error closing Output Stream", Toast.LENGTH_SHORT).show();
            }
        }
        return fileName;
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.confirmation_delete);
        popup.show();
    }

    @Override public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.yesItem) {
            deleteHike(hike);
            helper.replaceFragment(new HomeFragment(), null, "home");
            Toast.makeText(requireContext(), "Successfully deleted hike " + hike.getHikeName(), Toast.LENGTH_SHORT).show();
            return true;
        } else return item.getItemId() == R.id.noItem;
    }

    //Database methods
    private void updateHike(Hike hike) {
        Runnable updateHike = () -> db.hikeDao().updateHike(hike);
        executors.execute(updateHike);
    }

    private void deleteHike(Hike hike) {
        Runnable deleteHike = () -> db.hikeDao().deleteHike(hike);
        executors.execute(deleteHike);
    }

    private void deleteObservation(Observation observation) {
        Runnable deleteObservation = () -> {
            db.observationDao().deleteObservation(observation);
        };
        executors.execute(deleteObservation);
    }

    private Bitmap loadFromInternalStorage(String fileName) {
        File path = requireContext().getFilesDir();
        try {
            File imageFile = new File(path, fileName);
            return BitmapFactory.decodeStream(new FileInputStream(imageFile));
        }
        catch (FileNotFoundException e)
        {
            Toast.makeText(requireContext(), "Cannot find image", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}