package com.example.m_hike;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment implements SelectListener, androidx.appcompat.widget.PopupMenu.OnMenuItemClickListener {

    RecyclerView hikeRecycler;
    MHikeDatabase db;
    LiveData<List<Hike>> hikeList;
    Helper helper;
    Button resetBtn;
    ExecutorService executors = Executors.newSingleThreadExecutor();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        helper = new Helper(getParentFragmentManager());
        db = MHikeDatabase.getInstance(requireActivity().getApplicationContext());
        hikeList = db.hikeDao().getAllHikes();
        resetBtn = requireView().findViewById(R.id.resetBtn);

        hikeRecycler = requireView().findViewById(R.id.hikeRecycler);
        hikeRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        HikeAdapter adapter = new HikeAdapter(requireContext(), this);
        hikeRecycler.setAdapter(adapter);

        hikeList.observe(getViewLifecycleOwner(), adapter::setHikeList);

        resetBtn.setOnClickListener(this::showPopup);
    }

    @Override
    public void onItemClicked(Hike hike) {
        Bundle bundle = new Bundle();
        bundle.putInt("hike_id", hike.getHikeId());
        helper.replaceFragment(new HikeFragment(), bundle, "hike");
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(requireContext(), v);
        popup.setOnMenuItemClickListener(this::onMenuItemClick);
        popup.inflate(R.menu.confirmation_reset);
        popup.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if(item.getItemId() == R.id.yesItem) {
            resetDatabase();
            Toast.makeText(requireContext(), "Successfully reset database", Toast.LENGTH_SHORT).show();
            return true;
        } else return item.getItemId() == R.id.noItem;
    }

    private void resetDatabase() {
        Runnable resetDatabase = () -> db.hikeDao().resetDatabase();
        executors.execute(resetDatabase);
    }
}