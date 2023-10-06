package com.example.m_hike;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class Helper {

    private FragmentManager fragManager;
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference ref = db.getReference("hike");
    public FragmentManager getFragManager() {
        return fragManager;
    }
    public void setFragManager(FragmentManager fragManager) {
        this.fragManager = fragManager;
    }

    //Constructor
    public Helper(FragmentManager fragManager) {
        this.fragManager = fragManager;
    }

    public void replaceFragment(Fragment fragment, @Nullable Bundle bundle, String backStack) {
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        fragmentTransaction.setReorderingAllowed(true).replace(R.id.frameLayout, fragment.getClass(), bundle).addToBackStack(backStack);
        fragmentTransaction.commit();
    }

    public void readDatabase() {
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String ma = Objects.requireNonNull(dataSnapshot.getValue()).toString();
                Log.d(TAG, "Value is: " + ma);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Failed to read value
                Log.d(TAG, "Failed to read value.", error.toException());
            }
        });
    }


}
