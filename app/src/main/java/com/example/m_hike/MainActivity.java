package com.example.m_hike;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.m_hike.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    Helper helper = new Helper(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        //Hiding the title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(getSupportActionBar()).hide();
        setContentView(binding.getRoot());

        //Setup bottom navigation bar
        helper.replaceFragment(new HomeFragment(), null, "home");
        binding.botNavView.setBackground(null);
        //Change fragment
        binding.botNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.home) {
                helper.replaceFragment(new HomeFragment(), null, "home");
            } else if (itemId == R.id.hike) {
                helper.replaceFragment(new HikeFragment(), null, "hike");
            } else if (itemId == R.id.account) {
                helper.replaceFragment(new AccountFragment(), null, "account");
            } else if (itemId == R.id.settings) {
                helper.replaceFragment(new SettingsFragment(), null, "settings");
            } else {
                helper.replaceFragment(new HomeFragment(), null, "home");
            }
            return true;
        });
        binding.fab.setOnClickListener(view -> showBottomDialog());

        //Back button
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int count = helper.getFragManager().getBackStackEntryCount();
                if(count == 0) {
                    Toast.makeText(MainActivity.this, "No previous fragment to go back", Toast.LENGTH_SHORT).show();
                } else {
                    String lastFragment = helper.getFragManager().getBackStackEntryAt(count-2).getName();
                    helper.getFragManager().popBackStack();
                    switch (Objects.requireNonNull(lastFragment)){
                        case "home":
                            helper.replaceFragment(new HomeFragment(), null, "home");
                            binding.botNavView.setSelectedItemId(R.id.home);
                            break;
                        case "hike":
                            helper.replaceFragment(new HikeFragment(), null, "hike");
                            binding.botNavView.setSelectedItemId(R.id.hike);
                            break;
                        case "account":
                            helper.replaceFragment(new AccountFragment(), null, "account");
                            binding.botNavView.setSelectedItemId(R.id.account);
                            break;
                        case "settings":
                            helper.replaceFragment(new SettingsFragment(), null, "settings");
                            binding.botNavView.setSelectedItemId(R.id.settings);
                            break;
                        default:
                            helper.replaceFragment(new HomeFragment(), null, "home");
                            binding.botNavView.setSelectedItemId(R.id.home);
                            break;
                    }
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);
    }

    private void showBottomDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottom_sheet_layout);

        LinearLayout hike = dialog.findViewById(R.id.layoutNewHike);
        ImageView cancelButton = dialog.findViewById(R.id.cancelButton);

        hike.setOnClickListener(view -> {
            dialog.dismiss();
            helper.replaceFragment(new HikeFragment(), null, "hike");
        });

        cancelButton.setOnClickListener(view -> dialog.dismiss());

        Objects.requireNonNull(dialog.getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.);
        assert fragment != null;
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}