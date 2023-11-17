package com.example.m_hike;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
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

public class HikeFragment extends Fragment implements androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener, LocationListener {
    //Hike Info
    int hikeId = 0;
    int observationId = 0;
    Hike hike;
    TextView hikeTitle;
    TextInputLayout nameInputLayout, locationInputLayout, lengthInputLayout, descriptionInputLayout, otherEquipmentInputLayout, participantsInputLayout, durationInputLayout;
    TextInputEditText nameInput, locationInput, lengthInput, descriptionInput, otherEquipmentInput, participantsInput, durationInput;
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
    LinearLayout addedObservationArea, observationBackground;
    Spinner observationTypeSpinner;
    EditText dateInput, timeInput, commentInput, observationLocationInput;
    TextView observationType, observationDateTime, observationTitle, observationLocation;
    ImageView imageView, observationImage;
    ImageButton deleteObservationBtn;
    ActivityResultLauncher<Intent> resultLauncher;
    Bitmap imageBitmap;
    View observationInputView;
    Button pickDateBtn, pickTimeBtn, pickNewLocationBtn;
    private boolean new_observation = false, image_set = false;
    DateTimeFormatter dateFormatter, timeFormatter;
    ArrayAdapter<String> observationTypeAdapter;
    LocationManager locationManager;
    double latitude, longitude;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (this.getArguments() != null) hikeId = this.getArguments().getInt("hike_id");
        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            try {
                Uri imageURI = Objects.requireNonNull(result.getData()).getData();
                imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().getContentResolver(), Objects.requireNonNull(imageURI)));
                imageView.setImageBitmap(imageBitmap);
                image_set = true;
            } catch (Exception exception) {
                Toast.makeText(requireContext(), "Error converting image to Bitmap", Toast.LENGTH_SHORT).show();
            }
        });
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
        observationLocationInput = observationInputView.findViewById(R.id.locationInput);
        pickNewLocationBtn = observationInputView.findViewById(R.id.pickNewLocationBtn);
        dateFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        observationTypeAdapter = new ArrayAdapter<>(requireContext().getApplicationContext(), R.layout.drop_down_item, getResources().getStringArray(R.array.observation_type));

        //Show other EditText
        otherCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
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
        if (hikeId != 0) {
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
                if (this.hike.getParking()) {
                    parkingSwitch.setChecked(true);
                }
                lengthInput.setText(String.format(Locale.getDefault(), "%.2f", this.hike.getLength()));
                difficultySpinner.setSelection(this.hike.getDifficulty() - 1);
                if (this.hike.getDescription() != null)
                    descriptionInput.setText(this.hike.getDescription());
                String[] eq = Objects.requireNonNull(this.hike.getEquipments()).split(",", 0);
                List<String> equipment = Arrays.stream(eq).collect(Collectors.toList());
                if (equipment.contains("satellite")) {
                    satelliteCB.setChecked(true);
                    equipment.remove(0);
                }
                if (equipment.contains("poles")) {
                    polesCB.setChecked(true);
                    equipment.remove(0);
                }
                if (equipment.contains("headlamp")) {
                    headlampCB.setChecked(true);
                    equipment.remove(0);
                }
                if (equipment.contains("water")) {
                    waterCB.setChecked(true);
                    equipment.remove(0);
                }
                if (equipment.contains("gps")) {
                    gpsCB.setChecked(true);
                    equipment.remove(0);
                }
                if (equipment.contains("battery")) {
                    batteryCB.setChecked(true);
                    equipment.remove(0);
                }
                if (equipment.size() > 0) {
                    otherCB.setChecked(true);
                    StringBuilder eSB = new StringBuilder();
                    for (String extraEquipment : equipment) {
                        eSB.append(extraEquipment.trim()).append(", ");
                    }
                    String extraEquipments = eSB.substring(0, eSB.length() - 2);
                    otherEquipmentInput.setText(extraEquipments);
                }
                participantsInput.setText(String.format(Locale.getDefault(), "%d", this.hike.getParticipants()));
                durationInput.setText(String.format(Locale.getDefault(), "%.2f", this.hike.getDuration()));
            });
            //Show observations
            LiveData<List<Observation>> observationsList = db.observationDao().getObservationsForHike(hikeId);
            observationsList.observe(getViewLifecycleOwner(), observations -> {
                addedObservationArea.removeAllViews();
                for (Observation observation : observations) {
                    if (observation.getObservationImage() != null) {
                        View addedObservationImageItem = getLayoutInflater().inflate(R.layout.observation_added_image_layout, addedObservationArea, false);
                        observationType = addedObservationImageItem.findViewById(R.id.observationType);
                        observationDateTime = addedObservationImageItem.findViewById(R.id.observationDateTime);
                        deleteObservationBtn = addedObservationImageItem.findViewById(R.id.deleteObservationBtn);
                        observationImage = addedObservationImageItem.findViewById(R.id.observationImage);
                        observationLocation = addedObservationImageItem.findViewById(R.id.observationLocation);
                        observationBackground = addedObservationImageItem.findViewById(R.id.observationBackground);

                        observationType.setText(observation.getObservationType());
                        observationDateTime.setText(String.format("at %s", observation.getObservationTime()));
                        observationLocation.setText(String.format("on %s", getLocationFromLatLong(observation.getObservationLatitude(), observation.getObservationLongitude())));
                        observationImage.setImageBitmap(loadFromInternalStorage(observation.getObservationImage()));
                        deleteObservationBtn.setOnClickListener(v1 -> {
                            deleteObservation(observation);
                            File imageFile = new File(requireContext().getFilesDir(), observation.getObservationImage());
                            imageFile.delete();
                            addedObservationArea.removeView(addedObservationImageItem);
                            observationInputArea.removeAllViews();
                            observationBtn.setText(R.string.add_observation);
                            observationBtn.setOnClickListener(v -> setUpObservationInput());
                        });
                        observationBackground.setOnClickListener(v -> updateObservation(observation));
                        addedObservationArea.addView(addedObservationImageItem);
                    } else {
                        View addedObservationItem = getLayoutInflater().inflate(R.layout.observation_added_layout, addedObservationArea, false);
                        observationType = addedObservationItem.findViewById(R.id.observationType);
                        observationDateTime = addedObservationItem.findViewById(R.id.observationDateTime);
                        deleteObservationBtn = addedObservationItem.findViewById(R.id.deleteObservationBtn);
                        observationLocation = addedObservationItem.findViewById(R.id.observationLocation);
                        observationBackground = addedObservationItem.findViewById(R.id.observationBackground);

                        observationType.setText(observation.getObservationType());
                        observationDateTime.setText(String.format("at %s", observation.getObservationTime().trim()));
                        observationLocation.setText(String.format("on %s", getLocationFromLatLong(observation.getObservationLatitude(), observation.getObservationLongitude())));
                        deleteObservationBtn.setOnClickListener(v1 -> {
                            deleteObservation(observation);
                            addedObservationArea.removeView(addedObservationItem);
                            observationInputArea.removeAllViews();
                            observationBtn.setText(R.string.add_observation);
                            observationBtn.setOnClickListener(v -> setUpObservationInput());
                        });
                        observationBackground.setOnClickListener(v -> updateObservation(observation));
                        addedObservationArea.addView(addedObservationItem);
                    }
                }
            });
        }

        //Add observation to hike
        observationBtn.setOnClickListener(v -> setUpObservationInput());
    }

    private void setUpSaveOrUpdateHike(String operation) {
        if (!isEmpty(nameInput) && !isEmpty(locationInput) && !isEmpty(lengthInput) && !isEmpty(participantsInput) && !isEmpty(durationInput)) {
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
            if (!isEmpty(descriptionInput))
                description = Objects.requireNonNull(descriptionInput.getText()).toString().trim();
            StringBuilder equipmentsBuilder = new StringBuilder();
            if (satelliteCB.isChecked()) equipmentsBuilder.append("satellite,");
            if (polesCB.isChecked()) equipmentsBuilder.append("poles,");
            if (headlampCB.isChecked()) equipmentsBuilder.append("headlamp,");
            if (waterCB.isChecked()) equipmentsBuilder.append("water,");
            if (gpsCB.isChecked()) equipmentsBuilder.append("gps,");
            if (batteryCB.isChecked()) equipmentsBuilder.append("battery,");
            if (otherCB.isChecked()) {
                if (isEmpty(otherEquipmentInput)) {
                    otherEquipmentInputLayout.setError("Please specify other equipments.");
                } else {
                    String[] otherEquipments = Objects.requireNonNull(otherEquipmentInput.getText()).toString().split(",");
                    for (String e : otherEquipments) {
                        if (!Objects.equals(e, "") && e != null) {
                            e = e.trim();
                            equipmentsBuilder.append(e).append(",");
                        }
                    }
                }
            }
            String equipments = equipmentsBuilder.toString();
            if (equipments.length() == 0) {
                Toast.makeText(requireContext(), "Please specify equipments for the hike", Toast.LENGTH_SHORT).show();
                return;
            }
            equipments = equipments.substring(0, equipments.length() - 1);
            int participants = Integer.parseInt(Objects.requireNonNull(participantsInput.getText()).toString().trim());
            double duration = Double.parseDouble(Objects.requireNonNull(durationInput.getText()).toString().trim());
            if (operation.equals("save")) {
                try {
                    hike = new Hike(name, location, date, parking, length, difficulty, description, equipments, participants, duration);
                    hikeId = (int) db.hikeDao().insertHike(hike);
                    hike.setHikeId(hikeId);
                    Toast.makeText(requireContext(), "Successfully saved " + hike.getHikeName() + " with id " + hikeId, Toast.LENGTH_SHORT).show();
                    observationBtn.setVisibility(View.VISIBLE);
                    saveBtn.setText(R.string.update);
                    saveBtn.setOnClickListener(v -> setUpSaveOrUpdateHike("update"));
                } catch (Exception exception) {
                    Toast.makeText(requireContext(), hike.getHikeName() + " already exists", Toast.LENGTH_SHORT).show();
                }
            } else if (operation.equals("update")) {
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

                try {
                    updateHike(this.hike);
                    Toast.makeText(requireContext(), "Successfully updated hike " + this.hike.getHikeName(), Toast.LENGTH_SHORT).show();
                } catch (Exception exception) {
                    Toast.makeText(requireContext(), "Error updating " + this.hike.getHikeName(), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            if (isEmpty(nameInput)) nameInputLayout.setError("Please enter hike name");
            if (isEmpty(locationInput)) locationInputLayout.setError("Please enter hike location");
            if (isEmpty(lengthInput)) lengthInputLayout.setError("Please enter hike length");
            if (isEmpty(participantsInput))
                participantsInputLayout.setError("Please enter number of participants");
            if (isEmpty(durationInput)) durationInputLayout.setError("Please enter hike duration");
        }
    }

    private void setUpObservationInput() {
        if (new_observation && hikeId != 0) {
            //Save observation
            String comment = null, image = null;
            if (!isEmpty(commentInput))
                comment = commentInput.getText().toString();
            if (image_set)
                image = saveToInternalStorage(imageBitmap, observationTypeSpinner.getSelectedItem().toString());
            Observation observation = new Observation(observationTypeSpinner.getSelectedItem().toString(), dateInput.getText().toString() + ", " + timeInput.getText().toString(), comment, image, longitude, latitude, hikeId);
            try {
                observation.setObservationId((int) db.observationDao().insertObservation(observation));
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error saving observation", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(requireContext(), "Successfully saved observation with id " + observationId, Toast.LENGTH_SHORT).show();
            if (image_set) {
                View addedObservationImageItem = getLayoutInflater().inflate(R.layout.observation_added_image_layout, addedObservationArea, false);
                observationType = addedObservationImageItem.findViewById(R.id.observationType);
                observationDateTime = addedObservationImageItem.findViewById(R.id.observationDateTime);
                deleteObservationBtn = addedObservationImageItem.findViewById(R.id.deleteObservationBtn);
                observationImage = addedObservationImageItem.findViewById(R.id.observationImage);
                observationLocation = addedObservationImageItem.findViewById(R.id.observationLocation);
                observationBackground = addedObservationImageItem.findViewById(R.id.observationBackground);

                observationType.setText(observation.getObservationType().trim());
                observationDateTime.setText(String.format("at %s", observation.getObservationTime().trim()));
                observationLocation.setText(String.format("on %s", getLocationFromLatLong(observation.getObservationLatitude(), observation.getObservationLongitude())));
                observationImage.setImageBitmap(imageBitmap);
                deleteObservationBtn.setOnClickListener(v1 -> {
                    deleteObservation(observation);
                    File imageFile = new File(requireContext().getFilesDir(), observation.getObservationImage());
                    imageFile.delete();
                    addedObservationArea.removeView(addedObservationImageItem);
                    observationInputArea.removeAllViews();
                    observationBtn.setText(R.string.add_observation);
                    observationBtn.setOnClickListener(v -> setUpObservationInput());
                });
                observationBackground.setOnClickListener(v -> updateObservation(observation));
                addedObservationArea.addView(addedObservationImageItem);
            } else {
                View addedObservationItem = getLayoutInflater().inflate(R.layout.observation_added_layout, addedObservationArea, false);
                observationType = addedObservationItem.findViewById(R.id.observationType);
                observationDateTime = addedObservationItem.findViewById(R.id.observationDateTime);
                deleteObservationBtn = addedObservationItem.findViewById(R.id.deleteObservationBtn);
                observationLocation = addedObservationItem.findViewById(R.id.observationLocation);
                observationBackground = addedObservationItem.findViewById(R.id.observationBackground);

                observationType.setText(observation.getObservationType());
                observationDateTime.setText(String.format("at %s", observation.getObservationTime().trim()));
                observationLocation.setText(String.format("on %s", getLocationFromLatLong(observation.getObservationLatitude(), observation.getObservationLongitude())));
                deleteObservationBtn.setOnClickListener(v1 -> {
                    deleteObservation(observation);
                    addedObservationArea.removeView(addedObservationItem);
                    observationInputArea.removeAllViews();
                    observationBtn.setText(R.string.add_observation);
                    observationBtn.setOnClickListener(v -> setUpObservationInput());
                });
                observationBackground.setOnClickListener(v -> updateObservation(observation));
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
            observationTypeSpinner.setAdapter(observationTypeAdapter);

            //Set text current date time
            dateInput.setText(LocalDateTime.now().format(dateFormatter));
            timeInput.setText(LocalDateTime.now().format(timeFormatter));
            commentInput.setText("");
            //Setup date and time button click
            pickDateBtn.setOnClickListener(dateDialog -> {
                DatePickerDialog dialog = new DatePickerDialog(requireContext(), (datePicker, year, month, dayOfMonth) -> dateInput.setText(LocalDate.of(year, month + 1, dayOfMonth).format(dateFormatter)), LocalDate.now().getYear(), LocalDate.now().getMonthValue(), LocalDate.now().getDayOfMonth());
                dialog.show();
            });
            pickTimeBtn.setOnClickListener(timeDialog -> {
                TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> timeInput.setText(LocalTime.of(hourOfDay, minute).format(timeFormatter)), LocalTime.now().getHour(), LocalTime.now().getMinute(), true);
                dialog.show();
            });
            getLocation();
            pickNewLocationBtn.setOnClickListener(v -> getLocation());
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

    private String saveToInternalStorage(Bitmap bitmapImage, String observationName) {
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.yesItem) {
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
        Runnable deleteObservation = () -> db.observationDao().deleteObservation(observation);
        executors.execute(deleteObservation);
    }

    private Bitmap loadFromInternalStorage(String fileName) {
        File path = requireContext().getFilesDir();
        try {
            File imageFile = new File(path, fileName);
            return BitmapFactory.decodeStream(new FileInputStream(imageFile));
        } catch (FileNotFoundException e) {
            Toast.makeText(requireContext(), "Cannot find image", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    private int getObservationTypeIndex(String observationType) {
        switch (observationType) {
            case "Animals sighting":
                return 0;
            case "Vegetation":
                return 1;
            case "Weather condition":
                return 2;
            case "Trails condition":
                return 3;
            case "Sight":
                return 4;
        }
        return 0;
    }

    private int getMonthValue(String monthName) {
        switch (monthName) {
            case "Jan":
                return 1;
            case "Feb":
                return 2;
            case "Mar":
                return 3;
            case "Apr":
                return 4;
            case "May":
                return 5;
            case "Jun":
                return 6;
            case "Jul":
                return 7;
            case "Aug":
                return 8;
            case "Sep":
                return 9;
            case "Oct":
                return 10;
            case "Nov":
                return 11;
            case "Dec":
                return 12;
        }
        return LocalDate.now().getMonthValue();
    }

    private void updateObservation(Observation observation) {
        //Set up form to update observation
        //Set spinner adapter and selected item
        observationTypeSpinner.setAdapter(observationTypeAdapter);
        observationTypeSpinner.setSelection(getObservationTypeIndex(observation.getObservationType()));
        //Set text current date time
        String[] observationDateTime = observation.getObservationTime().split((", "));
        dateInput.setText(observationDateTime[0]);
        timeInput.setText(observationDateTime[1]);
        //Setup date and time button click
        String[] dates = observationDateTime[0].split(" ");
        String[] times = observationDateTime[1].split(":");
        pickDateBtn.setOnClickListener(dateDialog -> {
            DatePickerDialog dialog = new DatePickerDialog(requireContext(), (datePicker, year, month, dayOfMonth) -> dateInput.setText(LocalDate.of(year, month + 1, dayOfMonth).format(dateFormatter)), Integer.parseInt(dates[2]), getMonthValue(dates[1]) + 1, Integer.parseInt(dates[0]));
            dialog.show();
        });
        pickTimeBtn.setOnClickListener(timeDialog -> {
            TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view1, hourOfDay, minute) -> timeInput.setText(LocalTime.of(hourOfDay, minute).format(timeFormatter)), Integer.parseInt(times[0]), Integer.parseInt(times[1]), true);
            dialog.show();
        });
        getLocation();
        pickNewLocationBtn.setOnClickListener(v -> getLocation());
        commentInput.setText(observation.getObservationComment());
        final boolean[] update_image = new boolean[1];
        if (observation.getObservationImage() != null && !observation.getObservationImage().equals("")) {
            imageView.setImageBitmap(loadFromInternalStorage(observation.getObservationImage()));
            imageView.setOnClickListener(v12 -> {
                pickImage();
                update_image[0] = true;
            });
        } else {
            imageView.setOnClickListener(v12 -> {
                Toast.makeText(requireContext(), "Please add new observation with image", Toast.LENGTH_SHORT).show();
            });
        }
        //Add view to layout
        observationInputArea.removeAllViews();
        observationInputArea.addView(observationInputView);
        observationBtn.setText(R.string.update_observation);
        observationBtn.setOnClickListener(v1 -> {
            observation.setObservationType(observationTypeSpinner.getSelectedItem().toString());
            observation.setObservationTime(dateInput.getText().toString().trim() + ", " + timeInput.getText().toString().trim());
            String comment = null, image = null;
            if (!isEmpty(commentInput))
                observation.setObservationComment(commentInput.getText().toString());
            //If image is updated
            if (update_image[0]) {
                image = saveToInternalStorage(imageBitmap, observationTypeSpinner.getSelectedItem().toString());
                File imageFile = new File(requireContext().getFilesDir(), observation.getObservationImage());
                imageFile.delete();
                observation.setObservationImage(image);
            }
            try {
                Runnable updateObservation = () -> db.observationDao().updateObservation(observation);
                executors.execute(updateObservation);
                observationBtn.setOnClickListener(v2 -> setUpObservationInput());
                observationBtn.setText(R.string.add_observation);
                observationInputArea.removeAllViews();
                Toast.makeText(requireContext(), "Successfully updated observation " + observation.getObservationType(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(requireContext(), "Error updating observation " + observation.getObservationType(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(requireActivity(), permissions, 100);
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 5, this);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        try{
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            String address = addresses.get(0).getAddressLine(0);
            observationLocationInput.setText(address);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Cannot get address, please try again.", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLocationFromLatLong(double latitude, double longitude) {
        try{
            Geocoder geocoder = new Geocoder(requireContext(), Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Cannot get address, please try again.", Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}