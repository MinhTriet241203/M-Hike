package com.example.m_hike;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class Helper {

    private FragmentManager fragManager;

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
}
