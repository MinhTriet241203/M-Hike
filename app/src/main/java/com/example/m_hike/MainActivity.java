package com.example.m_hike;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.m_hike.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("hike");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        //Hiding the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(binding.getRoot());

        //Setup bottom navigation bar
        replaceFragment(new HomeFragment());
        binding.botNavView.setBackground(null);
        binding.botNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home) {
                replaceFragment(new HomeFragment());
            } else if (itemId == R.id.hike) {
                replaceFragment(new HikeFragment());
            } else if (itemId == R.id.account) {
                replaceFragment(new AccountFragment());
            } else if (itemId == R.id.settings) {
                replaceFragment(new SettingsFragment());
            } else {
                replaceFragment(new HomeFragment());
            }
            return true;
        });
        binding.fab.setOnClickListener(view -> showBottomDialog());

        readDatabase();
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout hike = dialog.findViewById(R.id.layoutNewHike);
        LinearLayout observation = dialog.findViewById(R.id.layoutObservation);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        hike.setOnClickListener(view -> {
            dialog.dismiss();
            replaceFragment(new HikeFragment());
        });

        observation.setOnClickListener(view -> {
            dialog.dismiss();
            Toast.makeText(MainActivity.this,"Observation Added",Toast.LENGTH_SHORT).show();
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    public void readDatabase() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                Map<String, Object> map = (Map<String, Object>) dataSnapshot.getValue();
                Log.d(TAG, "Value is: " + map);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }
}