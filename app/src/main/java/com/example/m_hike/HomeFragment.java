package com.example.m_hike;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

public class HomeFragment extends Fragment implements SelectListener {

    RecyclerView hikeRecycler;
    MHikeDatabase db;
    LiveData<List<Hike>> hikeList;
    Helper helper;

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

        hikeRecycler = requireView().findViewById(R.id.hikeRecycler);
        hikeRecycler.setLayoutManager(new LinearLayoutManager(requireContext()));
        HikeAdapter adapter = new HikeAdapter(requireContext(), this);
        hikeRecycler.setAdapter(adapter);

        hikeList.observe(getViewLifecycleOwner(), adapter::setHikeList);

    }

    @Override
    public void onItemClicked(Hike hike) {
        Bundle bundle = new Bundle();
        bundle.putString("hike_name", hike.getHikeName());
        bundle.putString("hike_location", hike.getLocation());
        bundle.putString("hike_date", hike.getDate());
        helper.replaceFragment(new HikeFragment(), bundle, "hike");
    }
}